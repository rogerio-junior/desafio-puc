package br.puc_rio.tecgraf.challenge.application.api;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.puc_rio.tecgraf.challenge.domain.NotFoundException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.MismatchedInputException;

// Erros seguem o formato ProblemDetail (RFC 9457) em todos os endpoints.
// Erros de input informam cada campo inválido na extensão "errors"
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String INVALID_FIELD = "One field is invalid";
    private static final String INVALID_FIELDS = "Multiple fields are invalid";

    // HTTP 400: Validações de parâmetros de path/query (@Min, @Max, ...)
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var errors = ex.getParameterValidationResults().stream()
            .flatMap(result -> result.getResolvableErrors().stream()
                .map(error -> new FieldViolation(result.getMethodParameter().getParameterName(), error.getDefaultMessage())))
            .toList();

        return invalidFields(ex, errors, headers, status, request);
    }

    // HTTP 400: Parâmetros de path/query com tipo diferente do esperado
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var error = FieldViolation.ofType(ex.getPropertyName(), ex.getRequiredType());

        return invalidFields(ex, List.of(error), headers, status, request);
    }
    
    // HTTP 400: Parâmetros de query obrigatórios (@RequestParam(required = true))
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var error = new FieldViolation(ex.getParameterName(), "is required");

        return invalidFields(ex, List.of(error), headers, status, request);
    }

    // HTTP 400: Validações do request body (@Valid @RequestBody)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new FieldViolation(error.getField(), error.getDefaultMessage()))
            .toList();

        return invalidFields(ex, errors, headers, status, request);
    }

    // HTTP 400: Request body com JSON inválido ou contendo tipos incorretos
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex.getCause() instanceof MismatchedInputException cause && cause.getTargetType() != null && !cause.getPath().isEmpty()) {
            var field = cause.getPath().stream()
                .map(JacksonException.Reference::getPropertyName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("."));

            return invalidFields(ex, List.of(FieldViolation.ofType(field, cause.getTargetType())), headers, status, request);
        }

        var body = ProblemDetail.forStatusAndDetail(status, "Request body is missing or is not valid JSON");

        return handleExceptionInternal(ex, body, headers, status, request);
    }

    // HTTP 404: Lançado quando uma entidade não é encontrada
    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundEntity(NotFoundException ex, WebRequest request) {
        var status = HttpStatus.NOT_FOUND;

        return handleExceptionInternal(ex, ProblemDetail.forStatusAndDetail(status, ex.getMessage()), new HttpHeaders(), status, request);
    }

    private ResponseEntity<Object> invalidFields(Exception ex, List<FieldViolation> errors, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var body = ProblemDetail.forStatusAndDetail(status, errors.size() == 1 ? INVALID_FIELD : INVALID_FIELDS);
        body.setProperty("errors", errors);

        return handleExceptionInternal(ex, body, headers, status, request);
    }
}
