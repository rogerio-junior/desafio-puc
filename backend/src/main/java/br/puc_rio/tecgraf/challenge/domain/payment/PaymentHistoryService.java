package br.puc_rio.tecgraf.challenge.domain.payment;

import java.math.BigDecimal;
import java.time.YearMonth;

import br.puc_rio.tecgraf.challenge.domain.NotFoundException;
import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.SocialNumber;
import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentHistoryService {
    private final PaymentRepository paymentRepository;
    private final EmployeeRepository employeeRepository;

    public FindPaymentsResponse findHistory(SocialNumber employeeId, YearMonth payrollPeriod, Page.Cursor cursor) throws NotFoundException {
        final var employee = employeeRepository.getBySocialNumber(employeeId);
        
        final var criteria = new PaymentRepository.Criteria(employeeId, payrollPeriod);
        final var payments = paymentRepository.findByCriteria(criteria, cursor);

        final var employeeResult = new FindPaymentsResponse.Employee(
            employee.getSocialNumber().getNumber(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.isActive()
        );
        final var paymentsResult = payments.map(pi -> new FindPaymentsResponse.Payment(
            pi.year(),
            pi.month(),
            pi.salary()
        ));

        return new FindPaymentsResponse(employeeResult, paymentsResult);
    }

    // TODO: Melhorar nome do retorno
    public record FindPaymentsResponse(
        FindPaymentsResponse.Employee employee,
        Page<FindPaymentsResponse.Payment> paymentsPage
    ){
        public record Employee(
            String id,
            String firstName,
            String lastName,
            boolean active
        ){ }

        public record Payment(
            int year,
            int month,
            BigDecimal salaryToReceive
        ){ }
    }
}
