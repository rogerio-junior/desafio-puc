package br.puc_rio.tecgraf.challenge.domain.payment.rules;

import java.math.BigDecimal;

import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;

// Abstrai o cálculo do salário para permitir criar regras reutilizáveis e,
// caso alguma mude, muda para todos, como por exemplo o bonus passar a ser
// 4% ao invés de 3%.  
@FunctionalInterface
public interface SalaryRule {
    BigDecimal computeSalary(Payee payee, PayrollReference payrollRef, BigDecimal currentSalary);

    // Encadeia regras, aplicando esta regra primeiro e passa o resultado para a próxima.
    default SalaryRule andThen(SalaryRule next) {
        return (payee, payrollRef, currentSalary) -> {
            final var salaryAfterThisRule = this.computeSalary(payee, payrollRef, currentSalary);

            return next.computeSalary(payee, payrollRef, salaryAfterThisRule);
        };
    }
}
