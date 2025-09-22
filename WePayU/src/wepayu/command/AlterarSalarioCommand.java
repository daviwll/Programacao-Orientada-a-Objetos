package wepayu.command;

import wepayu.models.Assalariado;
import wepayu.models.Empregado;
import wepayu.models.Horista;
import wepayu.services.Sistema;

/**
 * Comando para alterar o salário de um empregado.
 * <p>
 * Esta classe encapsula a ação de alterar o salário, lidando com os diferentes
 * tipos de remuneração (horária ou mensal) e permitindo que a operação
 * seja executada e desfeita (undo).
 *
 * @see Command
 * @see Sistema
 * @see Empregado
 */
public class AlterarSalarioCommand  implements Command {
    private Sistema sistema;
    private String atributo;
    private String valor;
    private String id;
    private double salarioAntigo;

    /**
     * Constrói o comando para alterar o salário de um empregado.
     *
     * @param sistema A instância do sistema onde a operação será executada.
     * @param id O ID do empregado a ser alterado.
     * @param atributo O nome do atributo a ser alterado (deve ser "salario").
     * @param valor O novo valor do salário, em formato de String.
     */
    public AlterarSalarioCommand(Sistema sistema, String id,  String atributo, String valor) {
        this.sistema = sistema;
        this.id = id;
        this.atributo = atributo;
        this.valor = valor;
    }

    /**
     * Executa a alteração do salário.
     * <p>
     * Antes de executar a alteração, verifica o tipo do empregado para salvar
     * corretamente o salário antigo (seja ele horário ou mensal), permitindo
     * que a ação seja desfeita.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void execute() throws  Exception {
        Empregado empregado = sistema.getEmpregado(this.id);

        if (empregado instanceof Assalariado) {
            this.salarioAntigo = ((Assalariado) empregado).getSalarioMensal();
        } else if (empregado instanceof Horista) {
            this.salarioAntigo = ((Horista) empregado).getSalarioHora();
        }
        sistema.alteraEmpregado(this.id, this.atributo, this.valor, null,null);
    }

    /**
     * Desfaz a alteração do salário, restaurando o valor original.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void undo() throws Exception {
        sistema.alteraEmpregado(this.id, this.atributo, String.valueOf(this.salarioAntigo), null, null);
    }
}