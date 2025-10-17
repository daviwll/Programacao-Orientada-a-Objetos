package wepayu.exceptions;

public class ErroAoEscreverArquivoDeSaidaException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public ErroAoEscreverArquivoDeSaidaException() {
        super("Erro ao escrever arquivo de saida.");
    }

    public ErroAoEscreverArquivoDeSaidaException(String detail) {
        super(detail);
    }

    public ErroAoEscreverArquivoDeSaidaException(Throwable cause) {
        super("Erro ao escrever arquivo de saida.", cause);
    }
}