package br.puc_rio.tecgraf.challenge.domain.payment;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SalaryService {
    private final PaymentRepository paymentRepository;

    public ComputeSalaryResult computeSalary(final LocalDate referenceDate) {
        var payrollRef = PayrollReference.of(referenceDate);
        var pendings = paymentRepository.findPendingPayeesOn(payrollRef.getPeriod());
        
        var payments = pendings.stream().map(p -> toPayment(p, payrollRef)).toList();

        // Em aplicações de grande porte eu usaria processamento assíncrono, provavelmente em 2 etapas:
        // 1) Identificação dos funcionários pendentes paginado, enfileirando os lotes em uma fila (e.g. SQS);
        // 2) Consumer processa cada lote
        var generatedQuantity = paymentRepository.insertPayments(payments);

        return new ComputeSalaryResult(payrollRef, payments.size(), generatedQuantity);
    }

    private Payment toPayment(Payee p, PayrollReference payrollRef) {
        var salary = p.computeSalary(payrollRef);

        return new Payment(p.getEmployeeId(), payrollRef.getPeriod(), salary);
    }

    public record ComputeSalaryResult(PayrollReference payrollRef, int expectedQuantity, int generatedQuantity) {}
}
