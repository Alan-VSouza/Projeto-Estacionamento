package br.ifsp.demo.controller;

import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.EstacionamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EstacionamentoController.class)
class EstacionamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private EstacionamentoService estacionamentoService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE = "/estacionamento";

    private static final String PLACA = "ABC1234";

    @Test
    @Tag("TDD")
    @DisplayName("POST /estacionamento/entrada -> 200 e retorna registro de entrada")
    void whenPostEntrada_thenReturnsRegistro() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA);

        RegistroEntrada registro = new RegistroEntrada(veiculo);
        registro.setHoraEntrada(LocalDateTime.of(2025, 5, 4, 10, 0));

        when(estacionamentoService.registrarEntrada(any(Veiculo.class)))
                .thenReturn(registro);

        mockMvc.perform(post(BASE + "/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.veiculo.placa").value(PLACA))
                .andExpect(jsonPath("$.horaEntrada").value("2025-05-04T10:00:00"));
    }

}
