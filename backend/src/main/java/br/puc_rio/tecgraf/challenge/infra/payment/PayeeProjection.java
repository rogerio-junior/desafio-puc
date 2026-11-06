package br.puc_rio.tecgraf.challenge.infra.payment;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.puc_rio.tecgraf.challenge.domain.Gender;

public record PayeeProjection(
    String socialNumber,
    String type,
    LocalDate birthDate,
    Gender gender,
    BigDecimal baseSalary,
    int workedWeekendDays
) {}
