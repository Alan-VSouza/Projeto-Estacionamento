package br.ifsp.demo.controller;


import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.PagamentoService;
import br.ifsp.demo.service.VeiculoService;
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
import java.util.Collections;
import java.util.Optional;
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
    @Mock
    private VeiculoService veiculoService;

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

        pagamento = new Pagamento();
        pagamento.setUuid(UUID.randomUUID());
        pagamento.setPlaca(veiculo.getPlaca());
        pagamento.setHoraEntrada(veiculo.getHoraEntrada());
        pagamento.setHoraSaida(LocalDateTime.now());
        pagamento.setValor(35);
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
            when(veiculoService.buscarPorPlaca(any(String.class))).thenReturn(Optional.of(new Veiculo()));

            mockMvc.perform(post("/api/pagamentos")
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

            mockMvc.perform(delete("/api/pagamentos/{id}", pagamento.getUuid()))
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
                    any(String.class),
                    any(Double.class)
                    )).thenReturn(pagamento);

            mockMvc.perform(put("/api/pagamentos/{id}", pagamento.getUuid())
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

            mockMvc.perform(get("/api/pagamentos/{id}", pagamento.getUuid()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valor").value(35.0));

        }

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve buscar pagamentos por data com sucesso")
        void deveBuscarPagamentosPorDataComSucesso() throws Exception {

            LocalDate data = LocalDate.of(2025,4,1);
            LocalDateTime hora = data.atTime(10,0);

            pagamento.setHoraSaida(hora);

            when(pagamentoService.buscarPorData(data)).thenReturn(Collections.singletonList(pagamento));

            mockMvc.perform(get("/api/pagamentos/data")
                            .param("data", "2025-04-01"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));

        }

    }

    @Nested
    @DisplayName("Testes de Sucesso Extras")
    class SucessoExtras {

        @Test
        void deveBuscarPagamentoPorIdComSucesso() throws Exception {
            UUID pagamentoId = pagamento.getUuid();
            when(pagamentoService.buscarPorId(pagamentoId)).thenReturn(pagamento);

            mockMvc.perform(get("/api/pagamentos/{id}", pagamentoId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.placa").value("QBC-2994"));
        }
    }

    @Nested
    @DisplayName("Testes de Erro")
    class Erros {

        @Test
        void deveRetornarNotFoundQuandoPagamentoNaoExiste() throws Exception {
            UUID idInexistente = UUID.randomUUID();
            when(pagamentoService.buscarPorId(idInexistente)).thenReturn(null);

            mockMvc.perform(get("/api/pagamentos/{id}", idInexistente))
                    .andExpect(status().isNotFound());
        }

    }

}
