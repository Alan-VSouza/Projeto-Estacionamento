<<<<<<< HEAD
//package br.ifsp.demo.service;
//
//import br.ifsp.demo.model.RegistroEntrada;
//import br.ifsp.demo.model.Veiculo;
//import br.ifsp.demo.repository.RegistroEntradaRepository;
//import br.ifsp.demo.repository.VeiculoRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Tag;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class RegistroEntradaServiceTest {
//
//    private static final String PLACA_VEICULO = "ABC1234";
//    private static final String MENSAGEM_VEICULO_JA_REGISTRADO = "Veículo já registrado no estacionamento";
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
//    @BeforeEach
//    void setup() {
//        veiculo = criarVeiculo();
//        registroEntrada = criarRegistroEntrada(veiculo);
//    }
//
//    private Veiculo criarVeiculo() {
//        Veiculo veiculo = new Veiculo();
//        veiculo.setPlaca(PLACA_VEICULO);
//        veiculo.setTipoVeiculo("Carro");
//        veiculo.setModelo("Fusca");
//        veiculo.setCor("Azul");
//        return veiculo;
//    }
//
//    private RegistroEntrada criarRegistroEntrada(Veiculo veiculo) {
//        RegistroEntrada registroEntrada = new RegistroEntrada();
//        registroEntrada.setVeiculo(veiculo);
//        return registroEntrada;
//    }
//
//    @Test
//    @Tag("UnitTest")
//    @Tag("TDD")
//    @DisplayName("Deve registrar entrada com sucesso quando veículo não estiver registrado")
//    public void registrarEntrada_comSucesso() {
//        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());
//        when(registroEntradaRepository.save(any())).thenReturn(registroEntrada);
//
//        RegistroEntrada resultado = registroEntradaService.registrarEntrada(veiculo);
//
//        assertNotNull(resultado);
//        assertEquals(veiculo, resultado.getVeiculo());
//        verify(registroEntradaRepository, times(1)).save(any());
//    }
//
//    @Test
//    @Tag("UnitTest")
//    @Tag("TDD")
//    @DisplayName("Deve lançar IllegalArgumentException quando o veículo já estiver registrado")
//    public void registrarEntrada_veiculoJaRegistrado() {
//        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(registroEntrada));
//
//        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
//                () -> registroEntradaService.registrarEntrada(veiculo));
//
//        assertEquals(MENSAGEM_VEICULO_JA_REGISTRADO, ex.getMessage());
//        verify(registroEntradaRepository, times(0)).save(any());
//    }
//
//    @Test
//    @Tag("UnitTest")
//    @DisplayName("Deve registrar corretamente o horário de entrada do veículo")
//    public void registrarEntrada_comHorarioCorreto() {
//        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());
//
//        when(registroEntradaRepository.save(any())).thenAnswer(invocation -> {
//            RegistroEntrada entrada = invocation.getArgument(0);
//            entrada.setHoraEntrada(LocalDateTime.now());
//            return entrada;
//        });
//
//        RegistroEntrada resultado = registroEntradaService.registrarEntrada(veiculo);
//
//        assertNotNull(resultado);
//        assertEquals(veiculo, resultado.getVeiculo());
//        assertNotNull(resultado.getHoraEntrada());
//        verify(registroEntradaRepository, times(1)).save(any());
//    }
//
//    @Test
//    @Tag("UnitTest")
//    @DisplayName("Deve cancelar check-in com sucesso")
//    public void cancelarCheckIn_comSucesso() {
//        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(registroEntrada));
//
//        boolean sucesso = registroEntradaService.cancelarCheckIn(PLACA_VEICULO, MOTIVO_CANCELAMENTO);
//
//        assertTrue(sucesso);
//        verify(registroEntradaRepository, times(1)).delete(registroEntrada);
//        verify(logSistema, times(1)).registrarCancelamento(PLACA_VEICULO, MOTIVO_CANCELAMENTO);
//    }
//}
=======
package br.ifsp.demo.service;

import br.ifsp.demo.components.LogSistema;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistroEntradaServiceTest {

    private static final String PLACA_VEICULO = "ABC1234";
    private static final String MENSAGEM_VEICULO_JA_REGISTRADO = "Veículo já registrado no estacionamento";
    private static final String MOTIVO_CANCELAMENTO = "Cancelamento feito por engano";
    private static final String MENSAGEM_VEICULO_NAO_ENCONTRADO = "Veículo não encontrado";
    private static final String MENSAGEM_VEICULO_NAO_REGISTRADO = "Veículo não registrado no estacionamento";


    @Mock
    private VeiculoService veiculoService;

    @Mock
    private RegistroEntradaRepository registroEntradaRepository;

    @Mock
    private LogSistema logSistema;

    @InjectMocks
    private RegistroEntradaService registroEntradaService;

    private Veiculo veiculo;
    private RegistroEntrada registroEntrada;
    @BeforeEach
    void setup() {
        veiculo = criarVeiculo();
        registroEntrada = criarRegistroEntrada(veiculo);
    }

    private Veiculo criarVeiculo() {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(PLACA_VEICULO);
        veiculo.setTipoVeiculo("Carro");
        veiculo.setModelo("Fusca");
        veiculo.setCor("Azul");
        return veiculo;
    }

    private RegistroEntrada criarRegistroEntrada(Veiculo veiculo) {
        RegistroEntrada registroEntrada = new RegistroEntrada();
        registroEntrada.setVeiculo(veiculo);
        return registroEntrada;
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    @DisplayName("Deve registrar entrada com sucesso quando veículo não estiver registrado")
    public void registrarEntrada_comSucesso() {
        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());
        when(registroEntradaRepository.save(any())).thenReturn(registroEntrada);

        RegistroEntrada resultado = registroEntradaService.registrarEntrada(veiculo);

        assertNotNull(resultado);
        assertEquals(veiculo, resultado.getVeiculo());
        verify(registroEntradaRepository, times(1)).save(any());
    }

    @Test
    @Tag("UnitTest")
    @Tag("TDD")
    @DisplayName("Deve lançar IllegalArgumentException quando o veículo já estiver registrado")
    public void registrarEntrada_veiculoJaRegistrado() {
        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(registroEntrada));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> registroEntradaService.registrarEntrada(veiculo));

        assertEquals(MENSAGEM_VEICULO_JA_REGISTRADO, ex.getMessage());
        verify(registroEntradaRepository, times(0)).save(any());
    }

    @Test
    @Tag("UnitTest")
    @DisplayName("Deve registrar corretamente o horário de entrada do veículo")
    public void registrarEntrada_comHorarioCorreto() {
        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.empty());

        when(registroEntradaRepository.save(any())).thenAnswer(invocation -> {
            RegistroEntrada entrada = invocation.getArgument(0);
            entrada.setHoraEntrada(LocalDateTime.now());
            return entrada;
        });

        RegistroEntrada resultado = registroEntradaService.registrarEntrada(veiculo);

        assertNotNull(resultado);
        assertEquals(veiculo, resultado.getVeiculo());
        assertNotNull(resultado.getHoraEntrada());
        verify(registroEntradaRepository, times(1)).save(any());
    }

    @Test
    @Tag("UnitTest")
    @DisplayName("Deve cancelar check-in com sucesso")
    public void cancelarCheckIn_comSucesso() {
        when(veiculoService.buscarPorPlaca(PLACA_VEICULO)).thenReturn(Optional.of(veiculo));
        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(registroEntrada));

        boolean sucesso = registroEntradaService.cancelarCheckIn(veiculo, MOTIVO_CANCELAMENTO);

        assertTrue(sucesso);

        verify(registroEntradaRepository, times(1)).delete(registroEntrada);
        verify(logSistema, times(1)).registrarCancelamento(PLACA_VEICULO, MOTIVO_CANCELAMENTO);
    }
}
>>>>>>> deeb6351cf385aa8b0c3275aae070213b0912d16
