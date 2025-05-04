package br.ifsp.demo.controller;

import br.ifsp.demo.dto.ReciboDTO;
import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.service.RelatorioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RelatorioControllerTest {

    @Mock
    private RelatorioService relatorioService;

    @InjectMocks
    private RelatorioController relatorioController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(relatorioController)
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                .build();
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve gerar o relatório de desempenho com sucesso")
    void deveGerarRelatorioDesempenhoComSucesso() throws Exception {
        LocalDate data = LocalDate.of(2025, 5, 3);
        RelatorioDTO relatorioDTO = new RelatorioDTO(100, 2.5, 3500.00, 75.0);
        when(relatorioService.gerarRelatorioDesempenho(data)).thenReturn(relatorioDTO);

        mockMvc.perform(get("/api/relatorios/desempenho")
                        .param("data", data.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeVeiculos").value(100))
                .andExpect(jsonPath("$.tempoMedioEstadia").value(2.5))
                .andExpect(jsonPath("$.receitaTotal").value(3500.00))
                .andExpect(jsonPath("$.ocupacaoMedia").value(75.0));

        verify(relatorioService, times(1)).gerarRelatorioDesempenho(data);
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar recibo com os detalhes da estadia e pagamento")
    void deveRetornarReciboComOsDetalhesDaEstadiaEPagamento() throws Exception {
        String placa = "ABC1234";
        ReciboDTO reciboDTO = new ReciboDTO(placa, "Carro", LocalDateTime.of(2025, 5, 3, 9, 0), LocalDateTime.of(2025, 5, 3, 11, 30), 30.0, "Débito");
        when(relatorioService.gerarRecibo(placa)).thenReturn(reciboDTO);

        mockMvc.perform(get("/api/relatorios/recibo")
                        .param("placa", placa)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC1234"))
                .andExpect(jsonPath("$.tipoVeiculo").value("Carro"))
                .andExpect(jsonPath("$.entrada[0]").value(2025))
                .andExpect(jsonPath("$.entrada[1]").value(5))
                .andExpect(jsonPath("$.entrada[2]").value(3))
                .andExpect(jsonPath("$.entrada[3]").value(9))
                .andExpect(jsonPath("$.entrada[4]").value(0))
                .andExpect(jsonPath("$.saida[0]").value(2025))
                .andExpect(jsonPath("$.saida[1]").value(5))
                .andExpect(jsonPath("$.saida[2]").value(3))
                .andExpect(jsonPath("$.saida[3]").value(11))
                .andExpect(jsonPath("$.saida[4]").value(30))
                .andExpect(jsonPath("$.valorTotal").value(30.0))
                .andExpect(jsonPath("$.formaPagamento").value("Débito"));

        verify(relatorioService, times(1)).gerarRecibo(placa);
    }
}