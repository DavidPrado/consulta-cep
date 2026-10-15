package br.com.cobertura.consulta.domain.exception;

public class CepDuplicadoException extends RuntimeException {

    public CepDuplicadoException(String cep) {
        super("O CEP " + cep + " já está cadastrado na base interna.");
    }
}
