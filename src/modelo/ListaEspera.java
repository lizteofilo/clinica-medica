package modelo;

import excecao.OperacaoNaoPermitidaException;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ListaEspera {

    private final Medico medico;
    private final LocalDate data;
    private final Deque<Paciente> fila = new ArrayDeque<>();

    public ListaEspera(Medico medico, LocalDate data) {
        this.medico = Objects.requireNonNull(medico);
        this.data = Objects.requireNonNull(data);
    }

    public int entrar(Paciente paciente) throws OperacaoNaoPermitidaException {
        if (fila.contains(paciente)) {
            throw new OperacaoNaoPermitidaException("Você já está na lista de espera deste médico nesta data.");
        }
        fila.addLast(paciente);
        return fila.size();
    }

    public Optional<Paciente> removerPrimeiro() {
        return Optional.ofNullable(fila.pollFirst());
    }

    public void devolverAoInicio(Paciente paciente) {
        fila.addFirst(paciente);
    }

    public boolean remover(Paciente paciente) {
        return fila.remove(paciente);
    }

    public boolean contem(Paciente paciente) {
        return fila.contains(paciente);
    }

    public int posicaoDe(Paciente paciente) {
        int pos = 1;
        for (Paciente p : fila) {
            if (p.equals(paciente)) {
                return pos;
            }
            pos++;
        }
        return -1;
    }

    public boolean estaVazia() {
        return fila.isEmpty();
    }

    public List<Paciente> getPacientes() {
        return List.copyOf(fila);
    }

    public Medico getMedico() {
        return medico;
    }

    public LocalDate getData() {
        return data;
    }
}
