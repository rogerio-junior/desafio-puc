package br.puc_rio.tecgraf.challenge.domain.employee;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.Page.Cursor;

public interface EmployeeRepository {
    Page<Employee> findByActiveAndCep(boolean active, String cep, Page.Cursor cursor);
}
