package br.puc_rio.tecgraf.challenge.domain.employee;

import br.puc_rio.tecgraf.challenge.domain.Page;

public interface EmployeeRepository {
    Page<Employee> findByActiveAndCep(boolean active, String cep, Page.Cursor cursor);
}
