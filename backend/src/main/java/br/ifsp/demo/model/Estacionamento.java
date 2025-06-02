package br.ifsp.demo.model;

import br.ifsp.demo.exception.EstacionamentoLotadoException;
import br.ifsp.demo.service.CalculadoraDeTarifa;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Estacionamento {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private UUID id;
    private String nome;
    private String endereco;
    private int capacidade;


    public Estacionamento(String nome, String endereco, int capacidade) {

        if(nome == null || nome.trim().isEmpty())
            throw new IllegalArgumentException("Nome do estacionamento não pode ser nulo ou vazio");
        if(endereco == null || endereco.trim().isEmpty())
            throw new IllegalArgumentException("Endereço do estacionamento não pode ser nulo ou vazio");
        if(capacidade <= 0)
            throw new IllegalArgumentException("Capacidade do estacionamento precisa ser maior que zero");

        setNome(nome);
        setEndereco(endereco);
        setCapacidade(capacidade);

    }

    public RegistroEntrada registrarEntrada(Veiculo veiculo, int ocupacao) {

        if(ocupacao >= this.capacidade)
            throw new EstacionamentoLotadoException("O estacionamento está lotado");

        return new RegistroEntrada(veiculo);

    }

    public Pagamento registroSaida(RegistroEntrada registroEntrada, LocalDateTime horaSaida, CalculadoraDeTarifa tarifa) {

        if(registroEntrada == null)
            throw new IllegalArgumentException("Registro de entrada não pode ser nulo");

        return registroEntrada.finalizarEstadia(horaSaida, tarifa);

    }

    public UUID getId() {
        return id;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException("Endereço não pode ser nulo ou vazio");
        }
        this.endereco = endereco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        this.nome = nome;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero.");
        }
        this.capacidade = capacidade;
    }


}