package wepayu.command;

/**
 * Interface que define o contrato para todos os objetos Command no sistema.
 * <p>
 * Seguindo o padrão de projeto Command, esta interface garante que qualquer
 * ação que modifica o estado do sistema possa ser executada e, crucialmente,
 * desfeita, suportando a funcionalidade de undo/redo.
 *
 * @see Invoker
 */
public interface Command {
    /**
     * Executa a ação encapsulada pelo comando.
     *
     * @throws Exception se ocorrer um erro durante a execução da ação.
     */
    void execute() throws Exception;

    /**
     * Reverte a ação que foi executada pelo método {@link #execute()}.
     * <p>
     * A implementação deste método deve garantir que o estado do sistema
     * retorne à condição em que se encontrava antes da execução do comando.
     *
     * @throws Exception se ocorrer um erro ao tentar reverter a ação.
     */
    void undo() throws Exception;
}