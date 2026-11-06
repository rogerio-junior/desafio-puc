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

    // A classe que calcula salário é a PaymentEmployee.
    // A classe Employee é para o CRUD dos dados do funcionário.
    // A classe PaymentEmployee é uma projeção de Employee, 
    // contendo apenas os campos necessários para o cálculo e 
    // campos adicionais, como o weekendWorkedDays, que por sua vez
    // não faze sentido em um contexto de criação de um funcionário.
}
