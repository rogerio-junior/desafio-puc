package br.puc_rio.tecgraf.challenge.application.api;

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
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentRepository;
import br.puc_rio.tecgraf.challenge.domain.payment.SalaryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final SalaryService salaryService;
    private final PaymentRepository paymentRepository;

    @PostMapping("/calculate")
    public SalaryService.ComputeSalaryResult calculate(@Valid @RequestBody CalculateRequest request) {
        return salaryService.computeSalaryOf(request.referenceDate());
    }

    public record CalculateRequest(
        @NotNull(message = INVALID_REFERENCE_DATE)
        @PastOrPresent(message = INVALID_REFERENCE_DATE)
        LocalDate referenceDate
    ){
        final static String INVALID_REFERENCE_DATE = "only past or present date in the format 'YYYY-MM-DD' (ISO 8601)";
    }

    @GetMapping(produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv(@Valid ExportCsvRequest request) {
        final var cursor = new Page.Cursor(String.valueOf(0), Integer.MAX_VALUE);
        final var criteria = new PaymentRepository.Criteria(null, request.month());

        final var payments = paymentRepository.findByCriteria(criteria, cursor);

        final var csvPresenter = new PaymentCsvPresenter(payments, ',', StandardCharsets.UTF_8);
        final var csv = csvPresenter.present();
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users.csv\"")
            .contentType(new MediaType("text", "csv", csvPresenter.getCharset()))
            .body(csv);
    }

    public record ExportCsvRequest(
        @RequestParam
        @NotNull
        YearMonth month
    ){ }
}
