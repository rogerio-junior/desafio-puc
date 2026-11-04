package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.EmployeeType;
import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.FixedTermEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.InternEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.PermanentEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.ScholarEmployee;
import lombok.experimental.UtilityClass;

@UtilityClass 
public class PaymentEmployeeFactory {
    private static final BigDecimal BASE_SALARY = new BigDecimal("1000.00");
    private static final LocalDate BIRTHDAY_OUT_OF_PERIOD = LocalDate.of(1990, 8, 10);
 
    public static PaymentEmployee employee(String socialNumber) {
        return new PermanentEmployee(SocialNumber.of(socialNumber), BIRTHDAY_OUT_OF_PERIOD, Gender.M, BASE_SALARY, 0);
    }

    public static PaymentEmployee employee(EmployeeType type, LocalDate birthDate, Gender gender, int workedWeekendDays) {
        final var socialNumber = SocialNumber.of("1");

        return switch (type) {
            case PERMANENT -> new PermanentEmployee(socialNumber, birthDate, gender, BASE_SALARY, workedWeekendDays);
            case FIXED_TERM -> new FixedTermEmployee(socialNumber, birthDate, gender, BASE_SALARY, workedWeekendDays);
            case INTERN -> new InternEmployee(socialNumber, birthDate, gender, BASE_SALARY, workedWeekendDays);
            case SCHOLAR -> new ScholarEmployee(socialNumber, birthDate, gender, BASE_SALARY, workedWeekendDays);
        };
    }
}
