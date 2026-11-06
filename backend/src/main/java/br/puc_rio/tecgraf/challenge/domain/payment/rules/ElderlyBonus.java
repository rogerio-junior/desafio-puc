package br.puc_rio.tecgraf.challenge.domain.payment.rules;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Period;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;

public class ElderlyBonus implements SalaryRule {
    @Override
    public BigDecimal computeSalary(Payee payee, PayrollReference payrollRef, BigDecimal currentSalary) {
        // Considera a idade até o fim do mês de competência
        var age = Period.between(payee.getBirthDate(), payrollRef.getPeriod().atEndOfMonth()).getYears();

        if ((payee.getGender() == Gender.M && age > 65) || (payee.getGender() == Gender.F && age > 60)) {
            var bonus = payee.getBaseSalary().multiply(new BigDecimal("0.03")).setScale(2, RoundingMode.HALF_UP);

            return currentSalary.add(bonus);
        }

        return currentSalary;
    }
}
