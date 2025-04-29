package com.santander.springcepapi.boundary.cep.rest.doc;

import com.santander.springcepapi.boundary.cep.domain.CepVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "CEP", description = "API para gerenciamento de CEPs")
public interface CepResourceDoc {

    @Operation(summary = "Buscar CEP específico",
            description = "Retorna os dados de endereço para o CEP informado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CEP encontrado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CepVo.class))),
            @ApiResponse(responseCode = "404", description = "CEP não encontrado"),
            @ApiResponse(responseCode = "400", description = "CEP inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    CepVo buscaCep(@Parameter(description = "CEP a ser pesquisado (formato: 00000-000 ou 00000000)") String cep);

    @Operation(summary = "Listar todos os CEPs",
            description = "Retorna uma lista com todos os CEPs cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de CEPs recuperada com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CepVo.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    List<CepVo> listarCeps();
}