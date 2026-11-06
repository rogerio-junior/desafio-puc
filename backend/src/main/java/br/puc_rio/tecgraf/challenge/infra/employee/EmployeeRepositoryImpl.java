package br.puc_rio.tecgraf.challenge.infra.employee;

import java.sql.Types;
import java.util.Optional;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.Employee;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeCep;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeRepository;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {
    // Mapeia as colunas direto para o EmployeeCep, mas precisa de mapeamento extra
    // para traduzir o EmployeeType.
    private static final RowMapper<EmployeeCep> EMPLOYEE_CEP_MAPPER = employeeCepMapper();

    // Usei um JpaRepository aqui para ficar mais fácil de inserir 
    // funcionários e fazer CRUD, caso dê tempo de criar Employees
    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeMapper mapper;
    private final JdbcClient jdbcClient;

    @Override
    public Optional<Employee> findById(SocialNumber id) {
        if (id == null) {
            return Optional.empty();
        }

        return jpaRepository.findById(id.getNumber()).map(mapper::toDomain);
    }

    @Override
    public Page<EmployeeCep> findByActiveAndCep(boolean active, String cep, Page.Range range) {
        final var FILTERS = """
            e.active = :active
            AND (:cep IS NULL OR extract_cep(e.address) = :cep)
        """;

        final var SQL = """
            SELECT
                e.id_social_number AS employee_id,
                e.type,
                e.first_name,
                e.last_name,
                e.birth_date,
                e.gender,
                e.base_salary,
                extract_cep(e.address) AS cep,
                e.active
            FROM employee e
            WHERE %s
            ORDER BY cep NULLS LAST, e.first_name, e.last_name, e.id_social_number
            LIMIT :limit OFFSET :offset
        """.formatted(FILTERS);

        final var COUNT_SQL = "SELECT COUNT(*) FROM employee e WHERE %s".formatted(FILTERS);

        final var sanitizedCep = cep != null && cep.isBlank() ? null : cep;

        // O tipo do cep precisa ser informado para o banco entender o parâmetro quando ele for nulo
        final var items = jdbcClient.sql(SQL)
            .param("active", active)
            .param("cep", sanitizedCep, Types.VARCHAR)
            .param("limit", range.limit())
            .param("offset", range.offset())
            .query(EMPLOYEE_CEP_MAPPER)
            .list();

        final var total = jdbcClient.sql(COUNT_SQL)
            .param("active", active)
            .param("cep", sanitizedCep, Types.VARCHAR)
            .query(Long.class)
            .single();

        return new Page<>(items, range, total);
    }

    private static RowMapper<EmployeeCep> employeeCepMapper() {
        final var conversionService = new DefaultConversionService();
        conversionService.addConverter(String.class, EmployeeType.class, EmployeeType::fromCode);

        return DataClassRowMapper.newInstance(EmployeeCep.class, conversionService);
    }
}
