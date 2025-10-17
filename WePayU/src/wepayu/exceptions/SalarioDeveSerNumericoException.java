package wepayu.exceptions;

public class SalarioDeveSerNumericoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public SalarioDeveSerNumericoException() {
        super("Salario deve ser numerico.");
    }

    public SalarioDeveSerNumericoException(String detail) {
        super(detail);
    }
}