package wepayu.command;

import java.util.Stack;

/**
 * Memento para a classe {@link Invoker}.
 * <p>
 * Armazena um snapshot do estado interno do {@link Invoker},
 * especificamente das pilhas de undo e redo, para permitir que a operação
 * de resetar o histórico seja desfeita.
 *
 * @see Invoker
 * @see ZerarSistemaCommand
 */
public class InvokerMemento {
    private final Stack<Command> undoState;
    private final Stack<Command> redoState;

    /**
     * Constrói um novo Memento do Invoker.
     * <p>
     * As pilhas são clonadas para garantir que o Memento contenha uma cópia
     * independente do estado.
     *
     * @param undoStack A pilha de undo a ser salva.
     * @param redoStack A pilha de redo a ser salva.
     */
    @SuppressWarnings("unchecked")
    public InvokerMemento(Stack<Command> undoStack, Stack<Command> redoStack) {
        this.undoState = (Stack<Command>) undoStack.clone();
        this.redoState = (Stack<Command>) redoStack.clone();
    }

    /**
     * Retorna o estado salvo da pilha de undo.
     * @return Uma {@link Stack} com o estado de undo salvo.
     */
    public Stack<Command> getUndoState() {
        return undoState;
    }

    /**
     * Retorna o estado salvo da pilha de redo.
     * @return Uma {@link Stack} com o estado de redo salvo.
     */
    public Stack<Command> getRedoState() {
        return redoState;
    }
}