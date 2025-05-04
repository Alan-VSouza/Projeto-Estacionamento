package br.ifsp.demo.service;

import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EstacionamentoService {

    private final EstacionamentoRepository estacionamentoRepository;
    private final RegistroEntradaRepository registroEntradaRepository;
    private final PagamentoService pagamentoService;
    private final VeiculoService veiculoService;

    @Autowired
    public EstacionamentoService(EstacionamentoRepository estacionamentoRepository,
                                 RegistroEntradaRepository registroEntradaRepository,
                                 PagamentoService pagamentoService,
                                 VeiculoService veiculoService) {
        this.estacionamentoRepository = estacionamentoRepository;
        this.registroEntradaRepository = registroEntradaRepository;
        this.pagamentoService = pagamentoService;
        this.veiculoService = veiculoService;
    }

    public RegistroEntrada registrarEntrada(Veiculo veiculo) {
        estacionamentoRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Estacionamento não encontrado"));

        RegistroEntrada registroEntrada = new RegistroEntrada(veiculo);
        return registroEntradaRepository.save(registroEntrada);
    }

    public boolean cancelarEntrada(Veiculo veiculo) {
        veiculoService.buscarPorPlaca(veiculo.getPlaca())
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        RegistroEntrada entrada = registroEntradaRepository.findByVeiculo(veiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não registrado no estacionamento"));

        registroEntradaRepository.delete(entrada);
        return true;
    }

    public boolean registrarSaida(Veiculo veiculo) {
        if (veiculoService.buscarPorPlaca(veiculo.getPlaca()).isEmpty()) {
            return false;
        }

        Optional<RegistroEntrada> optEntrada = registroEntradaRepository.findByVeiculo(veiculo);
        if (optEntrada.isEmpty()) {
            return false;
        }
        RegistroEntrada entrada = optEntrada.get();

        registroEntradaRepository.delete(entrada);

        Pagamento pagamento = new Pagamento();
        pagamento.setPlaca(veiculo.getPlaca());
        pagamento.setHoraEntrada(entrada.getHoraEntrada());
        pagamento.setHoraSaida(LocalDateTime.now());
        pagamentoService.salvarPagamento(pagamento);

        return true;
    }
}
