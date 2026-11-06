package br.puc_rio.tecgraf.challenge.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.puc_rio.tecgraf.challenge.domain.employee.EmployeeRepository;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentHistoryService;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentRepository;
import br.puc_rio.tecgraf.challenge.domain.payment.SalaryService;

// Registra os serviços aqui para manter o pacote de domínio livre de anotações do Spring
@Configuration
public class DomainServiceConfig {
    @Bean
    public SalaryService salaryService(PaymentRepository paymentRepository) {
        return new SalaryService(paymentRepository);
    }

    @Bean
    public PaymentHistoryService paymentHistoryService(PaymentRepository paymentRepository, EmployeeRepository employeeRepository) {
        return new PaymentHistoryService(paymentRepository, employeeRepository);
    }
}
