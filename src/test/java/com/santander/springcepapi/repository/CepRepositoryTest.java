package com.santander.springcepapi.repository;

import com.santander.springcepapi.model.entity.Cep;
import com.santander.springcepapi.model.vo.CepVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.test.StepVerifier;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class CepRepositoryTest {

    private static final String CEP_SE = "01001000";
    private static final String CEP_FARIA_LIMA = "04544000";
    private static final CepVo SE = new CepVo(CEP_SE,
            "Praça da Sé", "Sé", "São Paulo", "SP");
    private static final CepVo FARIA_LIMA = new CepVo(CEP_FARIA_LIMA,
            "Av. Brigadeiro Faria Lima", "Itaim Bibi", "São Paulo", "SP");

    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;

    @Mock
    private DynamoDbTable<Cep> cepTable;

    private CepRepository cepRepository;

    private AutoCloseable closeable;

    @BeforeEach
    void configurarAmbienteDeTeste() {
        closeable = MockitoAnnotations.openMocks(this);

        when(dynamoDbEnhancedClient.table(eq(Cep.CEP_TABLE_NAME), eq(TableSchema.fromBean(Cep.class))))
                .thenReturn(cepTable);

        cepRepository = new CepRepository(dynamoDbEnhancedClient);
    }

    @AfterEach
    void fecharMocks() throws Exception {
        closeable.close();
    }


    @Test
    void deveRetornarListaDeCepsQuandoChamarFindAll() {
        // Preparação
        List<Cep> cepsEsperados = Arrays.asList(criarCep(SE), criarCep(FARIA_LIMA));
        configurarMockParaScan(cepsEsperados);

        // Execução
        List<Cep> resultado = cepRepository.findAll();

        // Verificação
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verificarResultado(resultado.get(0), cepsEsperados.get(0));
        verificarResultado(resultado.get(1), cepsEsperados.get(1));
        verify(cepTable).scan();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistemCeps() {
        // Preparação
        configurarMockParaScan(Collections.emptyList());

        // Execução
        List<Cep> resultado = cepRepository.findAll();

        // Verificação
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(cepTable).scan();
    }

    @Test
    void deveLancarExcecaoQuandoOcorrerErroNaConsulta() {
        // Preparação
        when(cepTable.scan()).thenThrow(new RuntimeException("Erro ao consultar banco"));

        // Verificação
        assertThrows(RuntimeException.class, () -> cepRepository.findAll());
        verify(cepTable).scan();
    }

    @Test
    void deveAdicionarCepComSucesso() {
        // Preparação
        Cep cepParaAdicionar = criarCep(SE);
        doNothing().when(cepTable).putItem(any(Cep.class));

        // Execução
        StepVerifier.create(cepRepository.add(cepParaAdicionar))
                // Verificação
                .expectNext(cepParaAdicionar)
                .verifyComplete();

        verify(cepTable).putItem(cepParaAdicionar);
    }

    @Test
    void deveRetornarCepQuandoEncontrado() {
        // Preparação
        Cep cepEsperado = criarCep(SE);
        when(cepTable.getItem(any(GetItemEnhancedRequest.class))).thenReturn(cepEsperado);

        // Execução
        Cep resultado = cepRepository.get(CEP_SE);

        // Verificação
        verificarResultado(resultado, cepEsperado);
        verificarChamadaGetItem();
    }

    private Cep criarCep(CepVo data) {
        return Cep.builder()
                .cep(data.cep())
                .logradouro(data.logradouro())
                .bairro(data.bairro())
                .localidade(data.localidade())
                .estado(data.estado())
                .build();
    }

    private void configurarMockParaScan(List<Cep> ceps) {
        PageIterable<Cep> pageIterable = (PageIterable<Cep>) mock(PageIterable.class);
        SdkIterable<Cep> sdkIterable = (SdkIterable<Cep>) mock(SdkIterable.class);
        when(cepTable.scan()).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(ceps.stream());
    }

    private void verificarResultado(Cep resultado, Cep esperado) {
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(esperado.getCep(), resultado.getCep()),
                () -> assertEquals(esperado.getLogradouro(), resultado.getLogradouro()),
                () -> assertEquals(esperado.getBairro(), resultado.getBairro()),
                () -> assertEquals(esperado.getLocalidade(), resultado.getLocalidade()),
                () -> assertEquals(esperado.getEstado(), resultado.getEstado())
        );
    }

    private void verificarChamadaGetItem() {
        ArgumentCaptor<GetItemEnhancedRequest> requestCaptor = ArgumentCaptor.forClass(GetItemEnhancedRequest.class);
        verify(cepTable).getItem(requestCaptor.capture());
        GetItemEnhancedRequest capturedRequest = requestCaptor.getValue();
        assertTrue(capturedRequest.consistentRead());
    }

}
