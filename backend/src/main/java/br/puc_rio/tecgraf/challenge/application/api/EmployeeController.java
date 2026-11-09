package br.puc_rio.tecgraf.challenge.application.api;

import static br.puc_rio.tecgraf.challenge.application.api.FieldViolation.Messages.CEP;
import static br.puc_rio.tecgraf.challenge.application.api.FieldViolation.Messages.PAGE_LIMIT;
import static br.puc_rio.tecgraf.challenge.application.api.FieldViolation.Messages.PAGE_OFFSET;

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
import jakarta.validation.constraints.Pattern;
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
        @RequestParam(defaultValue = "25") @Min(value = 1, message = PAGE_LIMIT) @Max(value = 500, message = PAGE_LIMIT) Integer limit,
        @RequestParam(defaultValue = "0") @Min(value = 0, message = PAGE_OFFSET) Integer offset
    ) throws NotFoundException {
        var range = new Page.Range(offset, limit);

        return paymentHistoryService.getHistory(employeeId, month, range);
    }

    @GetMapping
    public Page<EmployeeCep> findByActiveAndCep(
        @RequestParam(defaultValue = "true") boolean active,
        @RequestParam(required = false) @Pattern(regexp = "(\\d{5}-\\d{3})?", message = CEP) String cep,
        @RequestParam(defaultValue = "25") @Min(value = 1, message = PAGE_LIMIT) @Max(value = 500, message = PAGE_LIMIT) Integer limit,
        @RequestParam(defaultValue = "0") @Min(value = 0, message = PAGE_OFFSET) Integer offset
    ) {
        final var range = new Page.Range(offset, limit);

        return employeeRepository.findByActiveAndCep(active, cep, range);
    }
}
