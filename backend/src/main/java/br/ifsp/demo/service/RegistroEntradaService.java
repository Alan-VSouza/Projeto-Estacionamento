package br.ifsp.demo.service;

import br.ifsp.demo.components.LogSistema;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import br.ifsp.demo.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RegistroEntradaService {

    private final VeiculoService veiculoService;
    private final RegistroEntradaRepository registroEntradaRepository;
    private final LogSistema logSistema;

    @Autowired
    public RegistroEntradaService(VeiculoRepository veiculoRepository, VeiculoService veiculoService, RegistroEntradaRepository registroEntradaRepository, LogSistema logSistema) {
        this.veiculoService = veiculoService;
        this.registroEntradaRepository = registroEntradaRepository;
        this.logSistema = logSistema;
    }

    public RegistroEntrada registrarEntrada(Veiculo veiculo) {

        registroEntradaRepository.findByVeiculo(veiculo)
                .ifPresent(registro -> {
                    throw new IllegalArgumentException("Veículo já registrado no estacionamento");
                });

        RegistroEntrada registroEntrada = new RegistroEntrada(veiculo);
        return registroEntradaRepository.save(registroEntrada);
    }

    public boolean cancelarCheckIn(String placa, String motivoCancelamento) {

        Veiculo veiculo = veiculoService.buscarPorPlaca(placa)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        RegistroEntrada registroEntrada = registroEntradaRepository.findByVeiculo(veiculo)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não registrado no estacionamento"));

        registroEntradaRepository.delete(registroEntrada);

        logSistema.registrarCancelamento(placa, motivoCancelamento);

        return true;
    }
}
