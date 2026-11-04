package br.puc_rio.tecgraf.challenge.domain.payment;

import java.time.LocalDate;
import java.time.YearMonth;

import lombok.Data;

@Data
public class PayrollReference {
    private final LocalDate referenceDate;
    private final YearMonth period;

    public PayrollReference(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
        this.period = YearMonth.from(referenceDate).minusMonths(1);
    }
}
