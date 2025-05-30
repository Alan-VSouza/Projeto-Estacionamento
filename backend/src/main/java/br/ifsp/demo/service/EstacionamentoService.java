package br.ifsp.demo.service;

import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importar para ResponseStatusException
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException; // Importar ResponseStatusException

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
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
                        LocalDateTime.now(), // A hora de entrada do veículo é definida aqui
                        veiculo.getTipoVeiculo(),
                        veiculo.getModelo(),
                        veiculo.getCor()
                ));
    }

    public RegistroEntrada registrarEntrada(Veiculo veiculo, UUID idEstacionamento) {
        Estacionamento estacionamento = estacionamentoRepository.findById(idEstacionamento)
                .orElseThrow(() -> new IllegalArgumentException("Estacionamento não encontrado com ID: " + idEstacionamento));

        long veiculosEstacionados = registroEntradaRepository.count();
        if (veiculosEstacionados >= estacionamento.getCapacidade()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estacionamento lotado. Capacidade máxima de " + estacionamento.getCapacidade() + " veículos atingida.");
        }

        Optional<Veiculo> veiculoExistenteOpt = veiculoService.buscarPorPlaca(veiculo.getPlaca());
        if (veiculoExistenteOpt.isPresent()) {
            Optional<RegistroEntrada> entradaExistenteOpt = registroEntradaRepository.findByVeiculo(veiculoExistenteOpt.get());
            if (entradaExistenteOpt.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veículo com placa " + veiculo.getPlaca() + " já possui uma entrada registrada no estacionamento.");
            }
        }


        Veiculo veiculoCadastrado = obterOuCadastrarVeiculo(veiculo);

        RegistroEntrada registroEntrada = new RegistroEntrada(veiculoCadastrado);
        return registroEntradaRepository.save(registroEntrada);
    }

    public boolean cancelarEntrada(String placa) {
        Veiculo veiculo = veiculoService.buscarPorPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado para cancelamento com placa: " + placa));

        RegistroEntrada entrada = registroEntradaRepository.findByVeiculo(veiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo com placa " + placa + " não possui entrada registrada para cancelar."));

        registroEntradaRepository.delete(entrada);

        return true;
    }

    public RegistroEntrada buscarEntrada(String placa) {
        Optional<Veiculo> veiculoOpt = veiculoService.buscarPorPlaca(placa);
        return veiculoOpt.flatMap(registroEntradaRepository::findByVeiculo).orElse(null);
    }

    public boolean registrarSaida(String placa) {
        Veiculo veiculo = veiculoService.buscarPorPlaca(placa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo com placa " + placa + " não encontrado."));

        RegistroEntrada entrada = registroEntradaRepository.findByVeiculo(veiculo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veiculo com placa " + placa + " não possui entrada registrada no estacionamento."));

        LocalDateTime horaEntradaOriginal = entrada.getHoraEntrada();

        registroEntradaRepository.delete(entrada);

        Pagamento pagamento = criarPagamento(placa, horaEntradaOriginal);
        pagamentoService.salvarPagamento(pagamento);

        return true;
    }

    private Pagamento criarPagamento(String placa, LocalDateTime horaEntradaVeiculo) {
        Pagamento pagamento = new Pagamento();
        pagamento.setPlaca(placa);
        pagamento.setHoraEntrada(horaEntradaVeiculo);
        pagamento.setHoraSaida(LocalDateTime.now());
        return pagamento;
    }

    public Estacionamento criarEstacionamento(Estacionamento estacionamento) {
        if (estacionamento.getCapacidade() <= 0) {
            throw new IllegalArgumentException("Capacidade do estacionamento deve ser maior que zero.");
        }
        return estacionamentoRepository.save(estacionamento);
    }

    public Estacionamento buscarEstacionamento(UUID id) {
        return estacionamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estacionamento não encontrado com ID: " + id));
    }

    public Estacionamento buscarEstacionamentoAtual() {
        return estacionamentoRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum estacionamento configurado no sistema. Crie um primeiro."));
    }

    public List<RegistroEntrada> getAllEntradas() {
        return registroEntradaRepository.findAll();
    }
}
