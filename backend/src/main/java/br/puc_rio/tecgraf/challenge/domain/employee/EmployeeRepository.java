package br.puc_rio.tecgraf.challenge.domain.employee;

import java.util.Optional;

import br.puc_rio.tecgraf.challenge.domain.NotFoundException;
import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;

public interface EmployeeRepository {
    Optional<Employee> findBySocialNumber(SocialNumber socialNumber);

    // Eu costumo separar Find de Get para facilitar o uso onde a
    // recuperação da entidade é obrigatória e onde não é.
    // Também ajuda a padronizar esse mecanismo, principalmente
    // em retornos 404 de APIs.
    default Employee getBySocialNumber(SocialNumber socialNumber) throws NotFoundException {
       return findBySocialNumber(socialNumber).orElseThrow(() ->
           new NotFoundException("Employee not found [socialNumber=%s]".formatted(socialNumber.getNumber()))
       );
    }

    Page<Employee> findByActiveAndCep(boolean active, String cep, Page.Cursor cursor);
}
