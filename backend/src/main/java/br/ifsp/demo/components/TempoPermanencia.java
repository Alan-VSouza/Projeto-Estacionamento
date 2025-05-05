package br.ifsp.demo.components;

import org.springframework.stereotype.Component;

@Component
public class TempoPermanencia {

    private final ValorPermanencia valorPermanencia;

    public TempoPermanencia(ValorPermanencia valorPermanencia) {
        this.valorPermanencia = valorPermanencia;
    }

    private double calcularCustoComAdicional(double valorBase, int horas, int horasLimite) {
        return valorBase + (valorPermanencia.getHoraAdicional() * (horas - horasLimite));
    }

    private double calcularCustoComLimite(double valorBase, double valorLimite, int horas, int horasLimite) {
        double custo = calcularCustoComAdicional(valorBase, horas, horasLimite);
        return Math.min(custo, valorLimite);
    }

    public double calcularValorDaPermanencia(int horas) {
        if (horas <= 0) {
            throw new IllegalArgumentException("Horas deve ser maior que zero");
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
}
