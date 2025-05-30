package br.ifsp.demo.repository;

import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegistroEntradaRepository extends JpaRepository<RegistroEntrada, UUID> {

    Optional<RegistroEntrada> findByVeiculo(Veiculo veiculo);
    Optional<RegistroEntrada> findByVeiculo_Placa(String placa);
}
