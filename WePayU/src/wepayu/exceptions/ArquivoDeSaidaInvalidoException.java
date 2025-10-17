package wepayu.exceptions;

public class ArquivoDeSaidaInvalidoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public ArquivoDeSaidaInvalidoException() {
        super("Arquivo de saida invalido.");
    }

    public ArquivoDeSaidaInvalidoException(String detail) {
        super(detail);
    }
}