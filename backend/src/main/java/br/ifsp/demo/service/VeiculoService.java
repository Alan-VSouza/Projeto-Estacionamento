package br.ifsp.demo.service;

import br.ifsp.demo.model.Veiculo;
import br.ifsp.demo.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VeiculoService {

    private VeiculoRepository veiculoRepository;

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public Veiculo cadastrarVeiculo(String placa, LocalDateTime horaEntrada, String tipoVeiculo, String modelo, String cor) {
        Veiculo veiculo = new Veiculo();
        veiculo.setPlaca(placa);
        veiculo.setTipoVeiculo(tipoVeiculo);
        veiculo.setModelo(modelo);
        veiculo.setCor(cor);
        veiculo.setHoraEntrada(horaEntrada);

        return veiculoRepository.save(veiculo);
    }
}
