package modelo.especialidade;

public class Pediatria extends Especialidade {

    public static final int LIMITE_PEDIATRIA = 2;

    public Pediatria() {
        super("Pediatria");
    }

    @Override
    public int getLimiteDiario() {
        return LIMITE_PEDIATRIA;
    }
}
