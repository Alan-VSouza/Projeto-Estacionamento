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

        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setPlaca(placa);
        novoVeiculo.setTipoVeiculo(tipoVeiculo);
        novoVeiculo.setModelo(modelo);
        novoVeiculo.setCor(cor);
        novoVeiculo.setHoraEntrada(horaEntrada);

        return veiculoRepository.save(novoVeiculo);
    }

    public Veiculo atualizarVeiculo(Long id, String placa,
                                    String tipoVeiculo, String modelo, String cor) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Placa não pode ser vazia");
        }

        Veiculo veiculoExistente = veiculoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veículo não encontrado"));

        veiculoExistente.setPlaca(placa);
        veiculoExistente.setTipoVeiculo(tipoVeiculo);
        veiculoExistente.setModelo(modelo);
        veiculoExistente.setCor(cor);

        return veiculoRepository.save(veiculoExistente);
    }

    public void deletarVeiculo(Long id) {
        Veiculo veiculoParaDeletar = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        veiculoRepository.delete(veiculoParaDeletar);
    }

    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa);
    }
}