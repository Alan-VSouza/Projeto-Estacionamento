package br.ifsp.demo.exception;

public class VeiculoNaoEncontradoException extends RuntimeException {

    public VeiculoNaoEncontradoException(String message) {
        super(message);
    }
}
