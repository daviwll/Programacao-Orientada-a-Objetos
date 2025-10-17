package wepayu.exceptions;

public class ContaCorrenteNulaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public ContaCorrenteNulaException() {
        super("Conta corrente nao pode ser nulo.");
    }

    public ContaCorrenteNulaException(String detail) {
        super(detail);
    }
}