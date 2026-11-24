package modelo.especialidade;

public class Cardiologia extends Especialidade {

    public static final double ACRESCIMO_ECG = 0.25;

    public Cardiologia() {
        super("Cardiologia");
    }

    @Override
    public double calcularValorConsulta(double valorBase) {
        return valorBase * (1 + ACRESCIMO_ECG);
    }

    @Override
    public String descreverRegras() {
        return super.descreverRegras() + "; particular com +25% (ECG incluso)";
    }
}
