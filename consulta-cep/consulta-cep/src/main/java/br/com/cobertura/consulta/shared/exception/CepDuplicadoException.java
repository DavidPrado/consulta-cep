package br.com.cobertura.consulta.shared.exception;

public class CepDuplicadoException extends RuntimeException {

    public CepDuplicadoException(String cep) {
        super("O CEP " + cep + " já está cadastrado na base interna.");
    }
}
