package br.puc_rio.tecgraf.challenge.application.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.Presenter;
import br.puc_rio.tecgraf.challenge.domain.payment.EmployeePayment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class PaymentCsvPresenter implements Presenter<Page<EmployeePayment>, byte[]> {
    // Marca de ordem de bytes, sem ela o Excel abre o UTF-8 como ANSI e quebra os acentos ("MÃªs")
    private static final char UTF8_BOM = '\uFEFF';

    @Getter private final char separator;
    @Getter private final Charset charset;

    @Override
    public byte[] present(final Page<EmployeePayment> payments) {
        final var csv = new StringBuilder();

        if (charset.equals(StandardCharsets.UTF_8)) {
            csv.append(UTF8_BOM);
        }

        // Header
        appendCsv(csv, separator, "CPF", "Nome", "Sobrenome", "Mês", "Ano", "Salário a receber");

        // Rows
        for (var payment : payments.items()) {
            appendCsv(csv, separator, 
                payment.employeeId(), 
                payment.firstName(), 
                payment.lastName(), 
                String.valueOf(payment.month()), 
                String.valueOf(payment.year()), 
                formatAmount(payment.amount()));
        }

        return csv.toString().getBytes(charset);
    }

    // Valor numérico puro para a planilha reconhecer como número.
    private String formatAmount(final BigDecimal amount) {
        final var value = amount.setScale(2, RoundingMode.HALF_UP).toPlainString();

        // Usa ',' como separador, seguindo padrão Brasileiro
        return value.replace('.', ',');
    }

    private void appendCsv(StringBuilder builder, char separator, String... values) {
        for (var value : values) {
            builder.append(escapeCsv(value)).append(separator);
        }

        builder.replace(builder.length() - 1, builder.length(), "\n");
    }

    private String escapeCsv(final String value) {
        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
