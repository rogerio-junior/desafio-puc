package br.puc_rio.tecgraf.challenge.infra.employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, String> {
    // O CEP fica no meio do endereço, que pode ter mais de um. Por isso extrai todos e compara com o informado:
    // o valor não entra na regex, e os limites impedem que números maiores (ex: 123456-7890) sejam considerados.
    String ACTIVE_AND_CEP_FILTER = """
        e.active = :active
        AND (:cep IS NULL OR EXISTS (
            SELECT 1
            FROM regexp_matches(e.address, '(?<![0-9])[0-9]{5}-[0-9]{3}(?![0-9])', 'g') AS found
            WHERE found[1] = :cep
        ))
        """;

    @Query(value = "SELECT e.* FROM employee e WHERE " + ACTIVE_AND_CEP_FILTER + """
        ORDER BY e.first_name, e.last_name, e.id_social_number
        LIMIT :limit OFFSET :offset
        """, nativeQuery = true)
    List<EmployeeEntity> findByActiveAndCep(
        @Param("active") boolean active,
        @Param("cep") String cep,
        @Param("limit") int limit,
        @Param("offset") long offset
    );

    @Query(value = "SELECT COUNT(*) FROM employee e WHERE " + ACTIVE_AND_CEP_FILTER, nativeQuery = true)
    long countByActiveAndCep(@Param("active") boolean active, @Param("cep") String cep);
}
