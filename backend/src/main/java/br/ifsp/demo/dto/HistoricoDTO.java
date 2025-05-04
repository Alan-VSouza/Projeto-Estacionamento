package br.ifsp.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoDTO {

    private String placa;
    private LocalDateTime entrada;
    private LocalDateTime saida;
    private double valorTotal;
}