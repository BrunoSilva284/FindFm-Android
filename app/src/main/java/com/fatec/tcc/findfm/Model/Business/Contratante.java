package com.fatec.tcc.findfm.Model.Business;

import java.util.Date;

public class Contratante extends Usuario {

    private int capacidadeLocal;
    private Date inauguracao;
    private String cidade;
    private String uf;
    private String endereco;
    private int numero;

    public Contratante(){}

    public Contratante(Usuario usuario){
        super(  usuario.getNomeUsuario(),
                usuario.getNomeCompleto(),
                usuario.getSenha(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getFoto(),
                usuario.isConfirmado(),
                usuario.isPremium(),
                TiposUsuario.CONTRATANTE);
    }

    public Contratante(String nomeUsuario, String senha, String email, String telefone, String foto, boolean confirmado, boolean premium,
                 String nomeEstabelecimento, Date inauguracao, int capacidadeLocal, String cidade, String uf, String endereco, int numero) {
        super(nomeUsuario, nomeEstabelecimento, senha, email, telefone, foto, confirmado, premium, TiposUsuario.CONTRATANTE);
        this.inauguracao = inauguracao;
        this.capacidadeLocal = capacidadeLocal;
        this.cidade = cidade;
        this.uf = uf;
        this.setEndereco(endereco);
        this.setNumero(numero);
    }

    public Date getInauguracao() {
        return inauguracao;
    }

    public void setInauguracao(Date inauguracao) {
        this.inauguracao = inauguracao;
    }

    public int getCapacidadeLocal() {
        return capacidadeLocal;
    }

    public void setCapacidadeLocal(int capacidadeLocal) {
        this.capacidadeLocal = capacidadeLocal;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
