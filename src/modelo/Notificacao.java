package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Notificacao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final long id;
    private final Paciente paciente;
    private final String mensagem;
    private final LocalDateTime dataHora = LocalDateTime.now();
    private boolean lida;

    public Notificacao(long id, Paciente paciente, String mensagem) {
        this.id = id;
        this.paciente = Objects.requireNonNull(paciente);
        this.mensagem = Objects.requireNonNull(mensagem);
    }

    public void marcarComoLida() {
        lida = true;
    }

    public long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public String getMensagem() {
        return mensagem;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public boolean isLida() {
        return lida;
    }

    @Override
    public String toString() {
        return (lida ? "" : "[NOVA] ") + dataHora.format(FORMATO) + " - " + mensagem;
    }
}
