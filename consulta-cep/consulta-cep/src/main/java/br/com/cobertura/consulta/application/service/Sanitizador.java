package br.com.cobertura.consulta.application.service;

import org.springframework.stereotype.Component;

@Component
public class Sanitizador {

    public String sanitizarCep(String cep) {
        if (cep == null) {
            return null;
        }
        return cep.replaceAll("\\D", "").trim();
    }

    public String sanitizarTexto(String texto, int tamanhoMaximo) {
        if (texto == null) {
            return null;
        }
        String limpo = texto
                .replaceAll("[\\p{Cntrl}]", "")   // remove caracteres de controle
                .trim();
        return limpo.length() > tamanhoMaximo ? limpo.substring(0, tamanhoMaximo) : limpo;
    }

    public String sanitizarUf(String uf) {
        if (uf == null) {
            return null;
        }
        return uf.trim().toUpperCase();
    }
}
