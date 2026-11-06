package br.puc_rio.tecgraf.challenge.domain.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;

// Projeção do funcionário com o CEP extraído do endereço, usada na listagem de CEPs.
// O cep é null quando o endereço não contém um CEP no formato 00000-000.
public record EmployeeCep(
    String cep,
    String employeeId,
    EmployeeType type,
    String firstName,
    String lastName,
    LocalDate birthDate,
    Gender gender,
    BigDecimal baseSalary,
    boolean active
) { }
