package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Mudou a perspectiva: Ao invés de ser uma classe Employee geral, é uma projeção/visão dessa entidade
// Isso permite trazer para o domínio de payments apenas o que se é necessário para calcular e
// Representar nesse domínio
@Getter
@RequiredArgsConstructor // Talvez validar as constraints
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class PaymentEmployee {
    @EqualsAndHashCode.Include private final SocialNumber socialNumber;
    private final LocalDate birthDate;
    private final Gender gender;
    private final BigDecimal baseSalary;
    private final int workedWeekendDays;

    public abstract EmployeeType getType();
    
    // Mudei de double para BigDecimal para impedir erros de precisão
    // Mudei de Date para PayrollReference para manter unida a regra de data de referência + mês de competência, além de usar API "nova" de datas do Java
    // Geralmente eu separaria os dados do comportamento nesse caso
    public abstract BigDecimal computeSalary(PayrollReference payrollRef);
}
