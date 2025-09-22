package wepayu.command;

import wepayu.models.Empregado;
import wepayu.services.Sistema;

/**
 * Comando para criar um empregado no sistema.
 * Permite execução, undo e redo.
 */
public class CriarEmpregadoCommand implements Command {

    private Sistema sistema;
    private String name;
    private String endereco;
    private String tipo;
    private String salario;
    private String comissao;
    private Empregado empregadoCriado;

    /**
     * Construtor para empregado horista ou assalariado.
     *
     * @param sistema Instância do Sistema
     * @param name Nome do empregado
     * @param endereco Endereço do empregado
     * @param tipo Tipo do empregado (horista/assalariado)
     * @param salario Salário do empregado
     */
    public CriarEmpregadoCommand(Sistema sistema, String name, String endereco, String tipo, String salario) {
        this(sistema, name, endereco, tipo, salario, null);
    }

    /**
     * Construtor para empregado comissionado ou outros tipos.
     *
     * @param sistema Instância do Sistema
     * @param name Nome do empregado
     * @param endereco Endereço do empregado
     * @param tipo Tipo do empregado
     * @param salario Salário do empregado
     * @param comissao Percentual de comissão (opcional)
     */
    public CriarEmpregadoCommand(Sistema sistema, String name, String endereco, String tipo, String salario, String comissao) {
        this.sistema = sistema;
        this.name = name;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.comissao = comissao;
    }

    @Override
    public void execute() throws Exception {
        if (this.empregadoCriado == null) {
            String id;
            if (this.comissao == null) {
                id = sistema.criarEmpregado(this.name, this.endereco, this.tipo, this.salario);
            } else {
                id = sistema.criarEmpregado(this.name, this.endereco, this.tipo, this.salario, this.comissao);
            }
            this.empregadoCriado = sistema.getEmpregado(id);
        } else {
            sistema.addEmpregado(this.empregadoCriado);
        }
    }

    @Override
    public void undo() throws Exception {
        if (this.empregadoCriado != null) {
            sistema.removerEmpregado(this.empregadoCriado.getId());
        }
    }
}