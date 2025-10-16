package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando para lançar um cartão de ponto para um empregado horista.
 *
 * <p>A captura/restauração de estado para undo/redo é feita por {@link SnapshotCommand} via
 * mementos do {@link Sistema}. Este comando apenas delega a operação a
 * {@link Sistema#lancaCartao(String, String, String)}.</p>
 *
 * @since US08
 * @see SnapshotCommand
 * @see Sistema#lancaCartao(String, String, String)
 */
public class LancarCartaoCommand extends SnapshotCommand {

    private final String id;
    private final String data;
    private final String horas;

    /**
     * Cria o comando de lançamento de cartão de ponto.
     *
     * @param sistema instância do sistema (não {@code null})
     * @param id id do empregado horista
     * @param data data do cartão no formato {@code d/M/uuuu}
     * @param horas quantidade de horas trabalhadas no dia
     * @throws NullPointerException se {@code sistema} for {@code null}
     */
    public LancarCartaoCommand(Sistema sistema, String id, String data, String horas) {
        super(sistema);
        this.id = id;
        this.data = data;
        this.horas = horas;
    }

    /**
     * Executa o lançamento do cartão de ponto.
     *
     * @throws Exception se o empregado não for horista ou se a data/horas forem inválidas
     */
    @Override
    protected void doExecute() throws Exception {
        sistema.lancaCartao(id, data, horas);
    }
}
