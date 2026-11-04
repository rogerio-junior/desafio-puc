package br.puc_rio.tecgraf.challenge.domain.payment.employees;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.ElderlyBonus;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.SalaryRule;
import br.puc_rio.tecgraf.challenge.domain.Gender;

public class FixedTermEmployee extends PaymentEmployee {
    private static final SalaryRule SALARY_RULE = new ElderlyBonus();

    public FixedTermEmployee(SocialNumber socialNumber, LocalDate birthDate, Gender gender, BigDecimal baseSalary,
            int workedWeekendDays) {
        super(socialNumber, birthDate, gender, baseSalary, workedWeekendDays);
    }

    @Override
    public EmployeeType getType() {
        return EmployeeType.FIXED_TERM;
    }

    @Override
    public BigDecimal computeSalary(PayrollReference payrollRef) {
        return SALARY_RULE.computeSalary(this, payrollRef, getBaseSalary());
    }
}
