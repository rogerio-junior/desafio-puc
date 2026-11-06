package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;

// Projeção que une dados do funcionário com seu pagamento, usado pelas consultas
public record EmployeePayment(
    String employeeId,
    String firstName,
    String lastName,
    int month,
    int year,
    BigDecimal amount
) { }
