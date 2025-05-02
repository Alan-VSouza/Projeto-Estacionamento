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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

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

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(veiculoController)
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                .build();
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve cadastrar o veículo com sucesso")
    void deveCadastrarVeiculoComSucesso() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setTipoVeiculo("carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("azul");
        veiculo.setHoraEntrada(LocalDateTime.now());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(veiculoService.cadastrarVeiculo(anyString(), any(), anyString(), anyString(), anyString())).thenReturn(veiculo);

        mockMvc.perform(post("/api/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.modelo").value("Fusca"));

        verify(veiculoService, times(1)).cadastrarVeiculo(anyString(), any(), anyString(), anyString(), anyString());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando a placa for vazia")
    void deveRetornarBadRequestQuandoPlacaVazia() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("");
        veiculo.setModelo("Fusca");
        veiculo.setTipoVeiculo("carro");
        veiculo.setCor("azul");
        veiculo.setHoraEntrada(LocalDateTime.now());

        mockMvc.perform(post("/api/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Placa não pode ser vazia"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando o modelo for vazio")
    void deveRetornarBadRequestQuandoModeloVazio() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setModelo("");
        veiculo.setTipoVeiculo("carro");
        veiculo.setCor("azul");
        veiculo.setHoraEntrada(LocalDateTime.now());

        mockMvc.perform(post("/api/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Modelo não pode ser vazio"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando a cor for vazia")
    void deveRetornarBadRequestQuandoCorVazia() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setModelo("Fusca");
        veiculo.setTipoVeiculo("carro");
        veiculo.setCor("");
        veiculo.setHoraEntrada(LocalDateTime.now());

        mockMvc.perform(post("/api/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cor não pode ser vazia"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando a hora de entrada for nula")
    void deveRetornarBadRequestQuandoHoraEntradaNula() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setModelo("Fusca");
        veiculo.setTipoVeiculo("carro");
        veiculo.setCor("azul");
        veiculo.setHoraEntrada(null);

        mockMvc.perform(post("/api/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Hora de entrada não pode ser nula"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 400 quando a placa já estiver cadastrada")
    void deveRetornarBadRequestQuandoPlacaJaCadastrada() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setModelo("Fusca");
        veiculo.setTipoVeiculo("carro");
        veiculo.setCor("azul");
        veiculo.setHoraEntrada(LocalDateTime.now());

        when(veiculoService.cadastrarVeiculo(anyString(), any(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Placa já cadastrada"));

        mockMvc.perform(post("/api/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Placa já cadastrada"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve atualizar o veículo com sucesso")
    void deveAtualizarVeiculoComSucesso() throws Exception {
        Long idExistente = 1L;
        Veiculo veiculoAtualizado = new Veiculo();
        veiculoAtualizado.setPlaca("DEF5678");
        veiculoAtualizado.setModelo("Civic");
        veiculoAtualizado.setTipoVeiculo("carro");
        veiculoAtualizado.setCor("preto");
        veiculoAtualizado.setHoraEntrada(LocalDateTime.now());

        when(veiculoService.atualizarVeiculo(eq(idExistente), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(veiculoAtualizado);

        mockMvc.perform(put("/api/veiculos/{id}", idExistente)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(veiculoAtualizado)))
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

        doNothing().when(veiculoService).deletarVeiculo(eq(idExistente));

        mockMvc.perform(delete("/api/veiculos/{id}", idExistente))
                .andExpect(status().isNoContent());

        verify(veiculoService, times(1)).deletarVeiculo(eq(idExistente));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar 404 quando tentar deletar um veículo não encontrado")
    void deveRetornarNotFoundQuandoVeiculoNaoExistirParaDeletar() throws Exception {
        Long idInexistente = 999L;

        doThrow(new IllegalArgumentException("Veículo não encontrado")).when(veiculoService).deletarVeiculo(eq(idInexistente));

        mockMvc.perform(delete("/api/veiculos/{id}", idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Veículo não encontrado"));
    }

    @Test
    @Tag("TDD")
    @DisplayName("Deve retornar 400 quando o tipoVeiculo for vazio")
    void deveRetornarBadRequestQuandoTipoVeiculoVazio() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
        veiculo.setTipoVeiculo("");
        veiculo.setModelo("Fusca");
        veiculo.setCor("azul");
        veiculo.setHoraEntrada(LocalDateTime.now());

        mockMvc.perform(post("/api/veiculos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tipo de veículo não pode ser vazio"));
    }

    @Test
    @Tag("TDD")
    @DisplayName("Deve buscar veículo por placa com sucesso")
    void deveBuscarVeiculoPorPlacaComSucesso() throws Exception {
        String placa = "ABC1234";
        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca(placa);
        veiculo.setTipoVeiculo("carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("azul");
        veiculo.setHoraEntrada(LocalDateTime.now());

        when(veiculoService.buscarPorPlaca(eq(placa))).thenReturn(Optional.of(veiculo));

        mockMvc.perform(get("/api/veiculos/search")
                        .param("placa", placa)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value(placa))
                .andExpect(jsonPath("$.modelo").value("Fusca"));

        verify(veiculoService, times(1)).buscarPorPlaca(placa);
    }







}
