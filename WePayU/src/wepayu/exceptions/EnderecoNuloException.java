package wepayu.exceptions;

public class EnderecoNuloException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public EnderecoNuloException() {
        super("Endereco nao pode ser nulo.");
    }

    public EnderecoNuloException(String detail) {
        super(detail);
    }
}