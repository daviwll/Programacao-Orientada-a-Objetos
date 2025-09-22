package wepayu.command;

import wepayu.models.Empregado;
import wepayu.services.Sistema;

/**
 * Comando para remover um empregado do sistema.
 * Permite execução, undo e redo.
 */
public class RemoverEmpregadoCommand implements Command {

    private Sistema sistema;
    private String id;
    private Empregado empregadoRemovido;

    /**
     * Construtor do comando.
     *
     * @param sistema Instância do Sistema
     * @param id ID do empregado a ser removido
     */
    public RemoverEmpregadoCommand(Sistema sistema, String id) {
        this.sistema = sistema;
        this.id = id;
    }

    @Override
    public void execute() throws Exception {
        if (this.empregadoRemovido == null) {
            this.empregadoRemovido = sistema.getEmpregado(this.id);
        }
        sistema.removerEmpregado(this.empregadoRemovido.getId());
    }

    @Override
    public void undo() throws Exception {
        if (this.empregadoRemovido != null) {
            sistema.addEmpregado(this.empregadoRemovido);
        }
    }
}
