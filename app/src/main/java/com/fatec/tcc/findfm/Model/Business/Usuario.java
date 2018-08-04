package com.fatec.tcc.findfm.Model.Business;

public class Usuario {

    private String nomeUsuario;
    private boolean confirmado;
    private boolean premium;
    private String telefone;
    private String email;
    private String senha;
    private byte[] foto;

    public Usuario(){}

    /**
     * Para logar
     * @param nomeUsuario
     * @param senha
     */
    public Usuario(String nomeUsuario, String senha){
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
    }

    /**
     * Completo
     * @param nomeUsuario
     * @param senha
     * @param email
     * @param telefone
     * @param foto
     * @param confirmado
     * @param premium
     */
    public Usuario(String nomeUsuario, String senha, String email, String telefone, byte[] foto, boolean confirmado, boolean premium){
        this.nomeUsuario = nomeUsuario;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
        this.foto = foto;
        this.confirmado = confirmado;
        this.premium = premium;

    }

    public String getUsuario() {
        return nomeUsuario;
    }

    public void setUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
