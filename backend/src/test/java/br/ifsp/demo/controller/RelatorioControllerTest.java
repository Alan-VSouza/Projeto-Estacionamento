package br.ifsp.demo.controller;

import br.ifsp.demo.dto.RelatorioDTO;
import br.ifsp.demo.service.RelatorioService;
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
        RelatorioDTO relatorioDTO = new RelatorioDTO(100, 2.5, 3500.00, 75.0);
        when(relatorioService.gerarRelatorioDesempenho()).thenReturn(relatorioDTO);

        mockMvc.perform(get("/api/relatorios/desempenho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeVeiculos").value(100))
                .andExpect(jsonPath("$.tempoMedioEstadia").value(2.5))
                .andExpect(jsonPath("$.receitaTotal").value(3500.00))
                .andExpect(jsonPath("$.ocupacaoMedia").value(75.0));

        verify(relatorioService, times(1)).gerarRelatorioDesempenho();
    }
}