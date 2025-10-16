package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando genérico para alterar atributos de um empregado.
 *
 * <p>Este comando delega a alteração para {@link Sistema#alteraEmpregado(String, String, String, String, String)}
 * e confia no mecanismo de snapshot de {@link SnapshotCommand} para suportar
 * undo/redo de forma transacional, sem armazenar estado manualmente.</p>
 */
public class AlterarEmpregadoCommand extends SnapshotCommand {

    private final String id;
    private final String atributo;
    private final String valor1;
    private final String valor2;
    private final String valor3;

    /**
     * Cria o comando de alteração de empregado.
     *
     * @param sistema instância do sistema (não {@code null})
     * @param id identificador do empregado
     * @param atributo nome do atributo a alterar
     * @param v1 primeiro valor (obrigatório)
     * @param v2 segundo valor (opcional, pode ser {@code null})
     * @param v3 terceiro valor (opcional, pode ser {@code null})
     */
    public AlterarEmpregadoCommand(Sistema sistema, String id, String atributo, String v1, String v2, String v3) {
        super(sistema);
        this.id = id;
        this.atributo = atributo;
        this.valor1 = v1;
        this.valor2 = v2;
        this.valor3 = v3;
    }

    /**
     * Executa a alteração delegando ao {@link Sistema}.
     *
     * @throws Exception se a alteração for inválida
     */
    @Override
    protected void doExecute() throws Exception {
        sistema.alteraEmpregado(id, atributo, valor1, valor2, valor3);
    }
}
