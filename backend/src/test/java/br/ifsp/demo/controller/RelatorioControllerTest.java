package br.ifsp.demo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("Deve retornar relatório com informações completas")
    public void deveRetornarRelatorioComInformacoesCompletas() throws Exception {
        mockMvc.perform(get("/relatorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntradas").exists())
                .andExpect(jsonPath("$.mediaOcupacao").exists())
                .andExpect(jsonPath("$.totalArrecadado").exists());
    }
}