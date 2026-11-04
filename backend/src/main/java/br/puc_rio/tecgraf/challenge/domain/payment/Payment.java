package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;
import java.time.YearMonth;

import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Payment {
    private final SocialNumber employeeId;
    private final YearMonth yearMonth;
    private final BigDecimal amount;
}
