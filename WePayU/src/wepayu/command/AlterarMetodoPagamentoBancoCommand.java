package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando para alterar o método de pagamento de um empregado para depósito bancário.
 *
 * <p>A captura/restauração de estado para undo/redo é realizada por {@link SnapshotCommand}
 * via mementos do {@link Sistema}. Este comando apenas delega a operação a
 * {@link Sistema#alteraEmpregadoMetodoPagamentoBanco(String, String, String, String)}.</p>
 *
 * @since US08
 * @see SnapshotCommand
 * @see Sistema#alteraEmpregadoMetodoPagamentoBanco(String, String, String, String)
 */
public class AlterarMetodoPagamentoBancoCommand extends SnapshotCommand {

    private final String id;
    private final String banco;
    private final String agencia;
    private final String contaCorrente;

    /**
     * Cria o comando de alteração do método de pagamento para banco.
     *
     * @param sistema instância do sistema (não {@code null})
     * @param id id do empregado
     * @param banco nome do banco
     * @param agencia número da agência
     * @param contaCorrente número da conta corrente
     * @throws NullPointerException se {@code sistema} for {@code null}
     */
    public AlterarMetodoPagamentoBancoCommand(Sistema sistema,
                                              String id,
                                              String banco,
                                              String agencia,
                                              String contaCorrente) {
        super(sistema);
        this.id = id;
        this.banco = banco;
        this.agencia = agencia;
        this.contaCorrente = contaCorrente;
    }

    /**
     * Executa a alteração do método de pagamento para depósito bancário.
     *
     * @throws Exception se o empregado não existir ou os dados bancários forem inválidos
     */
    @Override
    protected void doExecute() throws Exception {
        sistema.alteraEmpregadoMetodoPagamentoBanco(id, banco, agencia, contaCorrente);
    }
}
