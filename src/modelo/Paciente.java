package modelo;

import excecao.AcessoNegadoException;

public class Paciente extends Usuario {

    public static final String SEM_PLANO = "Não tenho";

    private int idade;
    private String plano;                 
    private final Prontuario prontuario = new Prontuario(this);

    public Paciente(long id, String nome, String login, String senha, int idade, String plano) {
        super(id, nome, login, senha);
        definirIdade(idade);
        this.plano = normalizarPlano(plano);
    }

    @Override
    public String getTipo() {
        return "Paciente";
    }

    public boolean temPlano() {
        return plano != null;
    }

    public String getPlano() {
        return plano;
    }

    public String getPlanoDescricao() {
        return temPlano() ? plano : SEM_PLANO;
    }

    public void alterarPlano(Usuario solicitante, String novoPlano) throws AcessoNegadoException {
        verificarProprioUsuario(solicitante);
        this.plano = normalizarPlano(novoPlano);
    }

    public void alterarIdade(Usuario solicitante, int novaIdade) throws AcessoNegadoException {
        verificarProprioUsuario(solicitante);
        definirIdade(novaIdade);
    }

    private void definirIdade(int idade) {
        if (idade < 0 || idade > 130) {
            throw new IllegalArgumentException("Idade inválida: " + idade);
        }
        this.idade = idade;
    }

    private static String normalizarPlano(String plano) {
        if (plano == null || plano.isBlank() || plano.trim().equalsIgnoreCase(SEM_PLANO)) {
            return null;
        }
        return plano.trim();
    }

    public int getIdade() {
        return idade;
    }

    public Prontuario getProntuario() {
        return prontuario;
    }
}
