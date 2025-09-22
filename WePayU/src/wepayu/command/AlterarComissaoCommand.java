package wepayu.command;

import wepayu.models.Comissionado;
import wepayu.services.Sistema;

/**
 * Comando para alterar a taxa de comissão de um empregado comissionado.
 * <p>
 * Esta classe encapsula a ação de alterar a comissão, permitindo que a operação
 * seja executada e desfeita (undo) de forma confiável, guardando o estado anterior
 * da comissão.
 *
 * @see Command
 * @see Sistema
 * @see Comissionado
 */
public class AlterarComissaoCommand implements Command {
    private Sistema sistema;
    private String atributo;
    private String valor;
    private String id;
    private double comissaoAntiga;

    /**
     * Constrói o comando para alterar a comissão de um empregado.
     *
     * @param sistema A instância do sistema onde a operação será executada.
     * @param id O ID do empregado a ser alterado.
     * @param atributo O nome do atributo a ser alterado (deve ser "comissao").
     * @param valor O novo valor da taxa de comissão, em formato de String.
     */
    public AlterarComissaoCommand(Sistema sistema, String id,  String atributo, String valor) {
        this.sistema = sistema;
        this.id = id;
        this.atributo = atributo;
        this.valor = valor;
    }

    /**
     * Executa a alteração da taxa de comissão.
     * <p>
     * Antes de executar a alteração, salva o valor antigo da comissão para
     * permitir que a ação seja desfeita.
     *
     * @throws Exception se o empregado não for encontrado ou não for do tipo comissionado.
     */
    @Override
    public void execute() throws Exception {
        this.comissaoAntiga = ((Comissionado) sistema.getEmpregado(this.id)).getComissao();
        sistema.alteraEmpregado(this.id, this.atributo, this.valor, null, null);
    }

    /**
     * Desfaz a alteração da taxa de comissão, restaurando o valor original.
     *
     * @throws Exception se o empregado não for encontrado ou não for do tipo comissionado.
     */
    @Override
    public void undo() throws Exception {
        sistema.alteraEmpregado(this.id, this.atributo, String.valueOf(this.comissaoAntiga), null, null);
    }
}