package br.puc_rio.tecgraf.challenge.infra.payment;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeType;
import br.puc_rio.tecgraf.challenge.domain.payment.Payee;
import br.puc_rio.tecgraf.challenge.domain.payment.payees.FixedTermPayee;
import br.puc_rio.tecgraf.challenge.domain.payment.payees.InternPayee;
import br.puc_rio.tecgraf.challenge.domain.payment.payees.PermanentPayee;
import br.puc_rio.tecgraf.challenge.domain.payment.payees.ScholarPayee;
import br.puc_rio.tecgraf.challenge.infra.SocialNumberMapper;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = SocialNumberMapper.class
)
public interface PayeeMapper {
    default Payee toDomain(PayeeProjection projection) {
        return switch (EmployeeType.fromCode(projection.type())) {
            case FIXED_TERM -> toFixedTermPayee(projection);
            case INTERN -> toInternPayee(projection);
            case PERMANENT -> toPermanentPayee(projection);
            case SCHOLAR -> toScholarPayee(projection);
        };
    }

    FixedTermPayee toFixedTermPayee(PayeeProjection projection);
    InternPayee toInternPayee(PayeeProjection projection);
    PermanentPayee toPermanentPayee(PayeeProjection projection);
    ScholarPayee toScholarPayee(PayeeProjection projection);
}
