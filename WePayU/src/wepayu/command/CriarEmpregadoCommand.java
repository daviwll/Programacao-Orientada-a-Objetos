package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando para criar um empregado no sistema.
 * Usa SnapshotCommand para suportar undo/redo via mementos.
 */
public class CriarEmpregadoCommand extends SnapshotCommand {

    private final String name;
    private final String endereco;
    private final String tipo;
    private final String salario;
    private final String comissao;

    public CriarEmpregadoCommand(Sistema sistema, String name, String endereco, String tipo, String salario) {
        this(sistema, name, endereco, tipo, salario, null);
    }

    public CriarEmpregadoCommand(Sistema sistema, String name, String endereco, String tipo, String salario, String comissao) {
        super(sistema);
        this.name = name;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.comissao = comissao;
    }

    @Override
    protected void doExecute() throws Exception {
        if (comissao == null) {
            sistema.criarEmpregado(name, endereco, tipo, salario);
        } else {
            sistema.criarEmpregado(name, endereco, tipo, salario, comissao);
        }
    }
}