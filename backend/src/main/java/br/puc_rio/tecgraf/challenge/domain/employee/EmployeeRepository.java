package br.puc_rio.tecgraf.challenge.domain.employee;

import java.util.Optional;

import br.puc_rio.tecgraf.challenge.domain.NotFoundException;
import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;

public interface EmployeeRepository {
    Optional<Employee> findById(SocialNumber id);

    // Eu costumo separar Find de Get para facilitar o uso onde a
    // recuperação da entidade é obrigatória e onde não é.
    // Também ajuda a padronizar esse mecanismo, principalmente
    // em retornos 404 de APIs.
    default Employee getById(SocialNumber id) throws NotFoundException {
       return findById(id).orElseThrow(() ->
           new NotFoundException("Employee not found [id=%s]".formatted(id.getNumber()))
       );
    }

    Page<EmployeeCep> findByActiveAndCep(boolean active, String cep, Page.Range range);
}
