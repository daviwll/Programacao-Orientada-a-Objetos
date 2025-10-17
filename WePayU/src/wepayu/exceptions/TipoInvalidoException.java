package wepayu.exceptions;

public class TipoInvalidoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public TipoInvalidoException() {
        super("Tipo invalido.");
    }

    public TipoInvalidoException(String detail) {
        super(detail);
    }
}