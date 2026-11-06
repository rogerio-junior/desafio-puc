package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;

import br.puc_rio.tecgraf.challenge.domain.Page;

// Histórico de pagamentos de um funcionário
public record PaymentHistory(
    PaymentHistory.Employee employee,
    Page<PaymentHistory.Entry> payments
){
    public record Employee(
        String id,
        String firstName,
        String lastName,
        boolean active
    ){ }

    public record Entry(
        int year,
        int month,
        BigDecimal amount
    ){ }
}
