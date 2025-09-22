package wepayu.command;

import wepayu.models.Empregado;
import wepayu.models.TaxaServico;
import wepayu.services.Sistema;

/**
 * Comando para lançar uma taxa de serviço a um membro do sindicato.
 * <p>
 * Esta classe encapsula a ação de adicionar uma taxa de serviço avulsa,
 * permitindo que a operação seja executada e desfeita (undo).
 *
 * @see Command
 * @see Sistema
 * @see wepayu.models.MembroSindicato
 * @see TaxaServico
 */
public class LancarTaxaServicoCommand implements Command {
    private Sistema sistema;
    private String membroId, data, valor;
    private TaxaServico taxaAdicionada;

    /**
     * Constrói o comando para lançar uma taxa de serviço.
     *
     * @param sistema O sistema onde a operação será executada.
     * @param membroId O ID do membro no sindicato que receberá a cobrança.
     * @param data A data em que a taxa foi gerada (ex: "dd/MM/yyyy").
     * @param valor O valor da taxa de serviço.
     */
    public LancarTaxaServicoCommand(Sistema sistema, String membroId, String data, String valor) {
        this.sistema = sistema;
        this.membroId = membroId;
        this.data = data;
        this.valor = valor;
    }

    /**
     * Executa o lançamento da taxa de serviço.
     * <p>
     * Chama o método correspondente em {@link Sistema} e guarda a referência
     * do objeto {@link TaxaServico} criado para ser usado pelo método {@link #undo()}.
     *
     * @throws Exception se o membro do sindicato não for encontrado ou os dados forem inválidos.
     */
    @Override
    public void execute() throws Exception {
        this.taxaAdicionada = sistema.lancaTaxaServicoPorMembro(this.membroId, this.data, this.valor);
    }

    /**
     * Desfaz o lançamento da taxa de serviço, removendo-a do registro do membro.
     *
     * @throws Exception se ocorrer um erro ao localizar o empregado.
     */
    @Override
    public void undo() throws Exception {
        for (Empregado empregado : sistema.getEmpregados()) {
            if (empregado.isSindicalizado() && membroId.equals(empregado.getSindicato().getIdMembro())) {
                empregado.getSindicato().removeTaxa(this.taxaAdicionada);
                break;
            }
        }
    }
}