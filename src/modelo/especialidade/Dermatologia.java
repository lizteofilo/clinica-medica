package modelo.especialidade;

import java.util.Set;

public class Dermatologia extends Especialidade {

    public static final Set<String> PLANOS_ACEITOS = Set.of("Unimed", "Bradesco Saúde");

    public Dermatologia() {
        super("Dermatologia");
    }

    @Override
    public boolean aceitaPlano(String plano) {
        return plano != null && PLANOS_ACEITOS.stream().anyMatch(p -> p.equalsIgnoreCase(plano.trim()));
    }

    @Override
    public String descreverRegras() {
        return super.descreverRegras() + "; aceita apenas " + String.join(", ", PLANOS_ACEITOS);
    }
}
