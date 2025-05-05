package br.ifsp.demo.service;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Veiculo obterOuCadastrarVeiculo(Veiculo veiculo) {
        return veiculoService.buscarPorPlaca(veiculo.getPlaca())
                .orElseGet(() -> veiculoService.cadastrarVeiculo(
                        veiculo.getPlaca(),
                        LocalDateTime.now(),
                        veiculo.getTipoVeiculo(),
                        veiculo.getModelo(),
                        veiculo.getCor()
                ));
    }

    public RegistroEntrada registrarEntrada(Veiculo veiculo, UUID idEstacionamento) {
        Estacionamento estacionamento = estacionamentoRepository.findById(idEstacionamento)
                .orElseThrow(() -> new IllegalArgumentException("Estacionamento não encontrado"));

        Veiculo veiculoCadastrado = obterOuCadastrarVeiculo(veiculo);

        RegistroEntrada registroEntrada = new RegistroEntrada(veiculoCadastrado);
        return registroEntradaRepository.save(registroEntrada);
    }

    public boolean cancelarEntrada(String placa) {
        Veiculo veiculo = veiculoService.buscarPorPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        RegistroEntrada entrada = registroEntradaRepository.findByVeiculo(veiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não registrado no estacionamento"));

        registroEntradaRepository.delete(entrada);
        return true;
    }

    public RegistroEntrada buscarEntrada(String placa) {
        Optional<Veiculo> veiculoOpt = veiculoService.buscarPorPlaca(placa);
        return veiculoOpt.flatMap(registroEntradaRepository::findByVeiculo).orElse(null);
    }

    public boolean registrarSaida(String placa) {
        Optional<Veiculo> veiculoOpt = veiculoService.buscarPorPlaca(placa);
        if (veiculoOpt.isEmpty()) {
            return false;
        }

        Veiculo veiculo = veiculoOpt.get();
        Optional<RegistroEntrada> optEntrada = registroEntradaRepository.findByVeiculo(veiculo);
        if (optEntrada.isEmpty()) {
            return false;
        }

        RegistroEntrada entrada = optEntrada.get();

        registroEntradaRepository.delete(entrada);

        Pagamento pagamento = criarPagamento(placa, entrada);

        pagamentoService.salvarPagamento(pagamento);

        return true;
    }

    private Pagamento criarPagamento(String placa, RegistroEntrada entrada) {
        Pagamento pagamento = new Pagamento();
        pagamento.setPlaca(placa);
        pagamento.setHoraEntrada(entrada.getHoraEntrada());
        pagamento.setHoraSaida(LocalDateTime.now());
        return pagamento;
    }

    public Estacionamento criarEstacionamento(Estacionamento estacionamento) {
        return estacionamentoRepository.save(estacionamento);
    }

    public Estacionamento buscarEstacionamento(UUID id) {
        return estacionamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estacionamento não encontrado"));
    }

    public Estacionamento buscarEstacionamentoAtual() {
        return estacionamentoRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estacionamento não encontrado"));
    }

    public List<RegistroEntrada> getAllEntradas() {
        return registroEntradaRepository.findAll();
    }
}
