package br.puc_rio.tecgraf.challenge.infra.payment;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import br.puc_rio.tecgraf.challenge.domain.EmployeeType;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.FixedTermEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.InternEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.PermanentEmployee;
import br.puc_rio.tecgraf.challenge.domain.payment.employees.ScholarEmployee;
import br.puc_rio.tecgraf.challenge.infra.SocialNumberMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = SocialNumberMapper.class
)
public interface PaymentEmployeeMapper {
    default PaymentEmployee toDomain(PaymentEmployeeProjection projection) {
        return switch (EmployeeType.fromCode(projection.type())) {
            case FIXED_TERM -> toFixedTermEmployee(projection);
            case INTERN -> toInternEmployee(projection);
            case PERMANENT -> toPermanentEmployee(projection);
            case SCHOLAR -> toScholarEmployee(projection);
        };
    }

    FixedTermEmployee toFixedTermEmployee(PaymentEmployeeProjection projection);
    InternEmployee toInternEmployee(PaymentEmployeeProjection projection);
    PermanentEmployee toPermanentEmployee(PaymentEmployeeProjection projection);
    ScholarEmployee toScholarEmployee(PaymentEmployeeProjection projection);
}
