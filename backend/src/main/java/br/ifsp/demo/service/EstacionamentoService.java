package br.ifsp.demo.service;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstacionamentoService {

    private final EstacionamentoRepository estacionamentoRepository;
    private final RegistroEntradaRepository registroEntradaRepository;
    private final VeiculoService veiculoService;

    @Autowired
    public EstacionamentoService(EstacionamentoRepository estacionamentoRepository,
                                 RegistroEntradaRepository registroEntradaRepository,
                                 RegistroEntradaService registroEntradaService,
                                 VeiculoService veiculoService,
                                 Estacionamento estacionamento) {
        this.estacionamentoRepository = estacionamentoRepository;
        this.registroEntradaRepository = registroEntradaRepository;
        this.veiculoService = veiculoService;
    }

    public RegistroEntrada registrarEntrada(Veiculo veiculo) {
        Estacionamento estacionamento = estacionamentoRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Estacionamento não encontrado"));

        RegistroEntrada registroEntrada = new RegistroEntrada(veiculo);
        return registroEntradaRepository.save(registroEntrada);
    }

    public boolean cancelarEntrada(Veiculo veiculo) {
        if (veiculoService.buscarPorPlaca(veiculo.getPlaca()).isEmpty()) {
            return false;
        }

        Optional<RegistroEntrada> registroOpt = registroEntradaRepository.findByVeiculo(veiculo);
        if (registroOpt.isEmpty()) {
            return false;
        }

        registroEntradaRepository.delete(registroOpt.get());
        return true;
    }

}
