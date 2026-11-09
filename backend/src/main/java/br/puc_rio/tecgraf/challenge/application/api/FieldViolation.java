package br.puc_rio.tecgraf.challenge.application.api;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

import br.puc_rio.tecgraf.challenge.domain.SocialNumber;

// Representa um campo inválido em uma requisição, usando para detalhar "errors" do ProblemDetail
record FieldViolation(String field, String message) {
    // Tipos diferentes dos tradicionais (int, bool, ...) que precisam de mensagem própria para
    // o usuário entender como preencher
    private static final Map<Class<?>, String> TYPE_MESSAGES = Map.of(
        YearMonth.class, Messages.YEAR_MONTH,
        LocalDate.class, Messages.LOCAL_DATE,
        SocialNumber.class, Messages.CPF
    );

    static FieldViolation ofType(String field, Class<?> type) {
        return new FieldViolation(field, TYPE_MESSAGES.getOrDefault(type, "must be of type " + type.getSimpleName()));
    }

    // Mensagens padrão de campos inválidos
    static final class Messages {
        static final String PAGE_LIMIT = "must be an integer between 1 and 500";
        static final String PAGE_OFFSET = "must be an integer greater than or equal to 0";
        static final String YEAR_MONTH = "must be a string in ISO 8601 format YYYY-MM (e.g. \"2026-05\")";
        static final String LOCAL_DATE = "must be a string in ISO 8601 format YYYY-MM-DD (e.g. \"2026-05-01\")";
        static final String CPF = "must be a CPF with 11 digits (e.g. \"92572993848\")";
        static final String CEP = "must be a CEP in the format 00000-000 (e.g. \"01310-100\")";

        private Messages() { }
    }
}
