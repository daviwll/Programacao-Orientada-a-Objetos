package wepayu.command;

import wepayu.models.Comissionado;
import wepayu.models.Empregado;
import wepayu.models.ResultadoDeVenda;
import wepayu.services.Sistema;

/**
 * Comando para lançar um resultado de venda para um empregado comissionado.
 * <p>
 * Esta classe encapsula a ação de adicionar um resultado de venda, permitindo que a operação
 * seja executada e desfeita (undo).
 *
 * @see Command
 * @see Sistema
 * @see Comissionado
 * @see ResultadoDeVenda
 */
public class LancarVendaCommand implements Command {
    private Sistema sistema;
    private String id;
    private String data;
    private String valor;
    private ResultadoDeVenda vendaAdicionada;

    /**
     * Constrói o comando para lançar um resultado de venda.
     *
     * @param sistema A instância do sistema onde a operação será executada.
     * @param id O ID do empregado comissionado.
     * @param data A data da venda (ex: "dd/MM/yyyy").
     * @param valor O valor da venda.
     */
    public LancarVendaCommand(Sistema sistema, String id, String data, String valor) {
        this.sistema = sistema;
        this.id = id;
        this.data = data;
        this.valor = valor;
    }

    /**
     * Executa o lançamento do resultado de venda.
     * <p>
     * Chama o método correspondente em {@link Sistema} e guarda a referência
     * do objeto {@link ResultadoDeVenda} criado para ser usado pelo método {@link #undo()}.
     *
     * @throws Exception se o empregado não for encontrado, não for comissionado, ou os dados forem inválidos.
     */
    @Override
    public void execute() throws Exception {
        this.vendaAdicionada = this.sistema.lancaVenda(this.id, this.data, this.valor);
    }

    /**
     * Desfaz o lançamento da venda, removendo-a do registro do empregado.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void undo() throws Exception {
        if (this.vendaAdicionada == null) return;
        Empregado empregado = sistema.getEmpregado(this.id);
        if (empregado instanceof Comissionado) {
            ((Comissionado) empregado).removeVendaEspecifica(this.vendaAdicionada);
        }
        this.vendaAdicionada = null;
    }
}