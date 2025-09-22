package wepayu.command;

import wepayu.services.Sistema;

/**
 * Comando para alterar o endereço de um empregado.
 * <p>
 * Esta classe encapsula a ação de alterar o endereço, permitindo que a operação
 * seja executada e desfeita (undo) de forma confiável, guardando o endereço
 * anterior à modificação.
 *
 * @see Command
 * @see Sistema
 * @see wepayu.models.Empregado
 */
public class AlterarEnderecoCommand  implements Command {
    private Sistema sistema;
    private String atributo;
    private String valor;
    private String id;
    private String enderecoAntigo;

    /**
     * Constrói o comando para alterar o endereço de um empregado.
     *
     * @param sistema A instância do sistema onde a operação será executada.
     * @param id O ID do empregado a ser alterado.
     * @param atributo O nome do atributo a ser alterado (deve ser "endereco").
     * @param valor O novo endereço do empregado.
     */
    public AlterarEnderecoCommand(Sistema sistema, String id,  String atributo, String valor) {
        this.sistema = sistema;
        this.id = id;
        this.atributo = atributo;
        this.valor = valor;
    }

    /**
     * Executa a alteração do endereço.
     * <p>
     * Antes de executar a alteração, salva o endereço antigo do empregado para
     * permitir que a ação seja desfeita.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void execute() throws Exception {
        this.enderecoAntigo = sistema.getEmpregado(this.id).getEndereco();
        sistema.alteraEmpregado(this.id, this.atributo, this.valor, null, null);
    }

    /**
     * Desfaz a alteração do endereço, restaurando o valor original.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void undo() throws Exception {
        sistema.alteraEmpregado(this.id, this.atributo, this.enderecoAntigo, null, null);
    }
}