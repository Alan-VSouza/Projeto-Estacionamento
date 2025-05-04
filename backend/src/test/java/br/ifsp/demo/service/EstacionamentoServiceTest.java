package br.ifsp.demo.service;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EstacionamentoServiceTest {

    @Mock
    private EstacionamentoRepository estacionamentoRepository;

    @Mock
    private RegistroEntradaRepository registroEntradaRepository;

    @InjectMocks
    private EstacionamentoService estacionamentoService;

    private Estacionamento estacionamento;
    private Veiculo veiculo;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");

        estacionamento = new Estacionamento();
        estacionamento.setId(1L);
        estacionamento.setNome("Estacionamento Central");
        estacionamento.setEndereco("Rua X");
    }

    @Test
    @Tag("TDD")
    @Tag("UnitTest")
    public void registrarEntrada_comSucesso() {
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(registroEntradaRepository.save(any(RegistroEntrada.class))).thenReturn(new RegistroEntrada(veiculo));

        RegistroEntrada resultado = estacionamentoService.registrarEntrada(veiculo);

        assertNotNull(resultado);
        assertEquals(veiculo, resultado.getVeiculo());

        verify(registroEntradaRepository, times(1)).save(any(RegistroEntrada.class));
    }

    @Test
    public void cancelarEntrada_comSucesso() {
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(registroEntradaRepository.findByVeiculo(veiculo)).thenReturn(Optional.of(registroEntrada));

        boolean sucesso = estacionamentoService.cancelarEntrada("ABC1234");

        assertTrue(sucesso);
        verify(registroEntradaRepository, times(1)).delete(registroEntrada);
    }

}
