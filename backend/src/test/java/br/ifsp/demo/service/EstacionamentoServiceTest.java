package br.ifsp.demo.service;

import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EstacionamentoServiceTest {

    @Mock
    private RegistroEntradaRepository registroEntradaRepository;

    private Veiculo veiculo;

    @BeforeEach
    void setup() {
        veiculo = new Veiculo();
        veiculo.setPlaca("ABC1234");
    }

    @Test
    public void registrarEntrada_comSucesso() {
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        RegistroEntrada resultado = estacionamentoService.registrarEntrada(veiculo);

        assertNotNull(resultado);
        assertEquals(veiculo, resultado.getVeiculo());
    }

}
