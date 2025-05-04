package br.ifsp.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Estacionamento {

    @Id
    private Long id;
    private String nome;
    private String endereco;

    public Estacionamento(Long id, String nome, String endereco) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
    }

    public Estacionamento() {
    }
}
