package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Payee {
    @EqualsAndHashCode.Include private final SocialNumber socialNumber;
    private final LocalDate birthDate;
    private final Gender gender;
    private final BigDecimal baseSalary;
    private final int weekendDaysWorked;

    public abstract EmployeeType getType();
    
    // Mudei de double para BigDecimal para impedir erros de precisão.
    // Mudei de Date para PayrollReference para manter unida a regra de data 
    // de referência + mês de competência, além de usar API "nova" de datas do Java.
    // Mantive o método pela proposta do desafio, mas geralmente eu separaria os 
    // dados do comportamento para impedir que a classe fique inchada futuramente ao
    // adicionar mais funções conforme o sistema cresce.
    public abstract BigDecimal computeSalary(PayrollReference payrollRef);
}
