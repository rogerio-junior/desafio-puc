package br.puc_rio.tecgraf.challenge.domain.payment.rules;

import java.math.BigDecimal;

import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;

public class BirthdayBonus implements SalaryRule {
    @Override
    public BigDecimal computeSalary(Payee payee, PayrollReference payrollRef, BigDecimal currentSalary) {
        if (payrollRef.getPeriod().getMonth() == payee.getBirthDate().getMonth()) {
            var bonus = new BigDecimal("200");

            return currentSalary.add(bonus);
        }

        return currentSalary;
    }
}
