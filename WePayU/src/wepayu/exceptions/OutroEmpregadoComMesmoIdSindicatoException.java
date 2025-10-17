package wepayu.exceptions;

public class OutroEmpregadoComMesmoIdSindicatoException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public OutroEmpregadoComMesmoIdSindicatoException() {
        super("Ha outro empregado com esta identificacao de sindicato");
    }

    public OutroEmpregadoComMesmoIdSindicatoException(String detail) {
        super(detail);
    }
}