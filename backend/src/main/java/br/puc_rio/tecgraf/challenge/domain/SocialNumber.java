package br.puc_rio.tecgraf.challenge.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialNumber {
    private final String number;

    public static SocialNumber of(final String number) {
        if (!isValid(number)) {
            throw new IllegalArgumentException("Invalid cpf %s".formatted(number));
        }

        return new SocialNumber(number);
    }

    public static boolean isValid(final String number) {
        return true;
    }

    @Override
    public String toString() {
        return number;
    }
}
