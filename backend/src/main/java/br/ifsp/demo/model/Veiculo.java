package br.ifsp.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private LocalDateTime horaEntrada;

    @Column(unique = true, nullable = false)
    private String placa;

    @Column(nullable = false)
    private String tipoVeiculo;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String cor;

}
