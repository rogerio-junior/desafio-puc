package br.puc_rio.tecgraf.challenge.domain;

import java.util.Arrays;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmployeeType {
    PERMANENT("A"), // Contrato por tempo indeterminado
    FIXED_TERM("B"), // Contrato a prazo
    INTERN("C"), // Estagiário
    SCHOLAR("D");  // Bolsista

    private final String code;

    public static EmployeeType fromCode(final String code) {
        return Arrays.stream(values())
            .filter(type -> type.code.equals(code))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid employee type code %s".formatted(code)));
    }
}
