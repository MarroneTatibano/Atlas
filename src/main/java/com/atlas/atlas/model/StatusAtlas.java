
package com.atlas.atlas.model;

public class StatusAtlas {

    private String nome;
    private String mensagem;

    public StatusAtlas(String nome, String mensagem) {
        this.nome = nome;
        this.mensagem = mensagem;
    }

    public String getNome() {
        return nome;
    }

    public String getMensagem() {
        return mensagem;
    }
}
