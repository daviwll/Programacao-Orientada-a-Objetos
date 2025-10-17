package wepayu.exceptions;

public class SalarioNuloException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public SalarioNuloException() {
        super("Salario nao pode ser nulo.");
    }

    public SalarioNuloException(String detail) {
        super(detail);
    }
}