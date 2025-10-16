package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando para lançar um resultado de venda para um empregado comissionado.
 * Undo/redo são tratados por SnapshotCommand via mementos do Sistema.
 */
public class LancarVendaCommand extends SnapshotCommand {
    private final String id;
    private final String data;
    private final String valor;

    public LancarVendaCommand(Sistema sistema, String id, String data, String valor) {
        super(sistema);
        this.id = id;
        this.data = data;
        this.valor = valor;
    }

    @Override
    protected void doExecute() throws Exception {
        sistema.lancaVenda(id, data, valor);
    }
}