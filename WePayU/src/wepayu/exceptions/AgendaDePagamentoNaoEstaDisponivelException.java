package wepayu.exceptions;

public class AgendaDePagamentoNaoEstaDisponivelException extends BusinessException {
    private static final long serialVersionUID = 1L;

    public AgendaDePagamentoNaoEstaDisponivelException() {
        super("Agenda de pagamento nao esta disponivel");
    }

    public AgendaDePagamentoNaoEstaDisponivelException(String detail) {
        super(detail);
    }
}
