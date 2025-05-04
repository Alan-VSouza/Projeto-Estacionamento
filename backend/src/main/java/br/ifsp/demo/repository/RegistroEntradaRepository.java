package br.ifsp.demo.repository;

import br.ifsp.demo.model.RegistroEntrada;
import br.ifsp.demo.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistroEntradaRepository extends JpaRepository<RegistroEntrada, Long> {

    Optional<RegistroEntrada> findByVeiculo(Veiculo veiculo);
}
