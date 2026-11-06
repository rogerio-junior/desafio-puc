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

    @Override
    public Page<Employee> findByActiveAndCep(boolean active, String cep, Page.Range range) {
        final var offset = range.offset();
        final var sanitizedCep = cep != null && cep.isBlank() ? null : cep;

        final var employees = jpaRepository.findByActiveAndCep(active, sanitizedCep, range.limit(), offset).stream()
            .map(mapper::toDomain)
            .toList();

        final var total = jpaRepository.countByActiveAndCep(active, sanitizedCep);

        return new Page<>(employees, range, total);
    }
}
