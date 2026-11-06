package br.puc_rio.tecgraf.challenge.infra.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "employee")
@Getter
@Setter
@Accessors(chain = true)
public class EmployeeEntity {
    @Id 
    @Column(name = "id_social_number")
    private String id;

    private String type;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private boolean active;

    private BigDecimal baseSalary;
    
    private String address;
}
