package br.puc_rio.tecgraf.challenge.infra.employee;

import org.springframework.stereotype.Repository;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.employee.Employee;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepository {
    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeMapper mapper;

    // Assim como em PaymentRepositoryImpl, o afterKey do cursor é usado como offset
    @Override
    public Page<Employee> findByActiveAndCep(boolean active, String cep, Page.Cursor cursor) {
        final var offset = Long.parseLong(cursor.afterKey());

        final var employees = jpaRepository.findByActiveAndCep(active, cep, cursor.limit(), offset).stream()
            .map(mapper::toDomain)
            .toList();

        final var total = jpaRepository.countByActiveAndCep(active, cep);

        return new Page<>(employees, cursor, total);
    }
}
