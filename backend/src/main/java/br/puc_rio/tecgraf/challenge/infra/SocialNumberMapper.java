package br.puc_rio.tecgraf.challenge.infra;

import org.mapstruct.Mapper;

import br.puc_rio.tecgraf.challenge.domain.SocialNumber;

@Mapper(componentModel = "spring")
public interface SocialNumberMapper {
    default SocialNumber toCpf(String number) {
        return number == null ? null : SocialNumber.of(number);
    }

    default String toNumber(SocialNumber socialNumber) {
        return socialNumber == null ? null : socialNumber.getNumber();
    }
}
