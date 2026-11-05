package br.puc_rio.tecgraf.challenge.domain.payment.employees;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.SalaryRule;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.WeekendBonus;

public class ScholarEmployee extends PaymentEmployee {
    private static final SalaryRule SALARY_RULE = new WeekendBonus();

    public ScholarEmployee(SocialNumber socialNumber, LocalDate birthDate, Gender gender, BigDecimal baseSalary,
            int workedWeekendDays) {
        super(socialNumber, birthDate, gender, baseSalary, workedWeekendDays);
    }

    @Override
    public EmployeeType getType() {
        return EmployeeType.SCHOLAR;
    }

    @Override
    public BigDecimal computeSalary(PayrollReference payrollRef) {
        return SALARY_RULE.computeSalary(this, payrollRef, getBaseSalary());
    }
}
