package br.puc_rio.tecgraf.challenge.domain;

import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// VO para garantir que o CPF transportando entre as camadas
// do sistema esteja sempre válido. Poderia ser usado para
// suportar outros tipos de documento também.
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialNumber {
    // Aceita 92572993848 ou 925.729.938-48
    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");

    private final String number;

    public static SocialNumber of(final String number) {
        if (number == null) {
            return null;
        }

        if (!isValid(number)) {
            throw new IllegalArgumentException("Invalid cpf %s".formatted(number));
        }

        return new SocialNumber(number.replaceAll("\\D", ""));
    }

    public static boolean isValid(final String number) {
        // Validação básica de formato, ainda há falsos positivos
        // como 00000000000, mas optei por manter uma validação
        // simples e mínima por ser um desafio
        return number != null && CPF_PATTERN.matcher(number.trim()).matches();
    }

    @Override
    public String toString() {
        return number;
    }
}
