package br.puc_rio.tecgraf.challenge.infra.employee;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.Employee;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeMapper mapper;

    @Override
    public Optional<Employee> findById(SocialNumber id) {
        if (id == null) {
            return Optional.empty();
        }

        return jpaRepository.findById(id.getNumber()).map(mapper::toDomain);
    }

    // Assim como em PaymentRepositoryImpl, o afterKey do cursor é usado como offset
    @Override
    public Page<Employee> findByActiveAndCep(boolean active, String cep, Page.Cursor cursor) {
        final var offset = Long.parseLong(cursor.afterKey());
        final var sanitizedCep = cep != null && cep.isBlank() ? null : cep;

        final var employees = jpaRepository.findByActiveAndCep(active, sanitizedCep, cursor.limit(), offset).stream()
            .map(mapper::toDomain)
            .toList();

        final var total = jpaRepository.countByActiveAndCep(active, sanitizedCep);

        return new Page<>(employees, cursor, total);
    }
}
