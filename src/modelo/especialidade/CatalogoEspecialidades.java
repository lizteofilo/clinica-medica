package modelo.especialidade;

import java.util.List;

public final class CatalogoEspecialidades {

    private static final List<Especialidade> TODAS = List.of(
            new ClinicaGeral(), new Pediatria(), new Cardiologia(), new Dermatologia());

    private CatalogoEspecialidades() {
    }

    public static List<Especialidade> todas() {
        return TODAS;
    }

    public static Especialidade porNome(String nome) {
        return TODAS.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nome.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Especialidade desconhecida: " + nome));
    }
}
