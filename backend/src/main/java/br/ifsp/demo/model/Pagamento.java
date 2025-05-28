package br.ifsp.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
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

    public Pagamento() {}

    public Pagamento(String placa, LocalDateTime horaEntrada, LocalDateTime horaSaida, double valor) {
        this.uuid = UUID.randomUUID();
        setPlaca(placa);
        setHoraEntrada(horaEntrada);
        setHoraSaida(horaSaida);
        setValor(valor);
    }

    public Pagamento(RegistroEntrada registroEntrada) {
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        if (placa == null || placa.trim().isEmpty())
            throw new IllegalArgumentException("Placa não pode ser nula ou vazia");
        this.placa = placa;
    }

    public LocalDateTime getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(LocalDateTime horaEntrada) {
        if (horaEntrada == null)
            throw new IllegalArgumentException("Hora de entrada não pode ser nula");
        if (horaEntrada.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Hora de entrada não pode ser no futuro");
        this.horaEntrada = horaEntrada;
    }

    public LocalDateTime getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(LocalDateTime horaSaida) {
        if (horaSaida == null)
            throw new IllegalArgumentException("Hora de saída não pode ser nula");
        if (horaEntrada != null && horaSaida.isBefore(horaEntrada))
            throw new IllegalArgumentException("Hora de saída não pode ser anterior à hora de entrada");
        if (horaSaida.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException("Hora de saída não pode ser no futuro");
        this.horaSaida = horaSaida;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if (valor < 0)
            throw new IllegalArgumentException("Valor não pode ser negativo");
        this.valor = valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return Objects.equals(uuid, pagamento.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}