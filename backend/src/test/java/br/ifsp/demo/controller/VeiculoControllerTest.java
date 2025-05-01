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
}
