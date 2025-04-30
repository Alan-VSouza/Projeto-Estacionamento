package br.ifsp.demo.model;

import br.ifsp.demo.service.ValorPermanencia;
import org.springframework.stereotype.Component;

@Component
public class TempoPermanencia {

    private final ValorPermanencia valorPermanencia;

    public TempoPermanencia(ValorPermanencia valorPermanencia) {
        this.valorPermanencia = valorPermanencia;
    }



    public double calcularValorDaPermanencia(int horas) {

        if(horas <= 0)
            throw new IllegalArgumentException("Horas deve ser maior que zero");

        if(horas == 1)
            return valorPermanencia.getValorUmaHora();

        return 0.0;
    }
}
