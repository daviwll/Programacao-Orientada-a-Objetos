package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando para alterar o nome de um empregado.
 * <p>
 * Esta classe encapsula a ação de alterar o nome, permitindo que a operação
 * seja executada e desfeita (undo) de forma confiável, guardando o nome
 * anterior à modificação.
 *
 * @see Command
 * @see Sistema
 * @see wepayu.models.Empregado
 */
public class AlterarNomeCommand  implements Command {
    private Sistema sistema;
    private String atributo;
    private String valor;
    private String id;
    private String nomeAntigo;

    /**
     * Constrói o comando para alterar o nome de um empregado.
     *
     * @param sistema A instância do sistema onde a operação será executada.
     * @param id O ID do empregado a ser alterado.
     * @param atributo O nome do atributo a ser alterado (deve ser "nome").
     * @param valor O novo nome do empregado.
     */
    public AlterarNomeCommand(Sistema sistema, String id,  String atributo, String valor) {
        this.sistema = sistema;
        this.id = id;
        this.atributo = atributo;
        this.valor = valor;
    }

    /**
     * Executa a alteração do nome.
     * <p>
     * Antes de executar a alteração, salva o nome antigo do empregado para
     * permitir que a ação seja desfeita.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void execute() throws Exception {
        this.nomeAntigo = sistema.getEmpregado(this.id).getName();
        sistema.alteraEmpregado(this.id, this.atributo, this.valor, null, null);
    }

    /**
     * Desfaz a alteração do nome, restaurando o valor original.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void undo() throws Exception {
        sistema.alteraEmpregado(this.id, this.atributo, this.nomeAntigo, null, null);
    }
}