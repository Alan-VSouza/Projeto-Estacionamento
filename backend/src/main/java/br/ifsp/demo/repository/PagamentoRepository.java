package br.ifsp.demo.repository;
import br.ifsp.demo.model.Pagamento;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {
    List<Pagamento> findByHoraSaidaBetween(LocalDateTime inicioDoDia, LocalDateTime fimDoDia);
}