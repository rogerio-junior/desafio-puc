package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;

// Retorno das consultas
public record PaymentInfo(
    long key,
    String idEmployee,
    String firstName,
    String lastName,
    int month,
    int year,
    BigDecimal salary
) { }
