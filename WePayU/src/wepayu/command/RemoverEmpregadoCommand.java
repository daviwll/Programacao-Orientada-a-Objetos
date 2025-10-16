package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando para remover um empregado do sistema.
 * Undo/redo são tratados por SnapshotCommand via mementos do Sistema.
 */
public class RemoverEmpregadoCommand extends SnapshotCommand {

    private final String id;

    public RemoverEmpregadoCommand(Sistema sistema, String id) {
        super(sistema);
        this.id = id;
    }

    @Override
    protected void doExecute() throws Exception {
        sistema.removerEmpregado(id);
    }
}