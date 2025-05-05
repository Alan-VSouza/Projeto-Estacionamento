package br.ifsp.demo.controller;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.service.EstacionamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EstacionamentoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EstacionamentoService estacionamentoService;

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

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
    @Tag("UnitTest")
    @DisplayName("POST /estacionamento/registar-entrada -> 200 e retorna registro de entrada")
    void whenPostEntrada_thenReturnsRegistro() throws Exception {
        UUID estacionamentoId = UUID.randomUUID();

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA);

        Estacionamento estacionamento = new Estacionamento("Central", "Rua X");
        estacionamento.setId(estacionamentoId);

        RegistroEntrada registro = new RegistroEntrada(veiculo);
        registro.setHoraEntrada(LocalDateTime.of(2025, 5, 4, 10, 0));

        when(estacionamentoService.buscarEstacionamentoAtual()).thenReturn(estacionamento);

        when(estacionamentoService.registrarEntrada(any(Veiculo.class), eq(estacionamentoId)))
                .thenReturn(registro);

        mockMvc.perform(post(BASE + "/registar-entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculo))
                        .param("idEstacionamento", estacionamentoId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.veiculo.placa").value(PLACA))
                .andExpect(jsonPath("$.horaEntrada").value("2025-05-04T10:00:00"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("DELETE /estacionamento/cancelar-entrada -> 200 OK quando sucesso")
    void whenDeleteEntrada_thenReturns200() throws Exception {
        String placa = PLACA;

        when(estacionamentoService.cancelarEntrada(placa))
                .thenReturn(true);

        mockMvc.perform(delete(BASE + "/cancelar-entrada")
                        .param("placa", placa)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    @Tag("UnitTest")
    @DisplayName("DELETE /estacionamento/cancelar-entrada -> 404 Not Found quando falha")
    void whenDeleteEntradaFails_thenReturns404() throws Exception {
        String placa = PLACA;

        when(estacionamentoService.cancelarEntrada(placa))
                .thenReturn(false);

        mockMvc.perform(delete(BASE + "/cancelar-entrada")
                        .param("placa", placa)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("POST /estacionamento/saida -> 200 OK quando sucesso")
    void whenPostSaida_thenReturns200() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA);

        when(estacionamentoService.registrarSaida(any()))
                .thenReturn(true);

        mockMvc.perform(post(BASE + "/registrar-saida")
                        .param("placa", PLACA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Tag("UnitTest")
    @DisplayName("POST /estacionamento/saida -> 404 Not Found quando falha")
    void whenPostSaidaFails_thenReturns404() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA);

        when(estacionamentoService.registrarSaida(any()))
                .thenReturn(false);

        mockMvc.perform(post(BASE + "/registrar-saida")
                        .param("placa", PLACA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculo)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("GET /buscar-entrada?placa= -> 200 OK e retorna registro de entrada")
    void whenGetEntrada_thenReturnsRegistro() throws Exception {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA);

        RegistroEntrada registro = new RegistroEntrada(veiculo);
        registro.setHoraEntrada(LocalDateTime.of(2025, 5, 4, 10, 0));

        when(estacionamentoService.buscarEntrada(PLACA)).thenReturn(registro);

        mockMvc.perform(get(BASE + "/buscar-entrada")
                        .param("placa", PLACA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.veiculo.placa").value(PLACA))
                .andExpect(jsonPath("$.horaEntrada").value("2025-05-04T10:00:00"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("POST /estacionamento -> 201 Created e retorna estacionamento criado")
    void whenPostCriarEstacionamento_thenReturnsCreated() throws Exception {
        Estacionamento estacionamento = new Estacionamento("Central", "Rua X");
        when(estacionamentoService.criarEstacionamento(any(Estacionamento.class)))
                .thenReturn(estacionamento);

        mockMvc.perform(post(BASE + "/criar-estacionamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estacionamento)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("Central"))
                .andExpect(jsonPath("$.endereco").value("Rua X"));
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    @DisplayName("GET /estacionamento -> 200 OK e retorna estacionamento existente")
    void whenGetEstacionamento_thenReturnsEstacionamento() throws Exception {
        UUID idEstacionamento = UUID.randomUUID();

        Estacionamento est = new Estacionamento("Central", "Rua X");
        when(estacionamentoService.buscarEstacionamento(idEstacionamento)).thenReturn(est);

        mockMvc.perform(get(BASE + "/buscar-estacionamento/" + idEstacionamento)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("Central"))
                .andExpect(jsonPath("$.endereco").value("Rua X"));
    }

    @Test
    @Tag("UnitTest")
    @DisplayName("POST /estacionamento/registrar-saida -> 200 OK quando veículo não registrado e entrada é registrada com sucesso")
    void whenPostRegistrarEntrada_thenRegistersVehicleAndReturns200() throws Exception {
        UUID estacionamentoId = UUID.randomUUID();

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca("FUSCA");
        veiculo.setTipoVeiculo("Carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("Azul");

        Estacionamento estacionamento = new Estacionamento("Central", "Rua X");
        estacionamento.setId(estacionamentoId);

        when(estacionamentoService.buscarEstacionamentoAtual()).thenReturn(estacionamento);

        RegistroEntrada registro = new RegistroEntrada(veiculo);

        when(estacionamentoService.registrarEntrada(any(Veiculo.class), eq(estacionamentoId)))
                .thenReturn(registro);

        mockMvc.perform(post(BASE + "/registar-entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(veiculo))
                        .param("idEstacionamento", estacionamentoId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.veiculo.placa").value("FUSCA"))
                .andExpect(jsonPath("$.veiculo.modelo").value("Fusca"))
                .andExpect(jsonPath("$.veiculo.tipoVeiculo").value("Carro"))
                .andExpect(jsonPath("$.veiculo.cor").value("Azul"));
    }







}
