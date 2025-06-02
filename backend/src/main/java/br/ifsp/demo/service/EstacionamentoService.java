package br.ifsp.demo.service;

import br.ifsp.demo.dto.CriarEstacionamentoDTO;
import br.ifsp.demo.model.Estacionamento;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.model.Pagamento;
import br.ifsp.demo.repository.EstacionamentoRepository;
import br.ifsp.demo.repository.PagamentoRepository;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import br.ifsp.demo.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.IllegalFormatWidthException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class EstacionamentoService {

    private final EstacionamentoRepository estacionamentoRepository;
    private final RegistroEntradaRepository registroEntradaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final VeiculoService veiculoService;
    private final CalculadoraDeTarifa calculadoraDeTarifa;

    public EstacionamentoService(EstacionamentoRepository estacionamentoRepository,
                                 RegistroEntradaRepository registroEntradaRepository,
                                 PagamentoRepository pagamentoRepository,
                                 VeiculoService veiculoService,
                                 CalculadoraDeTarifa calculadoraDeTarifa) {
        this.estacionamentoRepository = estacionamentoRepository;
        this.registroEntradaRepository = registroEntradaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.veiculoService = veiculoService;
        this.calculadoraDeTarifa = calculadoraDeTarifa;
    }

    @Transactional
    public RegistroEntrada registrarEntrada(Veiculo veiculo, UUID idEstacionamento, Integer vagaId) {

        if(veiculo == null)
            throw new IllegalArgumentException("Veiculo não pode ser nulo");

        if(idEstacionamento == null)
            throw new IllegalArgumentException("ID do estacionamento não pode ser nulo");

        if(vagaId == null)
            throw new IllegalArgumentException("Número da vaga não pode ser nulo");

        if(vagaId <= 0)
            throw new IllegalArgumentException("Número da vaga deve ser maior que zero");

        Estacionamento estacionamento = estacionamentoRepository.findById(idEstacionamento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estacionamento não encontrado"));

        Optional<RegistroEntrada> vagaOcupada = registroEntradaRepository.findByVagaId(vagaId);

        if (vagaOcupada.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Vaga " + vagaId + " já está ocupada");
        }

        long veiculosEstacionados = registroEntradaRepository.count();

        if (veiculosEstacionados >= estacionamento.getCapacidade()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Estacionamento lotado. Capacidade máxima atingida.");
        }

        Optional<Veiculo> veiculoExistenteOpt = veiculoService.buscarPorPlaca(veiculo.getPlaca());
        if (veiculoExistenteOpt.isPresent()) {
            Optional<RegistroEntrada> entradaExistenteOpt = registroEntradaRepository.findByVeiculo(veiculoExistenteOpt.get());
            if (entradaExistenteOpt.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Veículo já possui uma entrada registrada na vaga " + entradaExistenteOpt.get().getVagaId());
            }
        }

        Veiculo veiculoCadastrado = veiculoService.obterOuCadastrarVeiculo(veiculo);
        RegistroEntrada registroEntrada = new RegistroEntrada(veiculoCadastrado, vagaId);
        return registroEntradaRepository.save(registroEntrada);
    }

    public Integer findNextAvailableSpot() {
        List<Integer> vagasOcupadas = registroEntradaRepository.findAllOccupiedSpotIds();

        for (int i = 1; i <= 200; i++) {
            if (!vagasOcupadas.contains(i)) {
                return i;
            }
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Todas as vagas estão ocupadas");
    }

    @Transactional
    public RegistroEntrada registrar(Veiculo veiculoDados, UUID idEstacionamento) {
        if(veiculoDados == null)
            throw new IllegalArgumentException("Veiculo para registro não pode ser nulo");

        if(idEstacionamento == null)
            throw new IllegalArgumentException("ID do estacionamento não pode ser nulo");

        Integer vagaId = findNextAvailableSpot();
        return registrarEntrada(veiculoDados, idEstacionamento, vagaId);
    }

    @Transactional
    public Pagamento registrarSaida(String placa) {

        if(placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");

        Estacionamento estacionamento = buscarEstacionamentoAtual();

        Veiculo veiculo = veiculoService.buscarPorPlaca(placa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não está registrado"));

        RegistroEntrada registroEntrada = registroEntradaRepository.findByVeiculo(veiculo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum registro de entrada ativo para esse veículo"));

        Pagamento pagamento = estacionamento.registroSaida(registroEntrada, LocalDateTime.now(), calculadoraDeTarifa);

        pagamentoRepository.save(pagamento);
        registroEntradaRepository.delete(registroEntrada);

        return pagamento;
    }

    @Transactional
    public Estacionamento criarEstacionamento(CriarEstacionamentoDTO dto) {
        if(dto == null)
            throw new IllegalArgumentException("Dados de criação do estacionamento não podem ser nulos");

        Estacionamento estacionamento = new Estacionamento(
                dto.nome(),
                dto.endereco(),
                dto.capacidade()
        );

        return estacionamentoRepository.save(estacionamento);
    }

    public Estacionamento buscarEstacionamento(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do estacionamento não pode ser nulo.");
        }
        return estacionamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estacionamento não encontrado com o ID: " + id));
    }

    public Estacionamento buscarEstacionamentoAtual() {
        return estacionamentoRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum estacionamento encontrado"));
    }

    public boolean cancelarEntrada(String placa) {

        if(placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");

        Veiculo veiculo = veiculoService.buscarPorPlaca(placa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veiculo não encontrado"));

        RegistroEntrada entrada = registroEntradaRepository.findByVeiculo(veiculo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não possui entrada registrada para cancelar"));

        registroEntradaRepository.delete(entrada);
        return true;
    }

    public RegistroEntrada buscarEntrada(String placa) {

        if(placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");

        Veiculo veiculo = veiculoService.buscarPorPlaca(placa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Esse veículo não está no estacionamento"));

        return registroEntradaRepository.findByVeiculo(veiculo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não existe nenhuma entrada registrada nesse veículo"));
    }

    public List<RegistroEntrada> getAllEntradas() {
        return registroEntradaRepository.findAll();
    }
}
