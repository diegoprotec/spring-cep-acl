package com.santander.springcepapi.exception;

import com.santander.springcepapi.exception.constraint.ConstraintException;
import com.santander.springcepapi.exception.negocio.NegocioException;
import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public static ExceptionResponse badRequest(MethodArgumentNotValidException exception, WebRequest request) {
        List<Map<String, String>> validationErrors = processValidationErrors(
                exception.getBindingResult().getAllErrors()
        );
        return new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                Evento.getFormattedTimestamp(),
                INVALID_CALL_TITLE,
                INVALID_DATA_MESSAGE,
                request.getDescription(false),
                validationErrors
        );
    }

    public static ExceptionResponse constraintViolation(ConstraintException exception, WebRequest request) {
        List<Map<String, String>> validationErrors = processConstraintViolations(
                exception.getViolations()
        );
        return new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                Evento.getFormattedTimestamp(),
                INVALID_CALL_TITLE,
                INVALID_DATA_MESSAGE,
                request.getDescription(false),
                validationErrors
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

    public static ExceptionResponse internalError(InternalServerErrorException exception, WebRequest request) {
        return new ExceptionResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                Evento.getFormattedTimestamp(),
                "Error de aplicação",
                exception.getMessage(),
                request.getDescription(false),
                new ArrayList<>()
        );
    }

    private static List<Map<String, String>> processValidationErrors(List<ObjectError> erros) {
        return erros.stream()
                .filter(erro -> erro instanceof FieldError)
                .map(erro -> (FieldError) erro)
                .filter(erro -> erro.getDefaultMessage() != null)
                .map(erro -> Map.of(erro.getField(), erro.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    private static List<Map<String, String>> processConstraintViolations(
            Set<ConstraintViolation<?>> violacoes
    ) {
        return violacoes.stream()
                .map(ConstraintViolation::getMessage)
                .map(mensagem -> Map.of(ERROR_FIELD, mensagem))
                .collect(Collectors.toList());
    }
}