package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public Veiculo cadastrarVeiculo(String placa, LocalDateTime horaEntrada,
                                    String tipoVeiculo, String modelo, String cor) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa não pode ser vazia");
        }
        if (horaEntrada == null || horaEntrada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Hora de entrada não pode ser nula");
        }
        if (veiculoRepository.findByPlaca(placa).isPresent()) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }
        Veiculo v = new Veiculo();
        v.setPlaca(placa);
        v.setTipoVeiculo(tipoVeiculo);
        v.setModelo(modelo);
        v.setCor(cor);
        v.setHoraEntrada(horaEntrada);
        return veiculoRepository.save(v);
    }

    public Veiculo atualizarVeiculo(Long id, String placa,
                                    String tipoVeiculo, String modelo, String cor) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Placa não pode ser vazia");
        }
        Veiculo v = veiculoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado"));
        v.setPlaca(placa);
        v.setTipoVeiculo(tipoVeiculo);
        v.setModelo(modelo);
        v.setCor(cor);
        return veiculoRepository.save(v);
    }

    public void deletarVeiculo(Long id) {
        Veiculo v = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        veiculoRepository.delete(v);
    }

    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa);
    }
}