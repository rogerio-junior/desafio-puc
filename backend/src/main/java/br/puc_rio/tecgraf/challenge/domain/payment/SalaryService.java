package br.puc_rio.tecgraf.challenge.domain.payment;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalaryService {
    private final PaymentRepository paymentRepository;

    public ComputeSalaryResult computeSalayOf(LocalDate referenceDate) {
        var payrollRef = new PayrollReference(referenceDate);
        var pendings = paymentRepository.findPendingsOn(payrollRef.getPeriod());
        var payments = pendings.stream().map(p -> computeSalary(p, payrollRef)).toList();

        // Em aplicações de grande porte, usaria processamento assíncrono, provavelmente em 2 etapas:
        // 1) Identificação dos funcionários pendentes paginado, enfileirando os lotes em uma fila (e.g. SQS);
        // 2) Consumer processa cada lote
        var generatedQuantity = paymentRepository.insertPayments(payments);

        return new ComputeSalaryResult(payrollRef, payments.size(), generatedQuantity);
    }

    private Payment computeSalary(PaymentEmployee p, PayrollReference payrollRef) {
        var salary = p.computeSalary(payrollRef);

        return new Payment(p.getSocialNumber(), payrollRef.getPeriod(), salary);
    }

    public record ComputeSalaryResult(PayrollReference payrollRef, int expectedQuantity, int generatedQuantity) {}
}
