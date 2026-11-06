package br.puc_rio.tecgraf.challenge.infra.employee;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, String> { }
