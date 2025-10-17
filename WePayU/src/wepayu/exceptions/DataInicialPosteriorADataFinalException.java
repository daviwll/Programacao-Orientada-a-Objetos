package wepayu.exceptions;

public class DataInicialPosteriorADataFinalException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public DataInicialPosteriorADataFinalException() {
        super("Data inicial nao pode ser posterior aa data final.");
    }

    public DataInicialPosteriorADataFinalException(String detail) {
        super(detail);
    }
}