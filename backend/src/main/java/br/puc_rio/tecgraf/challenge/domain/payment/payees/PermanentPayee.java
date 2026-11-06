package br.puc_rio.tecgraf.challenge.domain.payment.payees;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;
import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.BirthdayBonus;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.ElderlyBonus;
import br.puc_rio.tecgraf.challenge.domain.payment.rules.SalaryRule;

public class PermanentPayee extends Payee {
    private static final SalaryRule SALARY_RULE = new BirthdayBonus().andThen(new ElderlyBonus());

    public PermanentPayee(SocialNumber employeeId, LocalDate birthDate, Gender gender, BigDecimal baseSalary, int weekendDaysWorked) {
        super(employeeId, birthDate, gender, baseSalary, weekendDaysWorked);
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
