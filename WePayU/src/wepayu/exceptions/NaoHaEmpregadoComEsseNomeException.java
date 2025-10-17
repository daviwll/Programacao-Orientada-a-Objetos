package wepayu.exceptions;

public class NaoHaEmpregadoComEsseNomeException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public NaoHaEmpregadoComEsseNomeException() {
        super("Nao ha empregado com esse nome.");
    }

    public NaoHaEmpregadoComEsseNomeException(String detail) {
        super(detail);
    }
}