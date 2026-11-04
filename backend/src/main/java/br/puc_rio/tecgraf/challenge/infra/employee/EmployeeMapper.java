package br.puc_rio.tecgraf.challenge.infra.employee;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import br.puc_rio.tecgraf.challenge.domain.employee.Employee;
import br.puc_rio.tecgraf.challenge.infra.EmployeeTypeMapper;
import br.puc_rio.tecgraf.challenge.infra.SocialNumberMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = { SocialNumberMapper.class, EmployeeTypeMapper.class }
)
public interface EmployeeMapper {
    Employee toDomain(EmployeeEntity entity);
}
