package br.puc_rio.tecgraf.challenge.domain.employee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Resolvi dar nomes aos tipos para ficar mais fácil de me encontrar no código
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum EmployeeType {
    PERMANENT("A"), // Contrato por tempo indeterminado
    FIXED_TERM("B"), // Contrato a prazo
    INTERN("C"), // Estagiário
    SCHOLAR("D");  // Bolsista

    private final String code;

    public static EmployeeType fromCode(final String code) {
        return switch (code) {
            case "A" -> PERMANENT;
            case "B" -> FIXED_TERM;
            case "C" -> INTERN;
            case "D" -> SCHOLAR;
            default -> throw new IllegalArgumentException("Invalid employee type code %s".formatted(code));
        };
    }
}
