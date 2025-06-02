package br.ifsp.demo.model;

import br.ifsp.demo.service.CalculadoraDeTarifa;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Pagamento {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @Column(nullable = false)
    private String placa;

    @Column(nullable = false)
    private LocalDateTime horaEntrada;

    @Column(nullable = false)
    private LocalDateTime horaSaida;

    @Column(nullable = false)
    private double valor;


    public Pagamento(RegistroEntrada registroEntrada, LocalDateTime horaSaida, CalculadoraDeTarifa tarifa) {
        if(registroEntrada == null)
            throw new IllegalArgumentException("Registro de entrada não pode ser nulo");
        if(horaSaida == null)
            throw new IllegalArgumentException("Hora de saída não pode ser nula");
        if(tarifa == null)
            throw new IllegalArgumentException("Tarifa não pode ser nula");

        if(horaSaida.isBefore(registroEntrada.getHoraEntrada()))
            throw new IllegalArgumentException("Hora de saída não pode ser antes da hora de entrada");

        this.placa = registroEntrada.getVeiculo().getPlaca();
        this.horaEntrada = registroEntrada.getHoraEntrada();
        this.horaSaida = horaSaida;
        this.valor = tarifa.calcularValor(this.horaEntrada, this.horaSaida);

        if(this.valor < 0)
            throw new IllegalArgumentException("Valor da tarifa não pode ser negativo");
    }

    public Pagamento(RegistroEntrada registroEntrada, LocalDateTime horaEntrada, LocalDateTime horaSaida, CalculadoraDeTarifa tarifa) {
        if(registroEntrada == null)
            throw new IllegalArgumentException("Registro de entrada não pode ser nulo");
        if(horaEntrada == null)
            throw new IllegalArgumentException("Hora de entrada não pode ser nula");
        if(horaSaida == null)
            throw new IllegalArgumentException("Hora de saída não pode ser nula");
        if(tarifa == null)
            throw new IllegalArgumentException("Tarifa não pode ser nula");

        if(horaSaida.isBefore(horaEntrada))
            throw new IllegalArgumentException("Hora de saída não pode ser antes da hora de entrada");

        this.placa = registroEntrada.getVeiculo().getPlaca();
        this.horaEntrada = horaEntrada;
        this.horaSaida = horaSaida;
        this.valor = tarifa.calcularValor(this.horaEntrada, this.horaSaida);

        if(this.valor < 0)
            throw new IllegalArgumentException("Valor da tarifa não pode ser negativo");
    }

    public UUID getUuid() {
        return uuid;
    }
    public String getPlaca() {
        return placa;
    }
    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }
    public LocalDateTime getHoraSaida() {
        return horaSaida;
    }
    public double getValor() {
        return valor;
    }

}