package modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Prontuario {

    private final Paciente paciente;
    private final List<Consulta> registros = new ArrayList<>();

    Prontuario(Paciente paciente) {
        this.paciente = paciente;
    }

    void adicionar(Consulta consulta) {
        if (!registros.contains(consulta)) {
            registros.add(consulta);
        }
    }

    void remover(Consulta consulta) {
        registros.remove(consulta);
    }

    public List<Consulta> getRegistros() {
        List<Consulta> copia = new ArrayList<>(registros);
        copia.sort(Comparator.comparing(Consulta::getData).reversed());
        return copia;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public String formatar() {
        if (registros.isEmpty()) {
            return "Nenhuma consulta realizada.";
        }
        StringBuilder sb = new StringBuilder("Prontuário de ").append(paciente.getNome()).append("\n");
        for (Consulta c : getRegistros()) {
            RegistroAtendimento r = c.getRegistro();
            sb.append("\n").append("=".repeat(50)).append("\n")
              .append("Data: ").append(c.getDataFormatada()).append("\n")
              .append("Médico: ").append(c.getMedico().getNome())
              .append(" (").append(c.getMedico().getEspecialidade()).append(")\n")
              .append("Sintomas: ").append(r.getSintomas()).append("\n")
              .append("Diagnóstico: ").append(r.getDiagnostico()).append("\n")
              .append("Tratamento: ").append(r.getTratamento()).append("\n")
              .append("Medicamentos: ").append(r.getMedicamentos()).append("\n")
              .append("Exames solicitados: ").append(r.getExames()).append("\n")
              .append("Observações: ").append(r.getObservacoes()).append("\n")
              .append(String.format("Valor da consulta: R$ %.2f%n", c.getValorCobrado()));
        }
        return sb.toString();
    }
}
