package br.puc_rio.tecgraf.challenge.application.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeRepository;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentHistoryService;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerITest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentHistoryService paymentHistoryService;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Test
    void findByActiveAndCep_shouldReportJavaTypeWhenTypeHasNoMessage() throws Exception {
        expectInvalidField(mockMvc.perform(get("/employees").param("active", "tata")), "active", "must be of type boolean");
    }

    @Test
    void findByActiveAndCep_shouldRejectLimitOutOfRange() throws Exception {
        expectInvalidField(mockMvc.perform(get("/employees").param("limit", "0")), "limit", FieldViolation.Messages.PAGE_LIMIT);
    }

    @Test
    void findByActiveAndCep_shouldRejectNegativeOffset() throws Exception {
        expectInvalidField(mockMvc.perform(get("/employees").param("offset", "-1")), "offset", FieldViolation.Messages.PAGE_OFFSET);
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "01310-100" })
    void findByActiveAndCep_shouldAcceptValidCep(String cep) throws Exception {
        mockMvc.perform(get("/employees").param("cep", cep))
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "01310", "01310100", "01310-1000", "abcde-fgh" })
    void findByActiveAndCep_shouldRejectInvalidCep(String cep) throws Exception {
        expectInvalidField(mockMvc.perform(get("/employees").param("cep", cep)), "cep", FieldViolation.Messages.CEP);
    }

    @Test
    void findByActiveAndCep_shouldReportAllInvalidFields() throws Exception {
        mockMvc.perform(get("/employees").param("limit", "0").param("offset", "-1"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Multiple fields are invalid"))
            .andExpect(jsonPath("$.errors.length()").value(2));
    }

    @Test
    void findPayments_shouldRejectInvalidCpf() throws Exception {
        expectInvalidField(mockMvc.perform(get("/employees/123/payments")), "employeeId", FieldViolation.Messages.CPF);
    }

    @Test
    void findPayments_shouldRejectInvalidMonth() throws Exception {
        expectInvalidField(mockMvc.perform(get("/employees/92572993848/payments").param("month", "05-2026")), "month", FieldViolation.Messages.YEAR_MONTH);
    }

    private void expectInvalidField(ResultActions result, String field, String message) throws Exception {
        result
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.detail").value("One field is invalid"))
            .andExpect(jsonPath("$.errors.length()").value(1))
            .andExpect(jsonPath("$.errors[0].field").value(field))
            .andExpect(jsonPath("$.errors[0].message").value(message));
    }
}
