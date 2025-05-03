package br.ifsp.demo.controller;


import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.PagamentoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class PagamentoControllerTest {

    @Mock
    private PagamentoService pagamentoService;

    @InjectMocks
    private PagamentoController pagamentoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Pagamento pagamento;
    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(pagamentoController)
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                .build();

        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("QBC-2994");
        veiculo.setTipoVeiculo("carro");
        veiculo.setModelo("Escort Hobby");
        veiculo.setCor("prata");
        veiculo.setHoraEntrada(LocalDateTime.now().minusHours(6));

        pagamento = new Pagamento(veiculo);
        pagamento.setUuid(UUID.randomUUID());
        pagamento.setValor(35.0);
    }

    @Nested
    @DisplayName("TDD testes")
    class TDDTestes {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve criar o pagamento com sucesso")
        void deveCriarPagamentoComSucesso() throws Exception {

            doNothing().when(pagamentoService).salvarPagamento(any(Pagamento.class));

            mockMvc.perform(post("/api/pagamento")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pagamento)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.valor").value(35.0));
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve deletar o pagamento com sucesso")
        void deveDeletarPagamentoComSucesso() throws Exception {

            when(pagamentoService.buscarPorId(pagamento.getUuid())).thenReturn(pagamento);
            doNothing().when(pagamentoService).deletarPagamento(pagamento);

            mockMvc.perform(delete("/api/pagamento/{id}", pagamento.getUuid()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve atualizar o pagamento com sucesso")
        void deveAtualizarPagamentoComSucesso() throws Exception {

            when(pagamentoService.atualizarPagamento(
                    any(UUID.class),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class),
                    any(Veiculo.class),
                    any(Double.class)
                    )).thenReturn(pagamento);

            mockMvc.perform(put("/api/pagamento/{id}", pagamento.getUuid())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(pagamento)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valor").value(35.0));
        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve retornar um pagamento ao pesquisar por id")
        void deveRetornarUmPagamentoAoPesquisarPorId() throws Exception {

            when(pagamentoService.buscarPorId(pagamento.getUuid())).thenReturn(pagamento);

            mockMvc.perform(get("/api/pagamento/{id}", pagamento.getUuid()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valor").value(35.0));

        }

    }

}