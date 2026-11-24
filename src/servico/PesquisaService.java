package servico;

import modelo.Medico;
import modelo.Paciente;
import repositorio.DadosClinica;

import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PesquisaService {

    private final DadosClinica dados;

    public PesquisaService(DadosClinica dados) {
        this.dados = dados;
    }


    public List<Medico> pesquisar(String termo, Paciente paciente) {
        String t = normalizar(termo == null ? "" : termo);
        return dados.getMedicos().stream()
                .filter(m -> t.isEmpty()
                        || normalizar(m.getNome()).contains(t)
                        || normalizar(m.getEspecialidade().getNome()).contains(t))
                .filter(m -> paciente == null || !paciente.temPlano() || m.atendePlano(paciente.getPlano()))
                .sorted(Comparator.comparingDouble(Medico::getMediaAvaliacoes).reversed()
                        .thenComparing(Medico::getNome))
                .toList();
    }

    static String normalizar(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
