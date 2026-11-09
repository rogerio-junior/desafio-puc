package br.puc_rio.tecgraf.challenge.application.api;

import static br.puc_rio.tecgraf.challenge.application.api.FieldViolation.Messages.PAGE_LIMIT;
import static br.puc_rio.tecgraf.challenge.application.api.FieldViolation.Messages.PAGE_OFFSET;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.payment.EmployeePayment;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentRepository;
import br.puc_rio.tecgraf.challenge.domain.payment.SalaryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final SalaryService salaryService;
    private final PaymentRepository paymentRepository;
    private final PaymentCsvPresenter csvPresenter = new PaymentCsvPresenter(',', StandardCharsets.UTF_8);

    @PostMapping("/calculate")
    public SalaryService.ComputeSalaryResult calculate(@Valid @RequestBody CalculateRequest request) {
        return salaryService.computeSalary(request.referenceDate());
    }

    public record CalculateRequest(
        @NotNull(message = INVALID_REFERENCE_DATE)
        @PastOrPresent(message = INVALID_REFERENCE_DATE)
        LocalDate referenceDate
    ){
        final static String INVALID_REFERENCE_DATE = "must be a past or present date in ISO 8601 format YYYY-MM-DD (e.g. \"2026-05-01\")";
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<EmployeePayment> findByMonth(
        @RequestParam YearMonth month,
        @RequestParam(defaultValue = "25") @Min(value = 1, message = PAGE_LIMIT) @Max(value = 500, message = PAGE_LIMIT) Integer limit,
        @RequestParam(defaultValue = "0") @Min(value = 0, message = PAGE_OFFSET) Integer offset
    ) {
        final var range = new Page.Range(offset, limit);
        final var criteria = new PaymentRepository.Criteria(null, month);

        return paymentRepository.findByCriteria(criteria, range);
    }

    @GetMapping(produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(@RequestParam YearMonth month) {
        final var range = new Page.Range(0, Integer.MAX_VALUE);
        final var criteria = new PaymentRepository.Criteria(null, month);

        final var payments = paymentRepository.findByCriteria(criteria, range);

        final var csv = csvPresenter.present(payments);
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"pagamentos-%s.csv\"".formatted(month))
            .contentType(new MediaType("text", "csv", csvPresenter.getCharset()))
            .body(csv);
    }
}
