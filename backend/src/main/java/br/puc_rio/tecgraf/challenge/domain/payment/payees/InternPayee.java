package br.puc_rio.tecgraf.challenge.domain.payment.payees;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;
import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.SalaryRule;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.WeekendBonus;

public class InternPayee extends Payee {
    private static final SalaryRule SALARY_RULE = new WeekendBonus();

    public InternPayee(SocialNumber socialNumber, LocalDate birthDate, Gender gender, BigDecimal baseSalary, int weekendDaysWorked) {
        super(socialNumber, birthDate, gender, baseSalary, weekendDaysWorked);
    }

    @Override
    public EmployeeType getType() {
        return EmployeeType.INTERN;
    }

    @Override
    public BigDecimal computeSalary(PayrollReference payrollRef) {
        return SALARY_RULE.computeSalary(this, payrollRef, getBaseSalary());
    }
}
