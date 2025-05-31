package br.ifsp.demo.components;

import org.springframework.stereotype.Component;

@Component
public class LogSistema {

    public void registrarCancelamento(String placa, String motivoCancelamento) {
        System.out.println("Cancelamento do check-in do veículo " + placa + " realizado. Motivo: " + motivoCancelamento);
    }

}
