package wepayu.exceptions;

public class MembroNaoExisteException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public MembroNaoExisteException() {
        super("Membro nao existe.");
    }

    public MembroNaoExisteException(String detail) {
        super(detail);
    }
}