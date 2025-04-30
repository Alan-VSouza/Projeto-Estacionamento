package br.ifsp.demo.service;

import org.springframework.stereotype.Component;

@Component
public class ValorPermanencia {

    public double getValorVinteEQuatroHoras() {
        return 120.0;
    }

    public double getValorDozeHoras() {
        return 55.0;
    }

    public double getValorSeisHoras() {
        return 35.0;
    }

    public double getValorUmaHora() {
        return 10.0;
    }

    public double getHoraAdicional() {
        return 8.0;
    }
}