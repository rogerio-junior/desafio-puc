package br.puc_rio.tecgraf.challenge.domain.payment;

import java.time.LocalDate;
import java.time.YearMonth;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

// Une a data de referência e o mês de competência.
// Garante que esses dois dados estejam coerentes entre
// as camadas do sistema, evitando múltiplas validações
// no mesmo dado.
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PayrollReference {
    private final LocalDate referenceDate;
    private final YearMonth period;

    public static PayrollReference of(final LocalDate referenceDate) {
        if (referenceDate == null) {
            return null;
        }

        final var period = YearMonth.from(referenceDate).minusMonths(1);

        return new PayrollReference(referenceDate, period);
    }
}
