//package br.ifsp.demo.service;
//
//import br.ifsp.demo.model.RegistroEntrada;
//import br.ifsp.demo.model.Veiculo;
//import br.ifsp.demo.repository.RegistroEntradaRepository;
//import br.ifsp.demo.repository.VeiculoRepository;
//import org.junit.Test;
//import org.junit.jupiter.api.DisplayName;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//
//import java.util.Optional;
//
//import static org.codehaus.groovy.runtime.DefaultGroovyMethods.any;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.*;
//
//public class RegistroEntradaServiceTest {
//
//    @Mock
//    private VeiculoRepository veiculoRepository;
//
//    @Mock
//    private RegistroEntradaRepository registroEntradaRepository;
//
//    @InjectMocks
//    private RegistroEntradaService registroEntradaService;
//
//    private Veiculo veiculo;
//    private RegistroEntrada registroEntrada;
//
//    @Test
//    @DisplayName("Deve registrar entrada com sucesso quando veículo não estiver registrado")
//    public void registrarEntrada_comSucesso() {
//        when(veiculoRepository.findByPlaca("ABC1234")).thenReturn(Optional.of(veiculo));
//        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());
//        when(registroEntradaRepository.save(any(RegistroEntrada.class))).thenReturn(registroEntrada);
//
//        RegistroEntrada resultado = registroEntradaService.registrarEntrada("ABC1234");
//
//        assertNotNull(resultado);
//        assertEquals(veiculo, resultado.getVeiculo());
//        verify(registroEntradaRepository, times(1)).save(any(RegistroEntrada.class));
//    }
//}
