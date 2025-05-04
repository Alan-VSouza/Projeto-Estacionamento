package br.ifsp.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
public class Estacionamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID id;
    private String nome;
    private String endereco;

    public Estacionamento(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
        this.id = UUID.randomUUID();
    }

    public Estacionamento() {
    }
}
