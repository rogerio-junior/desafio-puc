package br.puc_rio.tecgraf.challenge.domain.payment.rules;

import java.math.BigDecimal;

import br.puc_rio.tecgraf.challenge.domain.PayrollReference;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentEmployee;

public class BirthdayBonus implements SalaryRule {
    @Override
    public BigDecimal computeSalary(PaymentEmployee employee, PayrollReference payrollRef, BigDecimal currentSalary) {
        if (payrollRef.getPeriod().getMonth() == employee.getBirthDate().getMonth()) {
            var bonus = new BigDecimal("200");

            return currentSalary.add(bonus);
        }

        return currentSalary;
    }
}
