package modelo;

import excecao.AcessoNegadoException;

public abstract class Usuario {

    private final long id;
    private String nome;
    private final String login;
    private String senha;

    protected Usuario(long id, String nome, String login, String senha) {
        this.id = id;
        this.nome = exigirTexto(nome, "nome");
        this.login = exigirTexto(login, "login");
        this.senha = exigirTexto(senha, "senha");
    }

    public boolean autenticar(String senhaInformada) {
        return senha.equals(senhaInformada);
    }


    protected void verificarProprioUsuario(Usuario solicitante) throws AcessoNegadoException {
        if (solicitante == null || !solicitante.equals(this)) {
            throw new AcessoNegadoException("Apenas o próprio usuário pode alterar seus dados pessoais.");
        }
    }

    public void alterarNome(Usuario solicitante, String novoNome) throws AcessoNegadoException {
        verificarProprioUsuario(solicitante);
        this.nome = exigirTexto(novoNome, "nome");
    }

    public void alterarSenha(Usuario solicitante, String novaSenha) throws AcessoNegadoException {
        verificarProprioUsuario(solicitante);
        this.senha = exigirTexto(novaSenha, "senha");
    }

    public abstract String getTipo();

    protected static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O campo " + campo + " é obrigatório.");
        }
        return valor.trim();
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Usuario u && u.id == id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return nome;
    }
}
