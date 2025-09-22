package wepayu.command;

import wepayu.services.Sistema;

// antes:
// private Invoker invoker;
// private InvokerMemento invokerMemento;

public class ZerarSistemaCommand implements Command {
    private final Sistema sistema;
    private SistemaMemento sistemaMemento;

    public ZerarSistemaCommand(Sistema sistema) {
        this.sistema = sistema;
    }

    @Override
    public void execute() throws Exception {
        this.sistemaMemento = sistema.save();  // snapshot do estado anterior
        sistema.zerarDadosInternos();          // limpa APENAS os dados do sistema
        // não mexe em undo/redo do Invoker
    }

    @Override
    public void undo() throws Exception {
        sistema.restore(this.sistemaMemento);  // restaura só o sistema
        // não mexe em undo/redo do Invoker
    }
}

