package br.puc_rio.tecgraf.challenge.domain.payment;

import java.time.YearMonth;
import java.util.List;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;

public interface PaymentRepository {
    // Filtros nulos em Criteria são ignorados
    Page<EmployeePayment> findByCriteria(Criteria criteria, Page.Cursor cursor);

    List<Payee> findPendingPayeesOn(YearMonth payrollPeriod);
    
    int insertPayments(List<Payment> payments);

    public record Criteria(SocialNumber employeeId, YearMonth payrollPeriod) { }
}
