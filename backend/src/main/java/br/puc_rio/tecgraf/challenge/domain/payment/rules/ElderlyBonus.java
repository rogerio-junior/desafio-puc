package br.puc_rio.tecgraf.challenge.domain.payment.rules;

import java.math.BigDecimal;
import java.time.Period;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.PayrollReference;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentEmployee;

public class ElderlyBonus implements SalaryRule {
    @Override
    public BigDecimal computeSalary(PaymentEmployee employee, PayrollReference payrollRef, BigDecimal currentSalary) {
        // Considera a idade no fim do mês de competência
        var age = Period.between(employee.getBirthDate(), payrollRef.getPeriod().atEndOfMonth()).getYears();

        if ((employee.getGender() == Gender.M && age > 65)
            || employee.getGender() == Gender.F && age > 60) {
            var bonus = employee.getBaseSalary().multiply(new BigDecimal("0.03"));

            return currentSalary.add(bonus);
        }

        return currentSalary;
    }
}
