package wepayu.command;
import wepayu.services.Sistema;

/**
 * Comando abstrato com suporte a snapshot para undo/redo.
 * <p>
 * Implementa o padrão <em>Command</em> em conjunto com o padrão <em>Memento</em>.
 * Antes de executar a operação concreta, captura um snapshot do {@link Sistema}
 * (estado "antes"); após executar, captura outro snapshot (estado "depois").
 * Assim, o {@link #undo()} restaura o estado anterior e o {@link #redo()} restaura
 * o estado posterior em tempo constante.
 * </p>
 *
 * <h3>Como estender</h3>
 * <ul>
 *   <li>Subclasses devem implementar apenas {@link #doExecute()} com a lógica da operação;</li>
 *   <li>Não sobrescreva {@link #execute()}, {@link #undo()} ou {@link #redo()} —
 *       eles já gerenciam os snapshots automaticamente.</li>
 * </ul>
 *
 * @see Command
 * @see Sistema
 * @see SistemaMemento
 */
public abstract class SnapshotCommand implements Command {
    protected final Sistema sistema;
    private SistemaMemento before, after;

    protected SnapshotCommand(Sistema sistema) { this.sistema = sistema; }

    /**
     * Executa o comando com captura automática de snapshots.
     * <ol>
     *   <li>Salva o snapshot "antes";</li>
     *   <li>Executa a lógica específica em {@link #doExecute()};</li>
     *   <li>Salva o snapshot "depois".</li>
     * </ol>
     *
     * @throws Exception se ocorrer erro durante a execução da lógica concreta
     */
    @Override
    public final void execute() throws Exception {
        before = sistema.save();
        doExecute();
        after = sistema.save();
    }

    /**
     * Desfaz o comando restaurando o snapshot anterior à execução.
     *
     * @throws Exception se houver falha na restauração do estado
     */
    @Override
    public final void undo() throws Exception { sistema.restore(before); }


    /**
     * Refaz o comando restaurando o snapshot posterior à execução.
     *
     * @throws Exception se houver falha na restauração do estado
     */
    @Override
    public final void redo() throws Exception { sistema.restore(after); }

    /**
     * Ponto de extensão que contém a lógica concreta do comando.
     * <p>
     * É chamado entre as capturas de snapshot em {@link #execute()}.
     * Toda mutação de estado do sistema deve ocorrer aqui.
     * </p>
     *
     * @throws Exception se a operação concreta falhar
     */
    protected abstract void doExecute() throws Exception;
}
