package wepayu.command;

import wepayu.models.CartaoDePonto;
import wepayu.models.Empregado;
import wepayu.models.Horista;
import wepayu.services.Sistema;

/**
 * Comando para lançar um cartão de ponto para um empregado horista.
 * <p>
 * Esta classe encapsula a ação de adicionar um cartão de ponto, permitindo que a operação
 * seja executada e desfeita (undo).
 *
 * @see Command
 * @see Sistema
 * @see Horista
 * @see CartaoDePonto
 */
public class LancarCartaoCommand implements Command {
    private Sistema sistema;
    private String id, data, horas;
    private CartaoDePonto cartaoAdicionado;

    /**
     * Constrói o comando para lançar um cartão de ponto.
     *
     * @param sistema A instância do sistema onde a operação será executada.
     * @param id O ID do empregado horista.
     * @param data A data do cartão de ponto (ex: "dd/MM/yyyy").
     * @param horas O número de horas trabalhadas no dia.
     */
    public LancarCartaoCommand(Sistema sistema, String id, String data, String horas) {
        this.sistema = sistema;
        this.id = id;
        this.data = data;
        this.horas = horas;
    }

    /**
     * Executa o lançamento do cartão de ponto.
     * <p>
     * Chama o método correspondente em {@link Sistema} e guarda a referência
     * do objeto {@link CartaoDePonto} criado para ser usado pelo método {@link #undo()}.
     *
     * @throws Exception se o empregado não for encontrado, não for horista, ou os dados forem inválidos.
     */
    @Override
    public void execute() throws Exception {
        this.cartaoAdicionado = sistema.lancaCartao(this.id, this.data, this.horas);
    }

    /**
     * Desfaz o lançamento do cartão de ponto, removendo-o do registro do empregado.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void undo() throws Exception {
        Empregado empregado = sistema.getEmpregado(this.id);
        if (empregado instanceof Horista) {
            ((Horista) empregado).removeCartaoDePonto(this.cartaoAdicionado);
        }
    }
}