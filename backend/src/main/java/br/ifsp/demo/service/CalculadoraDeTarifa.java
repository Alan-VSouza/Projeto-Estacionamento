package br.ifsp.demo.service;

import java.time.LocalDateTime;

public interface CalculadoraDeTarifa {
    double calcularValor (LocalDateTime entrada, LocalDateTime saida);
}
