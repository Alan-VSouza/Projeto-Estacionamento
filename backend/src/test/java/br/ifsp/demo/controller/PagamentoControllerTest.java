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

import java.time.LocalDateTime;
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

    private Veiculo veiculo;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(pagamentoController)
                .defaultRequest(get("/").accept(MediaType.APPLICATION_JSON))
                .build();

        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setPlaca("QBC-2994");
        veiculo.setTipoVeiculo("carro");
        veiculo.setModelo("Escort Hobby");
        veiculo.setCor("prata");
        veiculo.setHoraEntrada(LocalDateTime.now().minusHours(6));

    }

    @Nested
    @DisplayName("TDD testes")
    class TDDTestes {

        @Test
        @Tag("TDD")
        @Tag("UnitTest")
        @DisplayName("Deve criar o pagamento com sucesso")
        void deveCriarPagamentoComSucesso() throws Exception {

            Pagamento pagamento = new Pagamento(veiculo);
            pagamento.setValor(35.0);

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

            Pagamento pagamento = new Pagamento(veiculo);
            pagamento.setUuid(UUID.randomUUID());
            pagamento.setValor(35.0);

            when(pagamentoService.buscarPorId(pagamento.getUuid())).thenReturn(pagamento);
            doNothing().when(pagamentoService).deletarPagamento(pagamento);

            mockMvc.perform(delete("/api/pagamento/{id}", pagamento.getUuid()))
                    .andExpect(status().isNoContent());
        }

    }

}