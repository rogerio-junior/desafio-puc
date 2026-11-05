package br.puc_rio.tecgraf.challenge.application.api;

import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.Locale;

import br.puc_rio.tecgraf.challenge.domain.Page;
import br.puc_rio.tecgraf.challenge.domain.Presenter;
import br.puc_rio.tecgraf.challenge.domain.payment.PaymentInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class PaymentCsvPresenter implements Presenter<Page<PaymentInfo>, byte[]> {
    private static final NumberFormat FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

    @Getter private final char separator; // TODO: Validar , e ;
    @Getter private final Charset charset;

    @Override
    public byte[] present(final Page<PaymentInfo> payments) {
        final var csv = new StringBuilder();

        // Header
        appendCsv(csv, separator, "CPF", "Nome", "Sobrenome", "Mês", "Ano", "Salário a receber");

        // Rows
        for (var payment : payments.items()) {
            appendCsv(csv, separator, 
                payment.idEmployee(), 
                payment.firstName(), 
                payment.lastName(), 
                String.valueOf(payment.month()), 
                String.valueOf(payment.year()), 
                FORMATTER.format(payment.salary()));
        }

        return csv.toString().getBytes(charset);
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
