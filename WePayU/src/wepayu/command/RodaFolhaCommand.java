package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando responsável por rodar a folha de pagamento para uma data específica.
 *
 * <p>A captura de estado anterior/posterior (para suportar undo/redo) é feita por
 * {@link SnapshotCommand}, que chama {@code sistema.save()} antes e depois da execução
 * e restaura com {@code sistema.restore(...)} quando necessário.</p>
 *
 * <p>Este comando delega a operação para {@link Sistema#rodaFolha(String, String)}.</p>
 *
 * @since US07/US08
 * @see SnapshotCommand
 * @see Sistema#rodaFolha(String, String)
 */
public class RodaFolhaCommand extends SnapshotCommand {

    private final String data;
    private final String saida;

    /**
     * Cria um comando para processar a folha de pagamento em uma data, gerando o arquivo de saída.
     *
     * @param sistema instância do sistema onde a folha será processada (não {@code null})
     * @param data data de referência no formato {@code d/M/uuuu}
     * @param saida caminho/arquivo de saída a ser escrito
     *
     * @throws NullPointerException se {@code sistema} for {@code null}
     */
    public RodaFolhaCommand(Sistema sistema, String data, String saida) {
        super(sistema);
        this.data = data;
        this.saida = saida;
    }

    /**
     * Executa o processamento da folha de pagamento para a data e arquivo informados.
     * As exceções de validação de data e escrita de arquivo são propagadas.
     *
     * @throws Exception se a data for inválida, a saída for inválida ou ocorrer erro de I/O
     */
    @Override
    protected void doExecute() throws Exception {
        sistema.rodaFolha(data, saida);
    }
}
