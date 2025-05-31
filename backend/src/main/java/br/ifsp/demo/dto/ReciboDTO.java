package br.ifsp.demo.dto;

import java.time.LocalDateTime;

public record ReciboDTO(
        String placa,
        LocalDateTime entrada,
        LocalDateTime saida,
        double valorTotal
) {}
