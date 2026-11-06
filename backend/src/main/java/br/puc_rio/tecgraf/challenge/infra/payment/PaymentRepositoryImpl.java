package br.puc_rio.tecgraf.challenge.infra.payment;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.YearMonth;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.payment.Payment;
import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.EmployeePayment;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;

// Escolhi usar o JdbcClient para facilitar o uso de query nativa em projeção e insert em batch com ON CONFLICT e retorno de quantidade
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {
    private final JdbcClient jdbcClient;
    private final PayeeMapper mapper;

    @Override
    public Page<EmployeePayment> findByCriteria(Criteria criteria, Page.Range range) {
        // A construção dos critérios dessa maneira evita ter que criar vários ifs
        final var FILTERS = """
            (:employeeId IS NULL OR p.id_employee = :employeeId)
            AND (:year IS NULL OR p.year = :year)
            AND (:month IS NULL OR p.month = :month)
        """;

        final var SQL = """
            SELECT
                p.id_employee AS employee_id,
                e.first_name,
                e.last_name,
                p.month,
                p.year,
                p.payment AS amount
            FROM payment p
            JOIN employee e ON e.id_social_number = p.id_employee
            WHERE %s
            ORDER BY p.year DESC, p.month DESC, e.first_name, e.last_name, p.id_employee
            LIMIT :limit OFFSET :offset
        """.formatted(FILTERS);

        final var COUNT_SQL = "SELECT COUNT(*) FROM payment p WHERE %s".formatted(FILTERS);

        final var employeeId = criteria.employeeId() == null ? null : criteria.employeeId().getNumber();
        final var period = criteria.payrollPeriod();
        final var year = period == null ? null : period.getYear();
        final var month = period == null ? null : period.getMonthValue();

        // Somente os parâmetros não obrigatórios precisam de especificar o tipo para o banco entender quando forem nulos
        final var items = jdbcClient.sql(SQL)
            .param("employeeId", employeeId, Types.VARCHAR)
            .param("year", year, Types.INTEGER)
            .param("month", month, Types.INTEGER)
            .param("limit", range.limit())
            .param("offset", range.offset())
            .query(EmployeePayment.class)
            .list();

        var total = jdbcClient.sql(COUNT_SQL)
            .param("employeeId", employeeId, Types.VARCHAR)
            .param("year", year, Types.INTEGER)
            .param("month", month, Types.INTEGER)
            .query(Long.class)
            .single();

        return new Page<>(items, range, total);
    }

    @Override
    public List<Payee> findPendingPayeesOn(YearMonth payrollPeriod) {
        final var SQL = """
            SELECT
                e.id_social_number AS employee_id,
                e.type,
                e.birth_date,
                e.gender,
                e.base_salary,
                COUNT(DISTINCT CAST(a.entrance AS DATE)) AS weekend_days_worked
            FROM employee e
            LEFT JOIN payment p ON p.id_employee = e.id_social_number AND p.year = :year AND p.month = :month
            LEFT JOIN attendance a ON a.id_employee = e.id_social_number AND a.entrance >= :start AND a.entrance < :end
                AND EXTRACT(DOW FROM a.entrance) IN (0, 6) -- 0 = domingo, 6 = sábado
            WHERE e.active
            AND p.id_employee IS NULL
            GROUP BY e.id_social_number
            ORDER BY e.id_social_number
        """;

        var payees = jdbcClient.sql(SQL)
            .param("start", payrollPeriod.atDay(1).atStartOfDay())
            .param("end", payrollPeriod.plusMonths(1).atDay(1).atStartOfDay())
            .param("year", payrollPeriod.getYear())
            .param("month", payrollPeriod.getMonthValue())
            .query(PayeeProjection.class)
            .list();

        return payees.stream().map(mapper::toDomain).toList();
    }

    // Usa técnica de inserção em lote com arrays.
    // Retorna quantos registros foram efetivamente inseridos, desconsidera o que já existia
    @Override
    public int insertPayments(List<Payment> payments) {
        final var SQL = """
            INSERT INTO payment (id_employee, month, year, payment)
            SELECT *
            FROM unnest(
                CAST(:employeeIds AS VARCHAR[]),
                CAST(:months AS INTEGER[]),
                CAST(:years AS INTEGER[]),
                CAST(:amounts AS NUMERIC[])
            )
            ON CONFLICT (id_employee, month, year) DO NOTHING
        """;

        return jdbcClient.sql(SQL)
            .param("employeeIds", payments.stream().map(payment -> payment.getEmployeeId().getNumber()).toArray(String[]::new))
            .param("months", payments.stream().map(payment -> payment.getPeriod().getMonthValue()).toArray(Integer[]::new))
            .param("years", payments.stream().map(payment -> payment.getPeriod().getYear()).toArray(Integer[]::new))
            .param("amounts", payments.stream().map(Payment::getAmount).toArray(BigDecimal[]::new))
            .update();
    }
}
