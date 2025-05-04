package br.ifsp.demo.controller;

import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.EstacionamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EstacionamentoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EstacionamentoService estacionamentoService;

    @InjectMocks
    private EstacionamentoController estacionamentoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String BASE = "/estacionamento";
    private static final String PLACA = "ABC1234";

    @BeforeEach
    void setup() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders
                .standaloneSetup(estacionamentoController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();
    }

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
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.veiculo.placa").value(PLACA))
                .andExpect(jsonPath("$.horaEntrada").value("2025-05-04T10:00:00"));
    }

    @Test
    @Tag("TDD")
    @DisplayName("DELETE /estacionamento/entrada -> 200 OK quando sucesso")
    void whenDeleteEntrada_thenReturns200() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA);

        when(estacionamentoService.cancelarEntrada(any(Veiculo.class)))
                .thenReturn(true);

        mockMvc.perform(delete(BASE + "/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isOk());
    }

}
