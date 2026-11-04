package br.puc_rio.tecgraf.challenge.domain.payment.employees;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.BirthdayBonus;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.ElderlyBonus;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.SalaryRule;
import br.puc_rio.tecgraf.challenge.domain.EmployeeType;
import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.PayrollReference;

public class PermanentEmployee extends PaymentEmployee {
    private static final SalaryRule SALARY_RULE = SalaryRule.compound(new BirthdayBonus(), new ElderlyBonus());

    public PermanentEmployee(SocialNumber socialNumber, LocalDate birthDate, Gender gender, BigDecimal baseSalary,
            int workedWeekendDays) {
        super(socialNumber, birthDate, gender, baseSalary, workedWeekendDays);
    }

    @Override
    public EmployeeType getType() {
        return EmployeeType.PERMANENT;
    }

    @Override
    public BigDecimal computeSalary(PayrollReference payrollRef) {
        return SALARY_RULE.computeSalary(this, payrollRef, getBaseSalary());
    }
}
