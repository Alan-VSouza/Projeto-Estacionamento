package br.ifsp.demo.components;

import br.ifsp.demo.service.CalculadoraDeTarifa;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CalculadoraTempoPermanencia  implements CalculadoraDeTarifa {

    private final ValorPermanencia valorPermanencia;

    public CalculadoraTempoPermanencia(ValorPermanencia valorPermanencia) {
        this.valorPermanencia = valorPermanencia;
    }

    @Override
    public double calcularValor(LocalDateTime entrada, LocalDateTime saida) {

        if(entrada == null || saida == null)
            throw new IllegalArgumentException("horas não podem ser nulas");

        if(saida.isBefore(entrada))
            throw new IllegalArgumentException("Horário de saída não pode ser antes do horário de entrada");


        int horasPermanencia = (int) Math.ceil(java.time.Duration.between(entrada, saida).toMinutes() / 60.0);

        return calcularValorDaPermanencia(horasPermanencia);
    }

    private double calcularValorDaPermanencia(int horas) {
        if (horas <= 0) {
            horas = 1;
        }
        if (horas == 1) {
            return valorPermanencia.getValorUmaHora();
        }
        if (horas <= 6) {
            return calcularCustoComLimite(valorPermanencia.getValorUmaHora(), valorPermanencia.getValorSeisHoras(), horas, 1);
        }
        if (horas <= 12) {
            return calcularCustoComLimite(valorPermanencia.getValorSeisHoras(), valorPermanencia.getValorDozeHoras(), horas, 6);
        }

        if (horas <= 24) {
            return calcularCustoComLimite(valorPermanencia.getValorDozeHoras(), valorPermanencia.getValorVinteEQuatroHoras(), horas, 12);
        }

        return calcularCustoComAdicional(valorPermanencia.getValorVinteEQuatroHoras(), horas, 24);
    }

    private double calcularCustoComAdicional(double valorBase, int horas, int horasLimite) {
        return valorBase + (valorPermanencia.getHoraAdicional() * (horas - horasLimite));
    }

    private double calcularCustoComLimite(double valorBase, double valorLimite, int horas, int horasLimite) {
        double custo = calcularCustoComAdicional(valorBase, horas, horasLimite);
        return Math.min(custo, valorLimite);
    }


}
