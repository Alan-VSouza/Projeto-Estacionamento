package br.ifsp.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Veiculo {

    @Id
    private String placa;

    private LocalDateTime entrada;

    public Veiculo(String placa) {
        this.placa = placa;
        this.entrada = LocalDateTime.now();
    }
}
