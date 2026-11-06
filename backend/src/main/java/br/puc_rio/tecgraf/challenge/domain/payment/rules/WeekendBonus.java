package br.puc_rio.tecgraf.challenge.domain.payment.rules;

import java.math.BigDecimal;

import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;

public class WeekendBonus implements SalaryRule {
    @Override
    public BigDecimal computeSalary(Payee payee, PayrollReference payrollRef, BigDecimal currentSalary) {
        if (payee.getWorkedWeekendDays() > 0) {
            var bonus = new BigDecimal("40").multiply(BigDecimal.valueOf(payee.getWorkedWeekendDays()));

            return currentSalary.add(bonus);
        }

        return currentSalary;
    }
}
