package br.ifsp.demo.service;

import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.RegistroEntradaRepository;
import br.ifsp.demo.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistroEntradaService {

    private final VeiculoRepository veiculoRepository;
    private final RegistroEntradaRepository registroEntradaRepository;

    @Autowired
    public RegistroEntradaService(VeiculoRepository veiculoRepository, RegistroEntradaRepository registroEntradaRepository) {
        this.veiculoRepository = veiculoRepository;
        this.registroEntradaRepository = registroEntradaRepository;
    }

    public RegistroEntrada registrarEntrada(Veiculo veiculo) {

        registroEntradaRepository.findByVeiculo(veiculo)
                .ifPresent(registro -> {
                    throw new IllegalArgumentException("Veículo já registrado no estacionamento");
                });

        RegistroEntrada registroEntrada = new RegistroEntrada(veiculo);
        return registroEntradaRepository.save(registroEntrada);
    }
}
