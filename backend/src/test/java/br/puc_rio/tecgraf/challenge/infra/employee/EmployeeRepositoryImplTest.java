package br.puc_rio.tecgraf.challenge.infra.employee;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.Employee;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeRepository;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;

// Usa o Postgres local. Cada teste cria os próprios dados e sofre rollback ao final.
@SpringBootTest
@Transactional
class EmployeeRepositoryImplTest {
    private static final String CEP = "99999-001"; // Fora do seed, isola as fixtures
    private static final String OTHER_CEP = "99999-002";
    private static final Page.Cursor ALL = new Page.Cursor(null, 10);

    private static final String FIRST = "90000000001";
    private static final String SECOND = "90000000002";
    private static final String THIRD = "90000000003";
    private static final String FOURTH = "90000000004";

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void mapsAllFieldsOfTheEmployee() {
        insertEmployee(FIRST, "Ana", "Lima", "C", true, "Rua A, 10 - Centro, " + CEP);

        var page = repository.findByActiveAndCep(true, CEP, ALL);

        assertThat(page.total()).isEqualTo(1);
        var employee = page.items().getFirst();
        assertThat(employee.getSocialNumber()).isEqualTo(SocialNumber.of(FIRST));
        assertThat(employee.getType()).isEqualTo(EmployeeType.INTERN);
        assertThat(employee.getFirstName()).isEqualTo("Ana");
        assertThat(employee.getLastName()).isEqualTo("Lima");
        assertThat(employee.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 20));
        assertThat(employee.getGender()).isEqualTo(Gender.F);
        assertThat(employee.getBaseSalary()).isEqualByComparingTo("1234.56");
        assertThat(employee.getAddress()).isEqualTo("Rua A, 10 - Centro, " + CEP);
    }

    @Test
    void mapsEveryEmployeeTypeFromItsCode() {
        insertEmployee(FIRST, "A", "A", "A", true, CEP);
        insertEmployee(SECOND, "B", "B", "B", true, CEP);
        insertEmployee(THIRD, "C", "C", "C", true, CEP);
        insertEmployee(FOURTH, "D", "D", "D", true, CEP);

        assertThat(repository.findByActiveAndCep(true, CEP, ALL).items()).extracting(Employee::getType)
            .containsExactly(EmployeeType.PERMANENT, EmployeeType.FIXED_TERM, EmployeeType.INTERN, EmployeeType.SCHOLAR);
    }

    @Test
    void ordersAlphabeticallyAndUntiesHomonymsBySocialNumber() {
        insertEmployee(FOURTH, "Zeca", "Silva", "A", true, CEP);
        insertEmployee(THIRD, "Ágata", "Souza", "A", true, CEP);
        insertEmployee(SECOND, "Ana", "Lima", "A", true, CEP);
        insertEmployee(FIRST, "Ana", "Lima", "A", true, CEP);

        assertThat(repository.findByActiveAndCep(true, CEP, ALL).items()).extracting(this::socialNumberOf)
            .containsExactly(THIRD, FIRST, SECOND, FOURTH);
    }

    @Test
    void filtersByActiveStatus() {
        insertEmployee(FIRST, "Ana", "Lima", "A", true, CEP);
        insertEmployee(SECOND, "Bia", "Lima", "A", false, CEP);

        assertThat(repository.findByActiveAndCep(true, CEP, ALL).items()).extracting(this::socialNumberOf)
            .containsExactly(FIRST);
        assertThat(repository.findByActiveAndCep(false, CEP, ALL).items()).extracting(this::socialNumberOf)
            .containsExactly(SECOND);
    }

    @Test
    void findsTheCepAnywhereInTheAddress() {
        insertEmployee(FIRST, "A", "Início", "A", true, CEP + " - Rua A, 10");
        insertEmployee(SECOND, "B", "Fim", "A", true, "Rua B, 20, CEP " + CEP);
        insertEmployee(THIRD, "C", "Dois CEPs", "A", true, "Rua C, 30, " + OTHER_CEP + " / Rua D, 40, " + CEP);
        insertEmployee(FOURTH, "D", "Dois CEPs colados", "A", true, CEP + " " + OTHER_CEP);

        assertThat(repository.findByActiveAndCep(true, CEP, ALL).items()).extracting(this::socialNumberOf)
            .containsExactly(FIRST, SECOND, THIRD, FOURTH);
        assertThat(repository.findByActiveAndCep(true, OTHER_CEP, ALL).items()).extracting(this::socialNumberOf)
            .containsExactly(THIRD, FOURTH);
    }

    @Test
    void ignoresAddressesWithoutThatExactCep() {
        insertEmployee(FIRST, "A", "Outro CEP", "A", true, "Rua A, " + OTHER_CEP);
        insertEmployee(SECOND, "B", "Sem endereço", "A", true, null);
        insertEmployee(THIRD, "C", "Sem hífen", "A", true, "Rua C, 99999001");
        insertEmployee(FOURTH, "D", "Dígitos a mais", "A", true, "Travessa 199999-0012");

        assertThat(repository.findByActiveAndCep(true, CEP, ALL).items()).isEmpty();
    }

    @Test
    void returnsAnEmptyPageWhenNothingIsFound() {
        var page = repository.findByActiveAndCep(true, CEP, ALL);

        assertThat(page.items()).isEmpty();
        assertThat(page.total()).isZero();
    }

    @Test
    void doesNotInterpretTheInformedCepAsARegex() {
        insertEmployee(FIRST, "Ana", "Lima", "A", true, "Rua A, " + CEP);

        assertThat(repository.findByActiveAndCep(true, ".*", ALL).items()).isEmpty();
        assertThat(repository.findByActiveAndCep(true, "99999-00.", ALL).items()).isEmpty();
    }

    @Test
    void paginatesWithLimitAndOffsetKeepingTheTotal() {
        insertEmployee(FIRST, "Ana", "Lima", "A", true, CEP);
        insertEmployee(SECOND, "Bia", "Lima", "A", true, CEP);
        insertEmployee(THIRD, "Caio", "Lima", "A", true, CEP);
        insertEmployee(FOURTH, "Dora", "Lima", "A", false, CEP);

        var firstCursor = new Page.Cursor(null, 2);
        var firstPage = repository.findByActiveAndCep(true, CEP, firstCursor);
        var secondPage = repository.findByActiveAndCep(true, CEP, new Page.Cursor("2", 2));
        var pastTheEnd = repository.findByActiveAndCep(true, CEP, new Page.Cursor("4", 2));

        assertThat(firstPage.items()).extracting(this::socialNumberOf).containsExactly(FIRST, SECOND);
        assertThat(firstPage.cursor()).isEqualTo(firstCursor);
        assertThat(secondPage.items()).extracting(this::socialNumberOf).containsExactly(THIRD);
        assertThat(pastTheEnd.items()).isEmpty();
        assertThat(List.of(firstPage.total(), secondPage.total(), pastTheEnd.total())).containsOnly(3L);
    }

    private String socialNumberOf(Employee employee) {
        return employee.getSocialNumber().getNumber();
    }

    private void insertEmployee(String socialNumber, String firstName, String lastName, String type, boolean active, String address) {
        jdbc.update("""
            INSERT INTO employee (id_social_number, first_name, last_name, birth_date, gender, active, base_salary, address, type)
            VALUES (?, ?, ?, DATE '1990-05-20', 'F', ?, 1234.56, ?, ?)
            """, socialNumber, firstName, lastName, active, address, type);
    }
}
