package br.puc_rio.tecgraf.challenge.application.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.puc_rio.tecgraf.challenge.domain.payment.PaymentRepository;
import br.puc_rio.tecgraf.challenge.domain.payment.PayrollReference;
import br.puc_rio.tecgraf.challenge.domain.payment.SalaryService;
import br.puc_rio.tecgraf.challenge.domain.payment.SalaryService.ComputeSalaryResult;

@WebMvcTest(PaymentController.class)
class PaymentControllerITest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalaryService salaryService;

    @MockitoBean
    private PaymentRepository paymentRepository;

    @Test
    void calculate_shouldAcceptRequest() throws Exception {
        // Given
        final var referenceDate = LocalDate.of(2026, 5, 1);
        final var requestBody = """
            {"referenceDate": "2026-05-01"}""";

        when(salaryService.computeSalary(referenceDate)).thenReturn(mockComputeSalaryResult(referenceDate));

        // When & Then
        mockMvc.perform(post("/payments/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payrollRef.referenceDate").value("2026-05-01"))
            .andExpect(jsonPath("$.payrollRef.period").value("2026-04"))
            .andExpect(jsonPath("$.expectedQuantity").value(12))
            .andExpect(jsonPath("$.generatedQuantity").value(15));
    }

    // TODO: Alguns formatos deveriam cair na mensagem de unreadable body
    @ParameterizedTest
    @ValueSource(strings = {
        "{\"referenceDate\": \"\"}",
        "{\"referenceDate\": 202605}",
        "{\"referenceDate\": null}",
        "{\"month\": [2026, 5]}",
        "{}"
    })
    void calculate_shouldRejectWhenInvalidRequest(final String body) throws Exception {
        // When & Then
        mockMvc.perform(post("/payments/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.detail").value("One field is invalid"))
            .andExpect(jsonPath("$.errors[0].field").value("referenceDate"))
            .andExpect(jsonPath("$.errors[0].message").value(PaymentController.CalculateRequest.INVALID_REFERENCE_DATE));

        verify(salaryService, never()).computeSalary(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{\"referenceDate\": \"2026-13-01\"}",
        "{\"referenceDate\": \"2026-00-01\"}",
        "{\"referenceDate\": \"2026-5-1\"}",
        "{\"referenceDate\": \"05-2026\"}",
        "{\"referenceDate\": \"01/05/2026\"}",
        "{\"referenceDate\": \"0999-01\"}",
    })
    void calculate_shouldPointFieldWhenDateHasInvalidFormat(String body) throws Exception {
        mockMvc.perform(post("/payments/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.detail").value("One field is invalid"))
            .andExpect(jsonPath("$.errors[0].field").value("referenceDate"))
            .andExpect(jsonPath("$.errors[0].message").value(FieldViolation.Messages.LOCAL_DATE));

        verify(salaryService, never()).computeSalary(any());
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "not json" })
    void calculate_shouldRejectWhenUnreadableBody(String body) throws Exception {
        mockMvc.perform(post("/payments/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.detail").value("Request body is missing or is not valid JSON"));

        verify(salaryService, never()).computeSalary(any());
    }

    private SalaryService.ComputeSalaryResult mockComputeSalaryResult(LocalDate refDate) {
        return new ComputeSalaryResult(PayrollReference.of(refDate), 12, 15);
    }
}
