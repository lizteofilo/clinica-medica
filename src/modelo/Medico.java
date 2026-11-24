package modelo;

import excecao.AcessoNegadoException;
import excecao.OperacaoNaoPermitidaException;
import modelo.especialidade.Especialidade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Medico extends Usuario {

    private final Especialidade especialidade;       
    private final Set<String> planosAtendidos = new LinkedHashSet<>();
    private double valorBase;
    private final List<Avaliacao> avaliacoes = new ArrayList<>();

    public Medico(long id, String nome, String login, String senha,
                  Especialidade especialidade, double valorBase, Collection<String> planos) {
        super(id, nome, login, senha);
        this.especialidade = Objects.requireNonNull(especialidade, "Especialidade obrigatória");
        definirValorBase(valorBase);
        if (planos != null) {
            planos.stream().filter(p -> p != null && !p.isBlank()).map(String::trim).forEach(planosAtendidos::add);
        }
    }

    @Override
    public String getTipo() {
        return "Médico";
    }

    // ---------- regras delegadas à especialidade (polimorfismo) ----------

    public int getLimiteDiario() {
        return especialidade.getLimiteDiario();
    }

    public double getValorConsultaParticular() {
        return especialidade.calcularValorConsulta(valorBase);
    }

    public boolean atendePlano(String plano) {
        if (plano == null) {
            return true; 
        }
        boolean cadastrado = planosAtendidos.stream().anyMatch(p -> p.equalsIgnoreCase(plano.trim()));
        return cadastrado && especialidade.aceitaPlano(plano);
    }

    // ---------- avaliações recebidas ----------

    public void receberAvaliacao(Avaliacao avaliacao) throws OperacaoNaoPermitidaException {
        if (!avaliacao.getConsulta().getMedico().equals(this)) {
            throw new OperacaoNaoPermitidaException("A avaliação não se refere a uma consulta deste médico.");
        }
        if (avaliacoes.stream().anyMatch(a -> a.getConsulta().equals(avaliacao.getConsulta()))) {
            throw new OperacaoNaoPermitidaException("Esta consulta já foi avaliada.");
        }
        avaliacoes.add(avaliacao);
    }

    public double getMediaAvaliacoes() {
        return avaliacoes.stream().mapToInt(Avaliacao::getNota).average().orElse(0.0);
    }

    public List<Avaliacao> getAvaliacoesRecentes(int quantidade) {
        List<Avaliacao> copia = new ArrayList<>(avaliacoes);
        Collections.reverse(copia);
        return copia.subList(0, Math.min(quantidade, copia.size()));
    }

    public List<Avaliacao> getAvaliacoes() {
        return Collections.unmodifiableList(avaliacoes);
    }

    // ---------- alteração de dados (restrita ao próprio médico) ----------

    public void alterarValorBase(Usuario solicitante, double novoValor) throws AcessoNegadoException {
        verificarProprioUsuario(solicitante);
        definirValorBase(novoValor);
    }

    private void definirValorBase(double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("O valor da consulta não pode ser negativo.");
        }
        this.valorBase = valor;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public Set<String> getPlanosAtendidos() {
        return Collections.unmodifiableSet(planosAtendidos);
    }

    public double getValorBase() {
        return valorBase;
    }
}
