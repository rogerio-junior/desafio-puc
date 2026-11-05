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
    @EqualsAndHashCode.Include private SocialNumber socialNumber;
    private EmployeeType type;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private BigDecimal baseSalary;
    private String address;
    private boolean active;
}
