package br.ifsp.demo.repository;
import br.ifsp.demo.model.Pagamento;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, UUID> {
    @Query("SELECT SUM(p.valor) FROM Pagamento p WHERE p.horaSaida BETWEEN :inicio AND :fim")
    Double somarPagamentosPorData(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<Pagamento> findByHoraSaidaBetween(LocalDateTime inicioDoDia, LocalDateTime fimDoDia);
}