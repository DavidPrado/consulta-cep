package br.com.cobertura.consulta.shared.exception;

public class LoteNaoEncontradoException extends RuntimeException {

    public LoteNaoEncontradoException(String loteId) {
        super("Lote não encontrado: " + loteId);
    }
}