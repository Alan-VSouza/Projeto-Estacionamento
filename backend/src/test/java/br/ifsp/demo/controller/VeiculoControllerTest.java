package br.ifsp.demo.controller;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.VeiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerTest {

    @Mock
    private VeiculoService veiculoService;

    @InjectMocks
    private VeiculoController veiculoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LocalDateTime dataEntrada;
    private Veiculo veiculoPadrao;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(veiculoController)
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                .build();

        dataEntrada = LocalDateTime.now();
        veiculoPadrao = new Veiculo();
        veiculoPadrao.setPlaca("ABC1234");
        veiculoPadrao.setTipoVeiculo("carro");
        veiculoPadrao.setModelo("Fusca");
        veiculoPadrao.setCor("azul");
        veiculoPadrao.setHoraEntrada(dataEntrada);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve cadastrar o veículo com sucesso")
    void deveCadastrarVeiculoComSucesso() throws Exception {
        when(veiculoService.cadastrarVeiculo(
                eq(veiculoPadrao.getPlaca()),
                eq(veiculoPadrao.getHoraEntrada()),
                eq(veiculoPadrao.getTipoVeiculo()),
                eq(veiculoPadrao.getModelo()),
                eq(veiculoPadrao.getCor())
        )).thenReturn(veiculoPadrao);

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoPadrao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.modelo").value("Fusca"));

        verify(veiculoService).cadastrarVeiculo(
                eq(veiculoPadrao.getPlaca()),
                eq(veiculoPadrao.getHoraEntrada()),
                eq(veiculoPadrao.getTipoVeiculo()),
                eq(veiculoPadrao.getModelo()),
                eq(veiculoPadrao.getCor())
        );
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando a placa for vazia")
    void deveRetornarBadRequestQuandoPlacaVazia() throws Exception {
        veiculoPadrao.setPlaca("");

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoPadrao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Placa não pode ser vazia"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando o modelo for vazio")
    void deveRetornarBadRequestQuandoModeloVazio() throws Exception {
        veiculoPadrao.setModelo("");

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoPadrao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Modelo não pode ser vazio"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando a cor for vazia")
    void deveRetornarBadRequestQuandoCorVazia() throws Exception {
        veiculoPadrao.setCor("");

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoPadrao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cor não pode ser vazia"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando a hora de entrada for nula")
    void deveRetornarBadRequestQuandoHoraEntradaNula() throws Exception {
        veiculoPadrao.setHoraEntrada(null);

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoPadrao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Hora de entrada não pode ser nula"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando o tipoVeiculo for vazio")
    void deveRetornarBadRequestQuandoTipoVeiculoVazio() throws Exception {
        veiculoPadrao.setTipoVeiculo("");

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoPadrao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tipo de veículo não pode ser vazio"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando a placa já estiver cadastrada")
    void deveRetornarBadRequestQuandoPlacaJaCadastrada() throws Exception {
        when(veiculoService.buscarPorPlaca("ABC1234"))
                .thenReturn(Optional.of(veiculoPadrao));

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculoPadrao)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Placa já cadastrada"));

        verify(veiculoService).buscarPorPlaca("ABC1234");
        verify(veiculoService, never()).cadastrarVeiculo(any(), any(), any(), any(), any());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve atualizar o veículo com sucesso")
    void deveAtualizarVeiculoComSucesso() throws Exception {
        Long idExistente = 1L;
        Veiculo atualizado = new Veiculo();
        atualizado.setPlaca("DEF5678");
        atualizado.setTipoVeiculo("carro");
        atualizado.setModelo("Civic");
        atualizado.setCor("preto");
        atualizado.setHoraEntrada(dataEntrada);

        when(veiculoService.atualizarVeiculo(eq(idExistente), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(atualizado);

        mockMvc.perform(put("/api/veiculos/{id}", idExistente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("DEF5678"))
                .andExpect(jsonPath("$.modelo").value("Civic"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve deletar o veículo com sucesso")
    void deveDeletarVeiculoComSucesso() throws Exception {
        Long idExistente = 1L;
        doNothing().when(veiculoService).deletarVeiculo(idExistente);

        mockMvc.perform(delete("/api/veiculos/{id}", idExistente))
                .andExpect(status().isNoContent());

        verify(veiculoService).deletarVeiculo(idExistente);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 404 quando tentar deletar um veículo não encontrado")
    void deveRetornarNotFoundQuandoVeiculoNaoExistirParaDeletar() throws Exception {
        Long idInexistente = 999L;
        doThrow(new IllegalArgumentException("Veículo não encontrado"))
                .when(veiculoService).deletarVeiculo(idInexistente);

        mockMvc.perform(delete("/api/veiculos/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Veículo não encontrado"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve buscar veículo por placa com sucesso")
    void deveBuscarVeiculoPorPlacaComSucesso() throws Exception {
        String placa = "ABC1234";
        Veiculo encontrado = new Veiculo();
        encontrado.setId(1L);
        encontrado.setPlaca(placa);
        encontrado.setTipoVeiculo("carro");
        encontrado.setModelo("Fusca");
        encontrado.setCor("azul");
        encontrado.setHoraEntrada(dataEntrada);

        when(veiculoService.buscarPorPlaca(eq(placa)))
                .thenReturn(Optional.of(encontrado));

        mockMvc.perform(get("/api/veiculos/search")
                        .param("placa", placa)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value(placa))
                .andExpect(jsonPath("$.modelo").value("Fusca"));

        verify(veiculoService).buscarPorPlaca(placa);
    }
}
