package wepayu.exceptions;

public class TipoNaoAplicavelException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public TipoNaoAplicavelException() {
        super("Tipo nao aplicavel.");
    }

    public TipoNaoAplicavelException(String detail) {
        super(detail);
    }
}