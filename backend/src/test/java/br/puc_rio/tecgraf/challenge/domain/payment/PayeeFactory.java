package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;
import br.puc_rio.tecgraf.challenge.domain.payment.payees.FixedTermPayee;
import br.puc_rio.tecgraf.challenge.domain.payment.payees.InternPayee;
import br.puc_rio.tecgraf.challenge.domain.payment.payees.PermanentPayee;
import br.puc_rio.tecgraf.challenge.domain.payment.payees.ScholarPayee;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PayeeFactory {
    private static final BigDecimal BASE_SALARY = new BigDecimal("1000.00");
    private static final LocalDate BIRTHDAY_OUT_OF_PERIOD = LocalDate.of(1990, 8, 10);

    public static Payee payee(String employeeId) {
        return new PermanentPayee(SocialNumber.of(employeeId), BIRTHDAY_OUT_OF_PERIOD, Gender.M, BASE_SALARY, 0);
    }

    public static Payee payee(EmployeeType type, String employeeId, BigDecimal baseSalary, LocalDate birthDate, Gender gender, int weekendDaysWorked) {
        final var socialNumber = SocialNumber.of(employeeId);

        return switch (type) {
            case PERMANENT -> new PermanentPayee(socialNumber, birthDate, gender, baseSalary, weekendDaysWorked);
            case FIXED_TERM -> new FixedTermPayee(socialNumber, birthDate, gender, baseSalary, weekendDaysWorked);
            case INTERN -> new InternPayee(socialNumber, birthDate, gender, baseSalary, weekendDaysWorked);
            case SCHOLAR -> new ScholarPayee(socialNumber, birthDate, gender, baseSalary, weekendDaysWorked);
        };
    }
}
