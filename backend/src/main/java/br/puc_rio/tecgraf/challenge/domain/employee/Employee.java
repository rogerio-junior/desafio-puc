package br.puc_rio.tecgraf.challenge.domain.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Employee {
    // Usei string no lugar de int para considerar CPFs que iniciam com zero
    @EqualsAndHashCode.Include private SocialNumber id;
    private EmployeeType type;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private BigDecimal baseSalary;
    private String address;
    private boolean active;

    // O cálculo de salário fica em payment.Payee (beneficiário).
    // Veja mais sobre o motivo da decisão no `README.md#-decisões`
}
