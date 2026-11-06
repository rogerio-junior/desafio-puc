package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;
import java.time.YearMonth;

import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {
    @EqualsAndHashCode.Include private final SocialNumber employeeId;
    @EqualsAndHashCode.Include private final YearMonth period;
    private final BigDecimal amount;
}
