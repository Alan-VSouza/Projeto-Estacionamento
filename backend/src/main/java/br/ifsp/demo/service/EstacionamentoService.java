package br.ifsp.demo.service;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstacionamentoService {

    private final EstacionamentoRepository estacionamentoRepository;
    private final RegistroEntradaRepository registroEntradaRepository;

    @Autowired
    public EstacionamentoService(EstacionamentoRepository estacionamentoRepository,
                                 RegistroEntradaRepository registroEntradaRepository) {
        this.estacionamentoRepository = estacionamentoRepository;
        this.registroEntradaRepository = registroEntradaRepository;
    }

    public RegistroEntrada registrarEntrada(Veiculo veiculo) {
        Estacionamento estacionamento = estacionamentoRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Estacionamento não encontrado"));

        RegistroEntrada registroEntrada = new RegistroEntrada(veiculo);
        return registroEntradaRepository.save(registroEntrada);
    }
}
