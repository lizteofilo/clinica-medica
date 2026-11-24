package modelo;

import java.util.Objects;

public class Conta {

    private final long id;
    private final Consulta consulta;
    private final double valor;
    private SituacaoPagamento situacao = SituacaoPagamento.PENDENTE;

    public Conta(long id, Consulta consulta, double valor) {
        this.id = id;
        this.consulta = Objects.requireNonNull(consulta);
        if (valor < 0) {
            throw new IllegalArgumentException("O valor da conta não pode ser negativo.");
        }
        this.valor = valor;
    }

    public long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return consulta.getPaciente();
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public double getValor() {
        return valor;
    }

    public SituacaoPagamento getSituacao() {
        return situacao;
    }
}
