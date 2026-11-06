package br.puc_rio.tecgraf.challenge.domain.payment;

import static br.puc_rio.tecgraf.challenge.domain.employee.EmployeeFactory.birthdayIn;
import static br.puc_rio.tecgraf.challenge.domain.employee.EmployeeFactory.birthdayNotIn;
import static br.puc_rio.tecgraf.challenge.domain.employee.EmployeeFactory.employeeId;
import static br.puc_rio.tecgraf.challenge.domain.payment.PayeeFactory.payee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;

@ExtendWith(MockitoExtension.class)
class SalaryServiceUTest {
    private static final BigDecimal BASE_SALARY = new BigDecimal("1000.00");

    // Posição de cada cenário no array de payees (e, portanto, na lista de pagamentos)
    private static final int BIRTHDAY = 0;
    private static final int NO_BONUS = 1;
    private static final int ELDERLY_MALE_66 = 2;
    private static final int ELDERLY_FEMALE_61 = 3;
    private static final int NON_BINARY_70 = 4;
    private static final int MALE_64 = 5;
    private static final int MALE_65 = 6;
    private static final int FEMALE_60 = 7;
    private static final int FEMALE_59 = 8;
    private static final int WEEKEND = 9;
    private static final int BIRTHDAY_WEEKEND_FEMALE_61 = 10;
    private static final int ELDERLY_ROUNDING = 11;
    private static final int SCENARIOS_COUNT = 12;

    private static Payee[] buildScenarios(EmployeeType type, PayrollReference payrollRef) {
        final var period = payrollRef.getPeriod();
        final var scenarios = new Payee[SCENARIOS_COUNT];

        // Cada cenário tem um CPF distinto, derivado da posição, para garantir
        // que cada pagamento foi gerado a partir do payee correto.

        // Bônus aniversário +200: 30 anos, aniversariante, não binário, não trabalhou fds
        scenarios[BIRTHDAY] = payee(type, employeeId(BIRTHDAY), BASE_SALARY, birthdayIn(period, 30), Gender.N, 0);

        // Sem bônus: 59 anos, não aniversariante, não binário, não trabalhou fds
        scenarios[NO_BONUS] = payee(type, employeeId(NO_BONUS), BASE_SALARY, birthdayNotIn(period, 59), Gender.N, 0);

        // Bônus idoso 3%: 66 anos, não aniversariante, homem, não trabalho fds
        scenarios[ELDERLY_MALE_66] = payee(type, employeeId(ELDERLY_MALE_66), BASE_SALARY, birthdayNotIn(period, 66), Gender.M, 0);

        // Bônus idoso 3%: 61 anos, não aniversariante, mulher, não trabalho fds
        scenarios[ELDERLY_FEMALE_61] = payee(type, employeeId(ELDERLY_FEMALE_61), BASE_SALARY, birthdayNotIn(period, 61), Gender.F, 0);

        // Sem bônus idoso: 70 anos, não aniversariante, não binário, não trabalho fds
        scenarios[NON_BINARY_70] = payee(type, employeeId(NON_BINARY_70), BASE_SALARY, birthdayNotIn(period, 70), Gender.N, 0);

        // Sem bônus idoso: 64 anos, não aniversariante, homem, não trabalho fds
        scenarios[MALE_64] = payee(type, employeeId(MALE_64), BASE_SALARY, birthdayNotIn(period, 64), Gender.M, 0);

        // Sem bônus idoso: 65 anos, não aniversariante, homem, não trabalho fds
        scenarios[MALE_65] = payee(type, employeeId(MALE_65), BASE_SALARY, birthdayNotIn(period, 65), Gender.M, 0);

        // Sem bônus idoso: 60 anos, não aniversariante, mulher, não trabalho fds
        scenarios[FEMALE_60] = payee(type, employeeId(FEMALE_60), BASE_SALARY, birthdayNotIn(period, 60), Gender.F, 0);

        // Sem bônus idoso: 59 anos, não aniversariante, mulher, não trabalho fds
        scenarios[FEMALE_59] = payee(type, employeeId(FEMALE_59), BASE_SALARY, birthdayNotIn(period, 59), Gender.F, 0);

        // Bônus fds +40: 23 anos, não aniversariante, homem, trabalhou fds
        scenarios[WEEKEND] = payee(type, employeeId(WEEKEND), BASE_SALARY, birthdayNotIn(period, 23), Gender.M, 3);

        // Todos os bônus: 61 anos, aniversariante, mulher, trabalhou fds
        scenarios[BIRTHDAY_WEEKEND_FEMALE_61] = payee(type, employeeId(BIRTHDAY_WEEKEND_FEMALE_61), BASE_SALARY, birthdayIn(period, 61), Gender.F, 3);

        // Bônus idoso 3% com arredondamento: 61 anos, não aniversariante, mulher, não trabalhou fds, 3% de 1000.50 = 30.015
        scenarios[ELDERLY_ROUNDING] = payee(type, employeeId(ELDERLY_ROUNDING), new BigDecimal("1000.50"), birthdayNotIn(period, 61), Gender.F, 0);

        return scenarios;
    }

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private SalaryService sut;

    @Captor
    private ArgumentCaptor<List<Payment>> paymentsCaptor;

    @Test
    void computeSalary_shouldApplyPermanentRules() {
        final var payrollRef = PayrollReference.of(LocalDate.of(2026, 11, 4));
        final var salaries = computeSalaries(EmployeeType.PERMANENT, payrollRef);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(salaries.get(BIRTHDAY)).as("aniversariante: +200").isEqualByComparingTo("1200.00");
            softly.assertThat(salaries.get(NO_BONUS)).as("sem bônus").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(ELDERLY_MALE_66)).as("homem > 65: +3%").isEqualByComparingTo("1030.00");
            softly.assertThat(salaries.get(ELDERLY_FEMALE_61)).as("mulher > 60: +3%").isEqualByComparingTo("1030.00");
            softly.assertThat(salaries.get(NON_BINARY_70)).as("não binário não recebe bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(MALE_64)).as("homem <= 65: sem bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(MALE_65)).as("homem <= 65: sem bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(FEMALE_60)).as("mulher <= 60: sem bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(FEMALE_59)).as("mulher <= 60: sem bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(WEEKEND)).as("efetivo não recebe bônus fds").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(BIRTHDAY_WEEKEND_FEMALE_61)).as("aniversariante: +200; mulher > 60: +3% da base; efetivo sem bônus fds").isEqualByComparingTo("1230.00");
            softly.assertThat(salaries.get(ELDERLY_ROUNDING)).as("mulher > 60: +3% de 1000.50 = 30.015 -> 30.02").isEqualByComparingTo("1030.52");
        });
    }

    @Test
    void computeSalary_shouldApplyFixedTermRules() {
        final var payrollRef = PayrollReference.of(LocalDate.of(2026, 11, 4));
        final var salaries = computeSalaries(EmployeeType.FIXED_TERM, payrollRef);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(salaries.get(BIRTHDAY)).as("temporário não recebe bônus aniversário").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(NO_BONUS)).as("sem bônus").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(ELDERLY_MALE_66)).as("homem > 65: +3%").isEqualByComparingTo("1030.00");
            softly.assertThat(salaries.get(ELDERLY_FEMALE_61)).as("mulher > 60: +3%").isEqualByComparingTo("1030.00");
            softly.assertThat(salaries.get(NON_BINARY_70)).as("não binário não recebe bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(MALE_64)).as("homem <= 65: sem bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(MALE_65)).as("homem <= 65: sem bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(FEMALE_60)).as("mulher <= 60: sem bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(FEMALE_59)).as("mulher <= 60: sem bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(WEEKEND)).as("temporário não recebe bônus fds").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(BIRTHDAY_WEEKEND_FEMALE_61)).as("mulher > 60: +3%; temporário sem bônus aniversário/fds").isEqualByComparingTo("1030.00");
            softly.assertThat(salaries.get(ELDERLY_ROUNDING)).as("mulher > 60: +3% de 1000.50 = 30.015 -> 30.02").isEqualByComparingTo("1030.52");
        });
    }

    // Estagiário e bolsista têm a mesma regra
    @ParameterizedTest
    @EnumSource(value = EmployeeType.class, names = {"INTERN", "SCHOLAR"})
    void computeSalary_shouldApplyWeekendOnlyRules(EmployeeType type) {
        final var payrollRef = PayrollReference.of(LocalDate.of(2026, 11, 4));
        final var salaries = computeSalaries(type, payrollRef);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(salaries.get(BIRTHDAY)).as("não recebe bônus aniversário").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(NO_BONUS)).as("sem bônus").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(ELDERLY_MALE_66)).as("não recebe bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(ELDERLY_FEMALE_61)).as("não recebe bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(NON_BINARY_70)).as("não recebe bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(MALE_64)).as("não recebe bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(MALE_65)).as("não recebe bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(FEMALE_60)).as("não recebe bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(FEMALE_59)).as("não recebe bônus idoso").isEqualByComparingTo("1000.00");
            softly.assertThat(salaries.get(WEEKEND)).as("3 dias fds: +3 x 40").isEqualByComparingTo("1120.00");
            softly.assertThat(salaries.get(BIRTHDAY_WEEKEND_FEMALE_61)).as("3 dias fds: +3 x 40; sem bônus aniversário/idoso").isEqualByComparingTo("1120.00");
            softly.assertThat(salaries.get(ELDERLY_ROUNDING)).as("não recebe bônus idoso").isEqualByComparingTo("1000.50");
        });
    }

    // Executa o cálculo para os cenários do tipo informado, valida o que é comum
    // a todos os tipos e devolve os salários na mesma ordem dos cenários.
    private List<BigDecimal> computeSalaries(EmployeeType type, PayrollReference payrollRef) {
        // Given
        final var payees = buildScenarios(type, payrollRef);

        when(paymentRepository.findPendingPayeesOn(payrollRef.getPeriod())).thenReturn(List.of(payees));
        when(paymentRepository.insertPayments(anyList())).thenReturn(payees.length);

        // When
        final var result = sut.computeSalary(payrollRef.getReferenceDate());

        // Then
        verify(paymentRepository).insertPayments(paymentsCaptor.capture());

        assertThat(result.payrollRef()).isEqualTo(payrollRef);
        assertThat(result.expectedQuantity()).isEqualTo(payees.length);
        assertThat(result.generatedQuantity()).isEqualTo(payees.length);

        final var payments = paymentsCaptor.getValue();

        assertThat(payments)
            .extracting(Payment::getEmployeeId, Payment::getPeriod)
            .containsExactly(Arrays.stream(payees)
                .map(p -> tuple(p.getEmployeeId(), payrollRef.getPeriod()))
                .toArray(Tuple[]::new));

        return payments.stream().map(Payment::getAmount).toList();
    }

    @Test
    void computeSalayOf_shouldInsertOnlyHalf() {
        // Given
        final var referenceDate = LocalDate.of(2026, 11, 4);
        final var payrollPeriod = YearMonth.of(2026, 10);

        when(paymentRepository.findPendingPayeesOn(payrollPeriod))
            .thenReturn(List.of(payee("92572993848"), payee("09530965214")));
        when(paymentRepository.insertPayments(anyList())).thenReturn(1);

        // When
        final var result = sut.computeSalary(referenceDate);

        // Then
        verify(paymentRepository).insertPayments(paymentsCaptor.capture());

        assertThat(result.payrollRef().getReferenceDate()).isEqualTo(referenceDate);
        assertThat(result.payrollRef().getPeriod()).isEqualTo(payrollPeriod);
        assertThat(result.expectedQuantity()).isEqualTo(2);
        assertThat(result.generatedQuantity()).isEqualTo(1);
        assertThat(paymentsCaptor.getValue())
            .extracting(Payment::getEmployeeId, Payment::getPeriod, Payment::getAmount)
            .containsExactly(
                tuple(SocialNumber.of("92572993848"), payrollPeriod, new BigDecimal("1000.00")),
                tuple(SocialNumber.of("09530965214"), payrollPeriod, new BigDecimal("1000.00")));
    }
}
