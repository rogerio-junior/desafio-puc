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

    public static Payee payee(EmployeeType type, LocalDate birthDate, Gender gender, int weekendDaysWorked) {
        final var employeeId = SocialNumber.of("92572993848");

        return switch (type) {
            case PERMANENT -> new PermanentPayee(employeeId, birthDate, gender, BASE_SALARY, weekendDaysWorked);
            case FIXED_TERM -> new FixedTermPayee(employeeId, birthDate, gender, BASE_SALARY, weekendDaysWorked);
            case INTERN -> new InternPayee(employeeId, birthDate, gender, BASE_SALARY, weekendDaysWorked);
            case SCHOLAR -> new ScholarPayee(employeeId, birthDate, gender, BASE_SALARY, weekendDaysWorked);
        };
    }
}
