package br.com.cobertura.consulta.shared.exception;

public class CepNaoEncontradoException extends RuntimeException{

    public CepNaoEncontradoException(String cep) {
        super("O CEP " + cep + " não foi encontrado na base externa.");
    }
}
