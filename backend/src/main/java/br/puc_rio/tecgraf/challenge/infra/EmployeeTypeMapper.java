package br.puc_rio.tecgraf.challenge.infra;

import org.mapstruct.Mapper;

import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;

@Mapper(componentModel = "spring")
public interface EmployeeTypeMapper {
    default EmployeeType toType(String code) {
        return code == null ? null : EmployeeType.fromCode(code);
    }

    default String toCode(EmployeeType type) {
        return type == null ? null : type.getCode();
    }
}
