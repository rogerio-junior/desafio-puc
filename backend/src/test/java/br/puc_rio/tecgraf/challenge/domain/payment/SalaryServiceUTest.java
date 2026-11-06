package br.puc_rio.tecgraf.challenge.domain.payment;

import static br.puc_rio.tecgraf.challenge.domain.payment.PayeeFactory.payee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.puc_rio.tecgraf.challenge.domain.SocialNumber;

@ExtendWith(MockitoExtension.class)
class SalaryServiceUTest {
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private SalaryService sut;

    @Captor
    private ArgumentCaptor<List<Payment>> paymentsCaptor;

    @Test
    void computeSalayOf_shouldSuccessCompute() {
        // Given
        final var referenceDate = LocalDate.of(2026, 11, 4);
        final var payrollPeriod = YearMonth.of(2026, 10);

        when(paymentRepository.findPendingPayeesOn(payrollPeriod))
            .thenReturn(List.of(payee("92572993848"), payee("09530965214")));
        when(paymentRepository.insertPayments(anyList())).thenReturn(1);

        // When
        final var result = sut.computeSalaryOf(referenceDate);

        // Then
        verify(paymentRepository).insertPayments(paymentsCaptor.capture());

        assertThat(result.payrollRef().getReferenceDate()).isEqualTo(referenceDate);
        assertThat(result.payrollRef().getPeriod()).isEqualTo(payrollPeriod);
        assertThat(result.expectedQuantity()).isEqualTo(2);
        assertThat(result.generatedQuantity()).isEqualTo(1);
        assertThat(paymentsCaptor.getValue())
            .extracting(Payment::getEmployeeId, Payment::getYearMonth, Payment::getAmount)
            .containsExactly(
                tuple(SocialNumber.of("92572993848"), payrollPeriod, new BigDecimal("1000.00")),
                tuple(SocialNumber.of("09530965214"), payrollPeriod, new BigDecimal("1000.00")));
    }
}
