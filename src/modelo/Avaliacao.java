package modelo;

import excecao.DadosInvalidosException;
import excecao.OperacaoNaoPermitidaException;

import java.util.Objects;

public class Avaliacao {

    public static final int NOTA_MINIMA = 1;
    public static final int NOTA_MAXIMA = 5;

    private final Consulta consulta;
    private final int nota;
    private final String texto;

    public Avaliacao(Consulta consulta, int nota, String texto)
            throws DadosInvalidosException, OperacaoNaoPermitidaException {
        this.consulta = Objects.requireNonNull(consulta);
        if (consulta.getEstado() != EstadoConsulta.REALIZADA) {
            throw new OperacaoNaoPermitidaException("Somente consultas REALIZADAS podem ser avaliadas.");
        }
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new DadosInvalidosException("A nota deve ser de " + NOTA_MINIMA + " a " + NOTA_MAXIMA + " estrelas.");
        }
        this.nota = nota;
        this.texto = texto == null ? "" : texto.trim();
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public int getNota() {
        return nota;
    }

    public String getTexto() {
        return texto;
    }

    public String getEstrelas() {
        return "★".repeat(nota) + "☆".repeat(NOTA_MAXIMA - nota);
    }

    @Override
    public String toString() {
        return getEstrelas() + (texto.isEmpty() ? "" : " \"" + texto + "\"");
    }
}
