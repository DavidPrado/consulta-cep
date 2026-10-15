package br.com.cobertura.consulta.domain.exception;

public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String mensagem) {
        super(mensagem);
    }

    public ExternalApiException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
