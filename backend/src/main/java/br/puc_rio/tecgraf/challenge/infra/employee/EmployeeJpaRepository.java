package br.puc_rio.tecgraf.challenge.infra.employee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, String> {
    String ACTIVE_AND_CEP_FILTER = """
        e.active = :active
        AND (CAST(:cep AS TEXT) IS NULL OR extract_cep(e.address) = CAST(:cep AS TEXT))
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
