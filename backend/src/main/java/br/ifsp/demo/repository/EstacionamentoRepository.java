package br.ifsp.demo.repository;

import br.ifsp.demo.model.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Long> {
}