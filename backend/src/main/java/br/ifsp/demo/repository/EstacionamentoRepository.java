package br.ifsp.demo.repository;

import br.ifsp.demo.model.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstacionamentoRepository extends JpaRepository<Estacionamento, UUID> {
}