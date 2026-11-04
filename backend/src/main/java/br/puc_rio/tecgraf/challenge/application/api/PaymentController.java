package br.puc_rio.tecgraf.challenge.application.api;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/calculate")
    public SalaryService.ComputeSalaryResult calculate(@Valid @RequestBody CalculateRequest request) {
        return salaryService.computeSalayOf(request.referenceDate());
    }

    public record CalculateRequest(
        @NotNull(message = INVALID_REFERENCE_DATE)
        @PastOrPresent(message = INVALID_REFERENCE_DATE)
        LocalDate referenceDate
    ){
        final static String INVALID_REFERENCE_DATE = "only past or present date in the format 'YYYY-MM-DD' (ISO 8601)";
    }
}
