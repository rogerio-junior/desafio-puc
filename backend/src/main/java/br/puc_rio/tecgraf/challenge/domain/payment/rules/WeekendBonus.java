package br.puc_rio.tecgraf.challenge.domain.payment.rules;

import java.math.BigDecimal;

import br.puc_rio.tecgraf.challenge.domain.payment.PaymentEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;

public class WeekendBonus implements SalaryRule {
    @Override
    public BigDecimal computeSalary(PaymentEmployee employee, PayrollReference payrollRef, BigDecimal currentSalary) {
        if (employee.getWorkedWeekendDays() > 0) {
            var bonus = new BigDecimal("40").multiply(BigDecimal.valueOf(employee.getWorkedWeekendDays()));

            return currentSalary.add(bonus);
        }

        return currentSalary;
    }
}
