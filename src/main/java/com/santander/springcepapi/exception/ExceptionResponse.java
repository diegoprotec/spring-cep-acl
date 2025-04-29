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

public class ExceptionResponse {

    private final int status;
    private final String timestamp;
    private final String titulo;
    private final String descricao;
    private final String path;
    private final List<Map<String, String>> errors = new ArrayList<>();

    public ExceptionResponse(NotFoundException e, WebRequest request) {
        this.status = HttpStatus.NOT_FOUND.value();
        this.timestamp = Evento.getFormattedTimestamp();
        this.titulo = "Recurso não encontrado";
        this.descricao = e.getMessage();
        this.path = request.getDescription(false);
    }

    public ExceptionResponse(MethodArgumentNotValidException ex, WebRequest request) {
        processarViolacoes(ex.getBindingResult().getAllErrors());
        this.status = HttpStatus.BAD_REQUEST.value();
        this.timestamp = Evento.getFormattedTimestamp();
        this.titulo = "Chamada inválida";
        this.descricao = "Error nos dados fornecidos para o recurso";
        this.path = request.getDescription(false);
    }

    public ExceptionResponse(InternalServerErrorException e, WebRequest request) {
        this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.timestamp = Evento.getFormattedTimestamp();
        this.titulo = "Error de aplicação";
        this.descricao = e.getMessage();
        this.path = request.getDescription(false);
    }

    public ExceptionResponse(ConstraintException ex, WebRequest request) {
        processarViolacoes(ex.getViolations());
        this.status = HttpStatus.BAD_REQUEST.value();
        this.timestamp = Evento.getFormattedTimestamp();
        this.titulo = "Chamada inválida";
        this.descricao = "Error nos dados fornecidos para o recurso";
        this.path = request.getDescription(false);
    }

    public ExceptionResponse(NegocioException e, WebRequest request) {
        this.status = HttpStatus.UNPROCESSABLE_ENTITY.value();
        this.timestamp = Evento.getFormattedTimestamp();
        this.titulo = "Negócio";
        this.descricao = e.getMessage();
        this.path = request.getDescription(false);
    }

    private void processarViolacoes(List<ObjectError> erros) {
        erros.forEach(erro -> {
            if (erro instanceof FieldError erroField) {
                String nomeCampo = erroField.getField();
                String mensagemErro = erro.getDefaultMessage();
                if (mensagemErro != null) {
                    errors.add(Map.of(nomeCampo, mensagemErro));
                }
            }
        });
    }

    private void processarViolacoes(Set<ConstraintViolation<?>> violacoes) {
        this.errors.addAll(
                violacoes.stream()
                        .map(ConstraintViolation::getMessage)
                        .map(mensagem -> Map.of("error", mensagem))
                        .toList()
        );
    }

    public int getStatus() {
        return status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getPath() {
        return path;
    }

    public List<Map<String, String>> getErros() {
        return errors;
    }


}
