package br.puc_rio.tecgraf.challenge.domain.payment;

import java.time.YearMonth;
import java.util.List;

public interface PaymentRepository {
    List<PaymentEmployee> findPendingsOn(YearMonth yearMonth);
    
    int insertPayments(List<Payment> payments);
}
