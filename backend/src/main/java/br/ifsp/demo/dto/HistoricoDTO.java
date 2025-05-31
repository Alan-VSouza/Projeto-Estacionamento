package br.ifsp.demo.dto;

import java.time.LocalDateTime;

public record HistoricoDTO(
        String placa,
        LocalDateTime horaEntrada,
        LocalDateTime horaSaida,
        double valor
) {}
