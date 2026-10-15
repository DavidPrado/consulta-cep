package br.com.cobertura.consulta.shared.exception;

public class ExternalApiException extends RuntimeException {

    public ExternalApiException(String mensagem) {
        super(mensagem);
    }

    public ExternalApiException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
