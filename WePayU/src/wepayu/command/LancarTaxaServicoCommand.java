package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando para lançar uma taxa de serviço a um membro do sindicato.
 * Undo/redo são tratados por SnapshotCommand via mementos do Sistema.
 */
public class LancarTaxaServicoCommand extends SnapshotCommand {
    private final String membroId;
    private final String data;
    private final String valor;

    public LancarTaxaServicoCommand(Sistema sistema, String membroId, String data, String valor) {
        super(sistema);
        this.membroId = membroId;
        this.data = data;
        this.valor = valor;
    }

    @Override
    protected void doExecute() throws Exception {
        sistema.lancaTaxaServicoPorMembro(membroId, data, valor);
    }
}