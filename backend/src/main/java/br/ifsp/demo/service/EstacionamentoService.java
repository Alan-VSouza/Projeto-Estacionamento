package br.ifsp.demo.service;

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
    public RegistroEntrada registrar(Veiculo veiculoDados, UUID idEstacionamento) {

        Estacionamento estacionamento = estacionamentoRepository.findById(idEstacionamento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estacionamento não encontrado"));

        veiculoService.buscarPorPlaca(veiculoDados.getPlaca())
                .ifPresent(veiculo -> {
                    if (registroEntradaRepository.findByVeiculo(veiculo).isPresent()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Veículo já possui uma entrada registrada.");
                    }
                });

        Veiculo veiculoRegistrar = veiculoService.obterOuCadastrarVeiculo(veiculoDados);
        long veiculosEstacionados = registroEntradaRepository.count();

        RegistroEntrada novoRegistro = estacionamento.registrarEntrada(veiculoRegistrar, (int) veiculosEstacionados);

        return registroEntradaRepository.save(novoRegistro);
    }

    @Transactional
    public Pagamento registrarSaida(String placa) {

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

    public Estacionamento buscarEstacionamentoAtual() {
        return estacionamentoRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nenhum estacionamento encontrado"));
    }

    public boolean cancelarEntrada(String placa) {

        Veiculo veiculo = veiculoService.buscarPorPlaca(placa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veiculo não encontrado"));
        RegistroEntrada entrada = registroEntradaRepository.findByVeiculo(veiculo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não possui entrada registrada para cancelar"));

        registroEntradaRepository.delete(entrada);
        return true;
    }

}
