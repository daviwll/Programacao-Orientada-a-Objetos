package wepayu.command;

import java.util.Stack;

/**
 * Implementa o "Invocador" (Invoker) do padrão de projeto Command.
 * Gerencia o histórico de ações para a funcionalidade de undo/redo.
 */
public class Invoker {
    private Stack<Command> undoStack = new Stack<>();
    private Stack<Command> redoStack = new Stack<>();

    /**
     * Executa um comando e gerencia o histórico.
     * @param command O comando a ser executado.
     * @throws Exception se ocorrer um erro na execução.
     */
    public void executeCommand(Command command) throws Exception {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    /**
     * Desfaz o último comando.
     * @throws Exception se não houver comando para desfazer.
     */
    public void undoLastCommand() throws Exception {
        if (undoStack.isEmpty()) {
            throw new Exception("Nao ha comando a desfazer.");
        }
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
    }

    /**
     * Refaz o último comando desfeito.
     * @throws Exception se não houver comando para refazer.
     */
    public void redoLastCommand() throws Exception {
        if (redoStack.isEmpty()) {
            throw new Exception("Nao ha comando a refazer.");
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
    }

    /**
     * Limpa completamente o histórico de comandos.
     */
    public void clearHistory() {
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * Salva o estado atual do histórico em um Memento.
     * @return um {@link InvokerMemento} contendo o estado atual.
     */
    public InvokerMemento save() {
        return new InvokerMemento(this.undoStack, this.redoStack);
    }

    /**
     * Restaura o estado do histórico a partir de um Memento.
     * @param memento O {@link InvokerMemento} com o estado a ser restaurado.
     */
    public void restore(InvokerMemento memento) {
        this.undoStack = memento.getUndoState();
        this.redoStack = memento.getRedoState();
    }
}