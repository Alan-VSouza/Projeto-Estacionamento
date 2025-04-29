package br.ifsp.demo.service;

import org.springframework.stereotype.Component;

@Component
public class ValorPermanencia {

    public double getValorVinteEQuatroHoras() {
        return 60.0;
    }

    public double getValorDozeHoras() {
        return 30.0;
    }

    public double getValorSeisHoras() {
        return 15.0;
    }

    public double getValorUmaHora() {
        return 5.0;
    }

    public double getHoraAdicional() {
        return 3.0;
    }
}