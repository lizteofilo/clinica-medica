package modelo.especialidade;

import java.util.Objects;

public abstract class Especialidade {

    public static final int LIMITE_DIARIO_PADRAO = 3;

    private final String nome;

    protected Especialidade(String nome) {
        this.nome = Objects.requireNonNull(nome);
    }

    public String getNome() {
        return nome;
    }


    public int getLimiteDiario() {
        return LIMITE_DIARIO_PADRAO;
    }

  
    public double calcularValorConsulta(double valorBase) {
        return valorBase;
    }

 
    public boolean aceitaPlano(String plano) {
        return true;
    }

    
    public String descreverRegras() {
        return "Limite diário: " + getLimiteDiario() + " consultas";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Especialidade e && e.nome.equalsIgnoreCase(nome);
    }

    @Override
    public int hashCode() {
        return nome.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return nome;
    }
}
