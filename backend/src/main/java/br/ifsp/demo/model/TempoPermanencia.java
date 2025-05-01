package br.ifsp.demo.model;

import br.ifsp.demo.service.ValorPermanencia;
import org.springframework.stereotype.Component;

@Component
public class TempoPermanencia {

    private final ValorPermanencia valorPermanencia;

    public TempoPermanencia(ValorPermanencia valorPermanencia) {
        this.valorPermanencia = valorPermanencia;
    }

    public double calculoSeisHoras(int horas) {

        double custo = valorPermanencia.getValorUmaHora() + (valorPermanencia.getHoraAdicional() * (horas - 1));

        return Math.min(custo, valorPermanencia.getValorSeisHoras());
    }

    public double calcularValorDaPermanencia(int horas) {

        if(horas <= 0)
            throw new IllegalArgumentException("Horas deve ser maior que zero");

        else if(horas == 1)
            return valorPermanencia.getValorUmaHora();

        else if(horas <= 6)
            return calculoSeisHoras(horas);

        else if(horas <= 12)


        return 0.0;
    }
}
