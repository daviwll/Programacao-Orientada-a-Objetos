package wepayu.exceptions;

public class NaoPodeComandosAposEncerrarSistemaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public NaoPodeComandosAposEncerrarSistemaException() {
        super("Nao pode dar comandos depois de encerrarSistema.");
    }

    public NaoPodeComandosAposEncerrarSistemaException(String detail) {
        super(detail);
    }
}