package modelo;

import excecao.TransicaoInvalidaException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Consulta {

    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final long id;
    private final Medico medico;
    private final Paciente paciente;
    private final LocalDate data;
    private EstadoConsulta estado = EstadoConsulta.AGENDADA;   
    private RegistroAtendimento registro;                      
    private double valorCobrado;

    public Consulta(long id, Medico medico, Paciente paciente, LocalDate data) {
        this.id = id;
        this.medico = Objects.requireNonNull(medico);
        this.paciente = Objects.requireNonNull(paciente);
        this.data = Objects.requireNonNull(data);
    }

    public void cancelar() throws TransicaoInvalidaException {
        exigirAgendada("cancelar");
        estado = EstadoConsulta.CANCELADA;
    }

    public void realizar(RegistroAtendimento dados, double valor) throws TransicaoInvalidaException {
        Objects.requireNonNull(dados, "Registro do atendimento obrigatório");
        exigirAgendada("realizar");
        this.registro = dados;
        this.valorCobrado = valor;
        this.estado = EstadoConsulta.REALIZADA;
        paciente.getProntuario().adicionar(this);  
    }

    private void exigirAgendada(String acao) throws TransicaoInvalidaException {
        if (estado != EstadoConsulta.AGENDADA) {
            throw new TransicaoInvalidaException("Não é possível " + acao + " uma consulta "
                    + estado.name() + ". Apenas consultas AGENDADAS podem ser canceladas ou realizadas.");
        }
    }

    public boolean ocupaVaga() {
        return estado != EstadoConsulta.CANCELADA;
    }

    public long getId() {
        return id;
    }

    public Medico getMedico() {
        return medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public LocalDate getData() {
        return data;
    }

    public String getDataFormatada() {
        return data.format(FORMATO_DATA);
    }

    public EstadoConsulta getEstado() {
        return estado;
    }

    public RegistroAtendimento getRegistro() {
        return registro;
    }

    public double getValorCobrado() {
        return valorCobrado;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Consulta c && c.id == id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Consulta #" + id + " - " + getDataFormatada() + " - " + medico.getNome() + " - " + estado;
    }
}
