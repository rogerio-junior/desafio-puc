package br.puc_rio.tecgraf.challenge.infra.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.payment.Payment;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentInfo;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentRepository;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentRepository.Criteria;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.FixedTermEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.InternEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.PermanentEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.ScholarEmployee;

// Usa o Postgres local. Cada teste cria os próprios dados e sofre rollback ao final.
@SpringBootTest
@Transactional
class PaymentRepositoryImplTest {
    private static final YearMonth OCTOBER = YearMonth.of(2026, 10);
    private static final YearMonth UNUSED_PERIOD = YearMonth.of(1999, 1); // Sem pagamentos no seed, isola as fixtures

    private static final String PERMANENT = "90000000001";
    private static final String FIXED_TERM = "90000000002";
    private static final String INTERN = "90000000003";
    private static final String SCHOLAR = "90000000004";
    private static final String ALREADY_PAID = "90000000005";
    private static final String PAID_IN_OTHER_MONTH = "90000000006";
    private static final String INACTIVE = "90000000007";

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void findsActiveEmployeesWithoutPaymentMappedToTheirType() {
        insertEmployee(PERMANENT, "A", true);
        insertEmployee(FIXED_TERM, "B", true);
        insertEmployee(INTERN, "C", true);
        insertEmployee(SCHOLAR, "D", true);

        var found = findFixturesPendingOn(OCTOBER);

        assertThat(found.get(PERMANENT)).isInstanceOf(PermanentEmployee.class);
        assertThat(found.get(FIXED_TERM)).isInstanceOf(FixedTermEmployee.class);
        assertThat(found.get(INTERN)).isInstanceOf(InternEmployee.class);
        assertThat(found.get(SCHOLAR)).isInstanceOf(ScholarEmployee.class);

        var intern = found.get(INTERN);
        assertThat(intern.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(intern.getGender()).isEqualTo(Gender.F);
        assertThat(intern.getBaseSalary()).isEqualByComparingTo("1234.56");
        assertThat(intern.getWorkedWeekendDays()).isZero();
    }

    @Test
    void skipsInactiveAndAlreadyPaidEmployees() {
        insertEmployee(ALREADY_PAID, "A", true);
        insertPayment(ALREADY_PAID, 10, 2026, "1000.00");
        insertEmployee(PAID_IN_OTHER_MONTH, "A", true);
        insertPayment(PAID_IN_OTHER_MONTH, 10, 2025, "1000.00");
        insertEmployee(INACTIVE, "A", false);

        assertThat(findFixturesPendingOn(OCTOBER)).containsOnlyKeys(PAID_IN_OTHER_MONTH);
    }

    @Test
    void countsDistinctWeekendDaysWithinTheMonth() {
        insertEmployee(INTERN, "C", true);
        insertAttendance(INTERN,
            "2026-09-27 23:59:59", // domingo do mês anterior
            "2026-10-03 00:00:00", // sábado
            "2026-10-05 09:00:00", // segunda
            "2026-10-17 08:00:00", // sábado
            "2026-10-17 13:30:00", // segunda entrada no mesmo sábado
            "2026-10-31 23:59:59", // sábado, último instante do mês
            "2026-11-01 00:00:00"); // domingo do mês seguinte

        assertThat(findFixturesPendingOn(OCTOBER).get(INTERN).getWorkedWeekendDays()).isEqualTo(3);
    }

    @Test
    void insertsPaymentsAndReturnsTheCount() {
        insertEmployee(PERMANENT, "A", true);
        insertEmployee(INTERN, "C", true);

        var inserted = repository.insertPayments(List.of(
            payment(PERMANENT, "3090.00"),
            payment(INTERN, "1360.00")));

        assertThat(inserted).isEqualTo(2);
        assertThat(storedPayment(PERMANENT)).isEqualByComparingTo("3090.00");
        assertThat(storedPayment(INTERN)).isEqualByComparingTo("1360.00");
    }

    @Test
    void neverOverwritesAnExistingPayment() {
        insertEmployee(ALREADY_PAID, "A", true);
        insertEmployee(PERMANENT, "A", true);
        insertPayment(ALREADY_PAID, 10, 2026, "1000.00");

        var inserted = repository.insertPayments(List.of(
            payment(ALREADY_PAID, "2000.00"),
            payment(PERMANENT, "3000.00")));

        assertThat(inserted).isEqualTo(1);
        assertThat(storedPayment(ALREADY_PAID)).isEqualByComparingTo("1000.00");
    }

    @Test
    void insertingNothingReturnsZero() {
        assertThat(repository.insertPayments(List.of())).isZero();
    }

    @Test
    void ordersPaymentsAlphabeticallyAndMapsTheirFields() {
        insertEmployee(PERMANENT, "Zeca", "Silva");
        insertEmployee(FIXED_TERM, "Ágata", "Souza");
        insertEmployee(INTERN, "ana", "Lima");
        insertEmployee(SCHOLAR, "Ana", "Alves");
        insertPayment(PERMANENT, 1, 1999, "1000.00");
        insertPayment(FIXED_TERM, 1, 1999, "2000.00");
        insertPayment(INTERN, 1, 1999, "3000.00");
        insertPayment(SCHOLAR, 1, 1999, "4000.00");

        var page = repository.findByCriteria(new Criteria(null, UNUSED_PERIOD), new Page.Cursor(null, 10));

        assertThat(page.items()).extracting(PaymentInfo::idEmployee)
            .containsExactly(FIXED_TERM, INTERN, SCHOLAR, PERMANENT);
        assertThat(page.total()).isEqualTo(4);

        var first = page.items().getFirst();
        assertThat(first.firstName()).isEqualTo("Ágata");
        assertThat(first.lastName()).isEqualTo("Souza");
        assertThat(first.month()).isEqualTo(1);
        assertThat(first.year()).isEqualTo(1999);
        assertThat(first.salary()).isEqualByComparingTo("2000.00");
    }

    @Test
    void walksThroughAllPagesWithTheCursorIncludingHomonyms() {
        insertEmployee(PERMANENT, "Zeca", "Silva");
        insertEmployee(FIXED_TERM, "Ana", "Lima");
        insertEmployee(INTERN, "Ana", "Lima");
        insertPayment(PERMANENT, 1, 1999, "1000.00");
        insertPayment(FIXED_TERM, 1, 1999, "1000.00");
        insertPayment(INTERN, 1, 1999, "1000.00");
        var criteria = new Criteria(null, UNUSED_PERIOD);

        var firstPage = repository.findByCriteria(criteria, new Page.Cursor(null, 2));
        var secondPage = repository.findByCriteria(criteria, firstPage.nexCursor(PaymentInfo::cursorKey));
        var thirdPage = repository.findByCriteria(criteria, secondPage.nexCursor(PaymentInfo::cursorKey));

        assertThat(firstPage.items()).extracting(PaymentInfo::idEmployee).containsExactly(FIXED_TERM, INTERN);
        assertThat(secondPage.items()).extracting(PaymentInfo::idEmployee).containsExactly(PERMANENT);
        assertThat(thirdPage.items()).isEmpty();
        assertThat(List.of(firstPage.total(), secondPage.total(), thirdPage.total())).containsOnly(3L);
    }

    @Test
    void paginatesPaymentsOfTheSameEmployeeFromMostRecentToOldest() {
        insertEmployee(PERMANENT, "Ana", "Lima");
        insertEmployee(INTERN, "Bruno", "Lima");
        insertPayment(PERMANENT, 12, 1998, "1000.00");
        insertPayment(PERMANENT, 2, 1999, "2000.00");
        insertPayment(PERMANENT, 1, 1999, "3000.00");
        insertPayment(INTERN, 3, 1999, "4000.00");
        var criteria = new Criteria(SocialNumber.of(PERMANENT), null);

        var firstPage = repository.findByCriteria(criteria, new Page.Cursor(null, 2));
        var secondPage = repository.findByCriteria(criteria, firstPage.nexCursor(PaymentInfo::cursorKey));

        assertThat(firstPage.items()).extracting(PaymentInfo::cursorKey)
            .containsExactly(PERMANENT + ":1999:2", PERMANENT + ":1999:1");
        assertThat(secondPage.items()).extracting(PaymentInfo::cursorKey)
            .containsExactly(PERMANENT + ":1998:12");
        assertThat(firstPage.total()).isEqualTo(3);
    }

    @Test
    void combinesEmployeeAndPeriodFilters() {
        insertEmployee(PERMANENT, "Ana", "Lima");
        insertEmployee(INTERN, "Bruno", "Lima");
        insertPayment(PERMANENT, 1, 1999, "1000.00");
        insertPayment(PERMANENT, 2, 1999, "2000.00");
        insertPayment(INTERN, 1, 1999, "3000.00");

        var page = repository.findByCriteria(
            new Criteria(SocialNumber.of(PERMANENT), UNUSED_PERIOD), new Page.Cursor(null, 10));

        assertThat(page.items()).extracting(PaymentInfo::cursorKey).containsExactly(PERMANENT + ":1999:1");
        assertThat(page.total()).isEqualTo(1);
    }

    @Test
    void rejectsMalformedCursor() {
        var criteria = new Criteria(null, UNUSED_PERIOD);

        // O @Repository traduz o IllegalArgumentException lançado na implementação
        assertThatThrownBy(() -> repository.findByCriteria(criteria, new Page.Cursor("invalid", 10)))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
        assertThatThrownBy(() -> repository.findByCriteria(criteria, new Page.Cursor(PERMANENT + ":a:b", 10)))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    private Map<String, PaymentEmployee> findFixturesPendingOn(YearMonth yearMonth) {
        return repository.findPendingsOn(yearMonth).stream()
            .filter(employee -> employee.getSocialNumber().getNumber().startsWith("9000000000"))
            .collect(Collectors.toMap(employee -> employee.getSocialNumber().getNumber(), Function.identity()));
    }

    private Payment payment(String socialNumber, String amount) {
        return new Payment(SocialNumber.of(socialNumber), OCTOBER, new BigDecimal(amount));
    }

    private BigDecimal storedPayment(String socialNumber) {
        return jdbc.queryForObject(
            "SELECT payment FROM payment WHERE id_employee = ? AND month = 10 AND year = 2026",
            BigDecimal.class, socialNumber);
    }

    private void insertEmployee(String socialNumber, String type, boolean active) {
        jdbc.update("""
            INSERT INTO employee (id_social_number, first_name, last_name, birth_date, gender, active, base_salary, type)
            VALUES (?, 'Teste', 'Repositório', DATE '1990-05-20', 'F', ?, 1234.56, ?)
            """, socialNumber, active, type);
    }

    private void insertEmployee(String socialNumber, String firstName, String lastName) {
        jdbc.update("""
            INSERT INTO employee (id_social_number, first_name, last_name, birth_date, gender, active, base_salary, type)
            VALUES (?, ?, ?, DATE '1990-05-20', 'F', true, 1234.56, 'A')
            """, socialNumber, firstName, lastName);
    }

    private void insertPayment(String socialNumber, int month, int year, String amount) {
        jdbc.update("INSERT INTO payment (id_employee, month, year, payment) VALUES (?, ?, ?, ?)",
            socialNumber, month, year, new BigDecimal(amount));
    }

    private void insertAttendance(String socialNumber, String... entrances) {
        for (var entrance : entrances) {
            jdbc.update("INSERT INTO attendance (id_employee, entrance) VALUES (?, CAST(? AS TIMESTAMP))",
                socialNumber, entrance);
        }
    }
}
