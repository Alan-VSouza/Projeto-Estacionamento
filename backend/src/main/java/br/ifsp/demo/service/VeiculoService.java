package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VeiculoService {

    private VeiculoRepository veiculoRepository;

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public Veiculo cadastrarVeiculo(String placa, LocalDateTime horaEntrada, String tipoVeiculo, String modelo, String cor) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa não pode ser vazia");
        }

        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo não pode ser vazio");
        }

        if (cor == null || cor.trim().isEmpty()) {
            throw new IllegalArgumentException("Cor não pode ser vazia");
        }

        if (horaEntrada == null || horaEntrada.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Hora de entrada não pode ser nula");
        }

        if (veiculoRepository.findByPlaca(placa).isPresent()) {
            throw new IllegalArgumentException("Placa já cadastrada");
        }

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placa);
        veiculo.setTipoVeiculo(tipoVeiculo);
        veiculo.setModelo(modelo);
        veiculo.setCor(cor);
        veiculo.setHoraEntrada(horaEntrada);

        return veiculoRepository.save(veiculo);
    }

    public Veiculo atualizarVeiculo(Long id, String placa, String tipoVeiculo, String modelo, String cor) {
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("Placa não pode ser vazia");
        }

        Optional<Veiculo> veiculoOptional = veiculoRepository.findById(id);
        Veiculo veiculo = veiculoOptional.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        veiculo.setPlaca(placa);
        veiculo.setTipoVeiculo(tipoVeiculo);
        veiculo.setModelo(modelo);
        veiculo.setCor(cor);

        return veiculoRepository.save(veiculo);
    }

    public void deletarVeiculo(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));
        veiculoRepository.delete(veiculo);
    }

    public boolean verificarPlacaCadastrada(String placa) {
        return veiculoRepository.findByPlaca(placa).isPresent();
    }

}
