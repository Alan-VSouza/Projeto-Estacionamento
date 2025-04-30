package br.ifsp.demo.controller;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.VeiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VeiculoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VeiculoService veiculoService;

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

        when(veiculoService.cadastrarVeiculo(anyString(), any(), anyString(), anyString(), anyString())).thenReturn(veiculo);

        mockMvc.perform(post("/api/veiculos")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(veiculo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.modelo").value("Fusca"));

        verify(veiculoService, times(1)).cadastrarVeiculo(anyString(), any(), anyString(), anyString(), anyString());
    }

}
