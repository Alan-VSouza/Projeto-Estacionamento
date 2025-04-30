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

        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placa);
        veiculo.setTipoVeiculo(tipoVeiculo);
        veiculo.setModelo(modelo);
        veiculo.setCor(cor);
        veiculo.setHoraEntrada(horaEntrada);

        return veiculoRepository.save(veiculo);
    }

    public Veiculo atualizarVeiculo(Long id, String placa, String tipoVeiculo, String modelo, String cor) {
        Optional<Veiculo> veiculoOptional = veiculoRepository.findById(id);

        Veiculo veiculo = veiculoOptional.orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado"));

        veiculo.setPlaca(placa);
        veiculo.setTipoVeiculo(tipoVeiculo);
        veiculo.setModelo(modelo);
        veiculo.setCor(cor);

        return veiculoRepository.save(veiculo);
    }
}
