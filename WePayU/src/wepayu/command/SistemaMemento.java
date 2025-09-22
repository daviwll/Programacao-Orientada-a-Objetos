package wepayu.command;

import wepayu.models.Empregado;
import java.util.ArrayList;

/**
 * Implementa o "Memento" para a classe {@link wepayu.services.Sistema}.
 * <p>
 * Esta classe armazena um snapshot (uma "foto") do estado interno do {@link wepayu.services.Sistema},
 * especificamente a lista de empregados e o contador de IDs. Ela é usada pelo {@link ZerarSistemaCommand}
 * para permitir que a operação de resetar o sistema seja desfeita.
 *
 * @see wepayu.services.Sistema
 * @see ZerarSistemaCommand
 */
public class SistemaMemento {
    private final ArrayList<Empregado> empregadosState;
    private final int idState;

    /**
     * Constrói um novo Memento do Sistema, salvando o estado atual.
     * <p>
     * A lista de empregados é clonada para garantir que o Memento contenha uma cópia
     * profunda e independente do estado, e não apenas uma referência ao estado original.
     *
     * @param empregados A lista de empregados a ser salva.
     * @param id O valor atual do contador de IDs a ser salvo.
     */
    public SistemaMemento(ArrayList<Empregado> empregados, int id) {
        this.empregadosState = new ArrayList<>();
        for (Empregado e : empregados) {
            this.empregadosState.add(e.clone());
        }
        this.idState = id;
    }

    /**
     * Retorna o estado salvo da lista de empregados.
     *
     * @return Uma {@link ArrayList} contendo os empregados do estado salvo.
     */
    public ArrayList<Empregado> getEmpregadosState() {
        return empregadosState;
    }

    /**
     * Retorna o estado salvo do contador de IDs.
     *
     * @return O valor do contador de IDs salvo.
     */
    public int getIdState() {
        return idState;
    }
}