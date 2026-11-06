package br.puc_rio.tecgraf.challenge.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SocialNumberUTest {
    @ParameterizedTest
    @ValueSource(strings = { "92572993848", "925.729.938-48" })
    void of_shouldStoreOnlyDigits(final String number) {
        assertThat(SocialNumber.of(number).getNumber()).isEqualTo("92572993848");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "1", "925729938480", "9257299384a", "925.729.93848", "925-729-938.48" })
    void isValid_shouldRejectInvalidFormat(final String number) {
        assertThat(SocialNumber.isValid(number)).isFalse();
    }

    @Test
    void of_shouldThrowOnInvalidFormat() {
        assertThatThrownBy(() -> SocialNumber.of("1"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
