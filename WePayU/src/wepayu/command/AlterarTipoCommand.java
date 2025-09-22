package wepayu.command;

import wepayu.models.Empregado;
import wepayu.services.Sistema;

/**
 * Comando para alterar o tipo de um empregado (ex: de Horista para Assalariado).
 * <p>
 * Esta classe encapsula a ação de alterar o tipo, permitindo que a operação
 * seja executada e desfeita (undo) de forma confiável, guardando o tipo
 * anterior à modificação.
 *
 * @see Command
 * @see Sistema
 * @see Empregado
 */
public class AlterarTipoCommand  implements Command {
    private Sistema sistema;
    private String atributo;
    private String valor;
    private String id;
    private String tipoAntigo;

    /**
     * Constrói o comando para alterar o tipo de um empregado.
     *
     * @param sistema A instância do sistema onde a operação será executada.
     * @param id O ID do empregado a ser alterado.
     * @param atributo O nome do atributo a ser alterado (deve ser "tipo").
     * @param valor O novo tipo do empregado (ex: "horista", "assalariado").
     */
    public AlterarTipoCommand(Sistema sistema, String id,  String atributo, String valor) {
        this.sistema = sistema;
        this.id = id;
        this.atributo = atributo;
        this.valor = valor;
    }

    /**
     * Executa a alteração do tipo do empregado.
     * <p>
     * Antes de executar a alteração, salva o tipo antigo do empregado para
     * permitir que a ação seja desfeita. A lógica de negócio no {@link Sistema}
     * é responsável por manter o salário ao realizar a troca.
     *
     * @throws Exception se o empregado não for encontrado ou o tipo for inválido.
     */
    @Override
    public void execute() throws Exception {
        this.tipoAntigo = sistema.getEmpregado(this.id).getTipo();
        sistema.alteraEmpregado(this.id, this.atributo, this.valor, null, null);
    }

    /**
     * Desfaz a alteração do tipo, restaurando o tipo original do empregado.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void undo() throws Exception {
        sistema.alteraEmpregado(this.id, this.atributo, this.tipoAntigo, null, null);
    }
}