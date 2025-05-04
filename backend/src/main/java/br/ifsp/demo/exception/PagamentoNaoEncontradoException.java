package br.ifsp.demo.exception;

public class PagamentoNaoEncontradoException extends RuntimeException {

    public PagamentoNaoEncontradoException(String message) {
        super(message);
    }
}
