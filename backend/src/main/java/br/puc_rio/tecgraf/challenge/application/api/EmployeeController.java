package br.puc_rio.tecgraf.challenge.application.api;

import java.math.BigDecimal;
import java.time.YearMonth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.Employee;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeRepository;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentRepository;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final PaymentRepository paymentRepository;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/{cpf}/payments")
    public Page<FindPaymentsResponse> findPayments(
        @PathVariable String cpf,
        @RequestParam(required = false) YearMonth month,
        @RequestParam(defaultValue = "25") @Max(500) Integer limit,
        @RequestParam(defaultValue = "0") Integer offset
    ) {
        var cursor = new Page.Cursor(String.valueOf(offset), limit);
        var criteria = new PaymentRepository.Criteria(SocialNumber.of(cpf), month);

        // Usa conceito de presenter para converter o output no formato desejado
        return paymentRepository.findByCriteria(criteria, cursor)
            .map(p -> new FindPaymentsResponse(p.year(), p.month(), p.salary()));
    }

    public record FindPaymentsResponse(
        int year,
        int month,
        BigDecimal salaryToReceive
    ){ }
    
    @GetMapping
    public Page<Employee> findByActiveAndCep(
        @RequestParam(required = false) String cep,
        @RequestParam(defaultValue = "25") @Max(500) Integer limit,
        @RequestParam(defaultValue = "0") Integer offset
    ) {
        final var cursor = new Page.Cursor(String.valueOf(offset), limit);

        return employeeRepository.findByActiveAndCep(true, cep, cursor);
    }
}
