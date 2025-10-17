package wepayu.exceptions;

public class NaoHaComandoDesfazer extends BusinessException {
    private static final long serialVersionUID = 1L;

    public NaoHaComandoDesfazer() {
        super("Nao ha comando a desfazer.");
    }

    public NaoHaComandoDesfazer(String detail) {
        super(detail);
    }
}