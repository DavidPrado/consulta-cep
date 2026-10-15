package br.com.cobertura.consulta.domain.exception;

public class CepInvalidoException extends RuntimeException  {

    public CepInvalidoException(String mensagem) {
        super(mensagem);
    }
}
