package wepayu.exceptions;

public class HorasDevemSerPositivasException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public HorasDevemSerPositivasException() {
        super("Horas devem ser positivas.");
    }

    public HorasDevemSerPositivasException(String detail) {
        super(detail);
    }
}