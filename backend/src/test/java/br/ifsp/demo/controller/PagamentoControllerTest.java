package br.ifsp.demo.controller;

import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.service.PagamentoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PagamentoController.class)
class PagamentoControllerTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private PagamentoService pagamentoService;
//
//    @InjectMocks
//    private PagamentoController pagamentoController;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private Veiculo veiculo;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        veiculo = new Veiculo();
//        veiculo.setId(1L);
//        veiculo.setPlaca("BQF-2994");
//        veiculo.setModelo("Escort");
//        veiculo.setHoraEntrada(LocalDateTime.now().minusHours(5));
//        veiculo.setTipoVeiculo("carro");
//        veiculo.setCor("prata");
//    }
//
//    @Nested
//    @DisplayName("TDD testes")
//    class TDDTestes {
//
//        @Test
//        @Tag("TDD")
//        @DisplayName("testando se o POST salva o pagamento com sucesso")
//        void testandoPostSalvaOPagamentoComSucesso() throws Exception {
//
//            Pagamento pagamento = new Pagamento(veiculo);
//
//            mockMvc.perform(post("/pagamentos")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(pagamento)))
//                    .andExpect(status().isOk());
//
//
//        }
//
//    }


}