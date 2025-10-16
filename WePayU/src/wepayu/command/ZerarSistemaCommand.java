package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando para zerar os dados do sistema.
 *
 * <p>Este comando limpa somente os dados internos do {@link Sistema} (empregados, cartões,
 * vendas, taxas etc.), preservando o histórico de undo/redo do {@link Invoker} externo.
 * A captura/restauração do estado para undo/redo é gerenciada por {@link SnapshotCommand}.</p>
 *
 * @since US08
 */
public class ZerarSistemaCommand extends SnapshotCommand {

    /**
     * Cria o comando de zerar sistema.
     *
     * @param sistema instância do sistema (não {@code null})
     */
    public ZerarSistemaCommand(Sistema sistema) {
        super(sistema);
    }

    /**
     * Executa a limpeza dos dados internos do sistema.
     *
     * <p>Não altera as pilhas do invocador; o snapshot feito por {@link SnapshotCommand}
     * permite desfazer/refazer o estado do sistema após a limpeza.</p>
     *
     * @throws Exception se ocorrer erro durante a limpeza
     */
    @Override
    protected void doExecute() throws Exception {
        sistema.zerarDadosInternos();
    }
}
