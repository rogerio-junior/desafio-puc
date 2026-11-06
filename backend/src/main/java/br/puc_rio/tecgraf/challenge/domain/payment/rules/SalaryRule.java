package br.puc_rio.tecgraf.challenge.domain.payment.rules;

import java.math.BigDecimal;
import java.util.List;

import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;
import lombok.RequiredArgsConstructor;

public interface SalaryRule {
    BigDecimal computeSalary(Payee payee, PayrollReference payrollRef, BigDecimal currentSalary);

    static SalaryRule compound(SalaryRule... rules) {
        return new CompoundSalaryRule(List.of(rules));
    }

    // TODO: Deveria ser privado
    @RequiredArgsConstructor
    static class CompoundSalaryRule implements SalaryRule {
        private final List<SalaryRule> rules;

        public BigDecimal computeSalary(Payee payee, PayrollReference payrollRef, BigDecimal currentSalary) {
            return rules.stream().reduce(
                currentSalary,  // identity: começa no salário atual
                (salary, rule) -> rule.computeSalary(payee, payrollRef, salary),   // accumulator: aplica cada regra sobre o valor atual
                (a, b) -> { throw new UnsupportedOperationException("Regras são sequenciais"); } // combiner: só usado em parallelStream
            );
        }
    }
}
