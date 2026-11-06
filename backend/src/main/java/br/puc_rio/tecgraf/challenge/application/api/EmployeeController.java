package br.puc_rio.tecgraf.challenge.application.api;

import java.time.YearMonth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.puc_rio.tecgraf.challenge.domain.NotFoundException;
import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeCep;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeRepository;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentHistory;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentHistoryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final PaymentHistoryService paymentHistoryService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/{employeeId}/payments")
    public PaymentHistory findPayments(
        @PathVariable SocialNumber employeeId,
        @RequestParam(required = false) YearMonth month,
        @RequestParam(defaultValue = "25") @Min(1) @Max(500) Integer limit,
        @RequestParam(defaultValue = "0") @Min(0) Integer offset
    ) throws NotFoundException {
        var range = new Page.Range(offset, limit);

        return paymentHistoryService.getHistory(employeeId, month, range);
    }

    @GetMapping
    public Page<EmployeeCep> findByActiveAndCep(
        @RequestParam(required = false) String cep,
        @RequestParam(defaultValue = "25") @Min(1) @Max(500) Integer limit,
        @RequestParam(defaultValue = "0") @Min(0) Integer offset
    ) {
        final var range = new Page.Range(offset, limit);

        return employeeRepository.findByActiveAndCep(true, cep, range);
    }
}
