package br.puc_rio.tecgraf.challenge.domain.payment.rules;

import java.math.BigDecimal;

import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;

public class WeekendBonus implements SalaryRule {
    @Override
    public BigDecimal computeSalary(Payee payee, PayrollReference payrollRef, BigDecimal currentSalary) {
        if (payee.getWeekendDaysWorked() > 0) {
            var bonus = new BigDecimal("40").multiply(BigDecimal.valueOf(payee.getWeekendDaysWorked()));

            return currentSalary.add(bonus);
        }

        return currentSalary;
    }
}
