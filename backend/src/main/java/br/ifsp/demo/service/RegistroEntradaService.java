package br.ifsp.demo.service;

import br.ifsp.demo.components.LogSistema;
import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistroEntradaService {

    private final VeiculoService veiculoService;
    private final RegistroEntradaRepository registroEntradaRepository;
    private final LogSistema logSistema;

    @Autowired
    public RegistroEntradaService(VeiculoService veiculoService, RegistroEntradaRepository registroEntradaRepository, LogSistema logSistema) {
        this.veiculoService = veiculoService;
        this.registroEntradaRepository = registroEntradaRepository;
        this.logSistema = logSistema;
    }

    public RegistroEntrada registrarEntrada(Veiculo veiculo) {
        verificarSeVeiculoJaRegistrado(veiculo);

        RegistroEntrada registroEntrada = new RegistroEntrada(veiculo);
        return registroEntradaRepository.save(registroEntrada);
    }

    public boolean cancelarCheckIn(Veiculo veiculo, String motivoCancelamento) {
        Veiculo veiculoExistente = veiculoService.buscarPorPlaca(veiculo.getPlaca())
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        RegistroEntrada registroEntrada = registroEntradaRepository.findByVeiculo(veiculoExistente)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não registrado no estacionamento"));

        registroEntradaRepository.delete(registroEntrada);

        logSistema.registrarCancelamento(veiculo.getPlaca(), motivoCancelamento);

        return true;
    }

    private void verificarSeVeiculoJaRegistrado(Veiculo veiculo) {
        registroEntradaRepository.findByVeiculo(veiculo)
                .ifPresent(registro -> {
                    throw new IllegalArgumentException("Veículo já registrado no estacionamento");
                });
    }
}
