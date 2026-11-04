package br.puc_rio.tecgraf.challenge.domain.payment;

import java.time.YearMonth;
import java.util.List;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;

public interface PaymentRepository {
    // Filtros nulos em Criteria são ignorados. Para a próxima página, use page.nexCursor(PaymentInfo::cursorKey)
    Page<PaymentInfo> findByCriteria(Criteria criteria, Page.Cursor cursor);

    List<PaymentEmployee> findPendingsOn(YearMonth yearMonth);
    
    int insertPayments(List<Payment> payments);

    public record Criteria(SocialNumber socialNumber, YearMonth payrollPeriod) { }
}
