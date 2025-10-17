package wepayu.exceptions;

public class NaoHaComandoRefazer extends BusinessException {
    private static final long serialVersionUID = 1L;

    public NaoHaComandoRefazer() {
        super("Atributo nao existe.");
    }

    public NaoHaComandoRefazer(String detail) {
        super(detail);
    }
}