package excecao;

public class OperacaoNaoPermitidaException extends ClinicaException {
    public OperacaoNaoPermitidaException(String mensagem) {
        super(mensagem);
    }
}
