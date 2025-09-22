package wepayu.command;

import wepayu.models.Empregado;
import wepayu.models.MembroSindicato;
import wepayu.services.Sistema;

import java.util.HashMap;
import java.util.Map;

/**
 * Comando genérico para alterar um atributo de um empregado.
 * <p>
 * Esta classe encapsula diversas ações de modificação de um empregado, como
 * alterar nome, endereço, tipo, método de pagamento e status sindical.
 * Ela utiliza um padrão Memento (com um Map) para salvar o estado anterior
 * do atributo modificado, garantindo uma operação de 'undo' robusta.
 *
 * @see Command
 * @see Sistema
 * @see Empregado
 */
public class AlterarEmpregadoCommand implements Command {
    private Sistema sistema;
    private String id, atributo, valor1, valor2, valor3;
    private Map<String, Object> estadoAnterior = new HashMap<>();

    /**
     * Constrói o comando para alterar um atributo do empregado.
     *
     * @param sistema A instância do sistema onde a operação será executada.
     * @param id O ID do empregado a ser alterado.
     * @param atributo O nome do atributo a ser alterado (ex: "nome", "tipo").
     * @param v1 O valor principal da alteração.
     * @param v2 O valor secundário, usado para alterações mais complexas (ex: novo salário ao mudar de tipo).
     * @param v3 O terceiro valor, usado para alterações mais complexas (ex: taxa sindical).
     */
    public AlterarEmpregadoCommand(Sistema sistema, String id, String atributo, String v1, String v2, String v3) {
        this.sistema = sistema;
        this.id = id;
        this.atributo = atributo;
        this.valor1 = v1;
        this.valor2 = v2;
        this.valor3 = v3;
    }

    /**
     * Executa a alteração no empregado.
     * <p>
     * Antes de modificar, o método salva o estado atual do atributo que será
     * alterado. Isso permite que o comando {@link #undo()} possa restaurar
     * o estado original de forma precisa.
     *
     * @throws Exception se o empregado não for encontrado ou a alteração for inválida.
     */
    @Override
    public void execute() throws Exception {
        Empregado empregado = sistema.getEmpregado(id);

        switch (atributo.toLowerCase()) {
            case "nome":
                estadoAnterior.put("valor", empregado.getName());
                break;
            case "endereco":
                estadoAnterior.put("valor", empregado.getEndereco());
                break;
            case "tipo":
                estadoAnterior.put("empregadoAntigo", empregado);
                break;
            case "metodopagamento":
                estadoAnterior.put("metodo", empregado.getMetodoPagamento());
                estadoAnterior.put("banco", empregado.getBanco());
                estadoAnterior.put("agencia", empregado.getAgencia());
                estadoAnterior.put("conta", empregado.getContaCorrente());
                break;
            case "sindicalizado":
                estadoAnterior.put("sindicato", empregado.getSindicato());
                break;
        }
        sistema.alteraEmpregado(id, atributo, valor1, valor2, valor3);
    }

    /**
     * Desfaz a alteração, restaurando o estado anterior do empregado.
     * <p>
     * Utiliza os dados salvos durante a execução do {@link #execute()} para
     * reverter a modificação no empregado.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void undo() throws Exception {
        Empregado empregado = sistema.getEmpregado(id);

        switch (atributo.toLowerCase()) {
            case "nome":
                empregado.setName((String) estadoAnterior.get("valor"));
                break;
            case "endereco":
                empregado.setEndereco((String) estadoAnterior.get("valor"));
                break;
            case "metodopagamento":
                empregado.setMetodoPagamento((String) estadoAnterior.get("metodo"));
                empregado.setBanco((String) estadoAnterior.get("banco"));
                empregado.setAgencia((String) estadoAnterior.get("agencia"));
                empregado.setContaCorrente((String) estadoAnterior.get("conta"));
                break;
            case "sindicalizado":
                empregado.setSindicato((MembroSindicato) estadoAnterior.get("sindicato"));
                break;
            case "tipo":
                sistema.substituirEmpregado(empregado, (Empregado) estadoAnterior.get("empregadoAntigo"));
                break;
        }
    }
}