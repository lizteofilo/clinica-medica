package modelo;

import excecao.DadosInvalidosException;

public final class RegistroAtendimento {

    private final String sintomas;
    private final String diagnostico;
    private final String tratamento;
    private final String medicamentos;
    private final String exames;
    private final String observacoes;

    public RegistroAtendimento(String sintomas, String diagnostico, String tratamento,
                               String medicamentos, String exames, String observacoes)
            throws DadosInvalidosException {
        if (sintomas == null || sintomas.isBlank() || diagnostico == null || diagnostico.isBlank()) {
            throw new DadosInvalidosException("Sintomas e diagnóstico são obrigatórios para realizar a consulta.");
        }
        this.sintomas = sintomas.trim();
        this.diagnostico = diagnostico.trim();
        this.tratamento = limpar(tratamento);
        this.medicamentos = limpar(medicamentos);
        this.exames = limpar(exames);
        this.observacoes = limpar(observacoes);
    }

    private static String limpar(String s) {
        return s == null ? "" : s.trim();
    }

    public String getSintomas() {
        return sintomas;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getTratamento() {
        return tratamento;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public String getExames() {
        return exames;
    }

    public String getObservacoes() {
        return observacoes;
    }
}
