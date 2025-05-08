package com.santander.springcepapi.exception.handler;

import com.santander.springcepapi.exception.NegocioException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public record ExceptionResponse(
        int status,
        String timestamp,
        String titulo,
        String descricao,
        String path,
        List<Map<String, String>> errors
) {
    private static final String INVALID_CALL_TITLE = "Chamada inválida";
    private static final String INVALID_DATA_MESSAGE = "Error nos dados fornecidos para o recurso";
    private static final String ERROR_FIELD = "error";

    public static ExceptionResponse notFound(NotFoundException exception, WebRequest request) {
        return new ExceptionResponse(
                HttpStatus.NOT_FOUND.value(),
                Evento.getFormattedTimestamp(),
                "Recurso não encontrado",
                exception.getMessage(),
                request.getDescription(false),
                new ArrayList<>()
        );
    }

    public static ExceptionResponse handlerMethodValidationException(
            HandlerMethodValidationException exception, WebRequest request) {
        return new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                Evento.getFormattedTimestamp(),
                INVALID_CALL_TITLE,
                INVALID_DATA_MESSAGE,
                request.getDescription(false),
                getErrorField(exception)
        );
    }

    public static ExceptionResponse methodArgumentNotValidException(
            MethodArgumentNotValidException exception, WebRequest request) {
        return new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                Evento.getFormattedTimestamp(),
                INVALID_CALL_TITLE,
                INVALID_DATA_MESSAGE,
                request.getDescription(false),
                processValidationErrors(exception)
        );
    }

    public static ExceptionResponse constraintViolationException(
            ConstraintViolationException exception, WebRequest request) {
        return new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                Evento.getFormattedTimestamp(),
                INVALID_CALL_TITLE,
                INVALID_DATA_MESSAGE,
                request.getDescription(false),
                processConstraintViolations(exception)
        );
    }

    public static ExceptionResponse businessError(NegocioException exception, WebRequest request) {
        return new ExceptionResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                Evento.getFormattedTimestamp(),
                "Negócio",
                exception.getMessage(),
                request.getDescription(false),
                new ArrayList<>()
        );
    }

    public static ExceptionResponse internalError(Exception exception, WebRequest request) {
        var cause = getRootCause(exception);
        var descricao = cause instanceof InternalServerErrorException ? cause.getMessage() : "";
        return new ExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Evento.getFormattedTimestamp(),
                "Error interno da aplicação",
                descricao,
                request.getDescription(false),
                new ArrayList<>()
        );
    }

    private static List<Map<String, String>> getErrorField(HandlerMethodValidationException exception) {
        return exception.getParameterValidationResults().stream()
                .flatMap(validationResult -> validationResult
                        .getResolvableErrors().stream()
                        .map(error -> Map.of(
                                ERROR_FIELD,
                                error.getDefaultMessage() != null ? error.getDefaultMessage() : "Erro de validação")
                        )).toList();
    }

    private static List<Map<String, String>> processValidationErrors(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getAllErrors().stream()
                .filter(erro -> erro instanceof FieldError)
                .map(erro -> (FieldError) erro)
                .filter(erro -> erro.getDefaultMessage() != null)
                .map(erro -> Map.of(erro.getField(), erro.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    private static List<Map<String, String>> processConstraintViolations(ConstraintViolationException exception) {
        return exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .map(mensagem -> Map.of(ERROR_FIELD, mensagem))
                .collect(Collectors.toList());
    }

    private static Throwable getRootCause(Throwable throwable) {
        Set<Throwable> visited = new HashSet<>();
        Throwable cause = throwable;

        while (cause.getCause() != null && !visited.contains(cause.getCause())) {
            visited.add(cause);
            cause = cause.getCause();
        }

        return cause;
    }

}