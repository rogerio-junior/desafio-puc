package br.puc_rio.tecgraf.challenge.domain.payment;

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

    public PaymentHistory getHistory(SocialNumber employeeId, YearMonth payrollPeriod, Page.Range range) throws NotFoundException {
        final var employee = employeeRepository.getById(employeeId);

        final var criteria = new PaymentRepository.Criteria(employeeId, payrollPeriod);
        final var payments = paymentRepository.findByCriteria(criteria, range);

        final var historyEmployee = new PaymentHistory.Employee(
            employee.getId().getNumber(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.isActive()
        );
        final var entries = payments.map(payment -> new PaymentHistory.Entry(
            payment.year(),
            payment.month(),
            payment.amount()
        ));

        return new PaymentHistory(historyEmployee, entries);
    }
}
