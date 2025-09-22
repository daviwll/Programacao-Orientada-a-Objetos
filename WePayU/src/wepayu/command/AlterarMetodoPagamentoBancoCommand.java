package wepayu.command;

import wepayu.models.Empregado;
import wepayu.services.Sistema;

/**
 * Comando para alterar o método de pagamento de um empregado para depósito bancário.
 * <p>
 * Esta classe encapsula a ação de alterar o método de pagamento e os dados bancários associados,
 * permitindo que a operação seja executada e desfeita (undo) de forma segura, salvando e
 * restaurando todo o estado de pagamento anterior.
 *
 * @see Command
 * @see Sistema
 * @see Empregado
 */
public class AlterarMetodoPagamentoBancoCommand implements Command {
    private Sistema sistema;
    private String id;
    private String banco, agencia, contaCorrente;
    private String metodoAntigo, bancoAntigo, agenciaAntiga, contaAntiga;

    /**
     * Constrói o comando para alterar o método de pagamento para banco.
     *
     * @param s A instância do sistema onde a operação será executada.
     * @param id O ID do empregado a ser alterado.
     * @param b O nome do novo banco.
     * @param a O número da nova agência.
     * @param c O número da nova conta corrente.
     */
    public AlterarMetodoPagamentoBancoCommand(Sistema s, String id, String b, String a, String c) {
        this.sistema = s;
        this.id = id;
        this.banco = b;
        this.agencia = a;
        this.contaCorrente = c;
    }

    /**
     * Executa a alteração do método de pagamento.
     * <p>
     * Antes de executar, salva todos os dados de pagamento anteriores do empregado
     * (método, banco, agência, conta) para permitir que a ação seja desfeita.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void execute() throws Exception {
        Empregado e = sistema.getEmpregado(id);
        this.metodoAntigo = e.getMetodoPagamento();
        this.bancoAntigo = e.getBanco();
        this.agenciaAntiga = e.getAgencia();
        this.contaAntiga = e.getContaCorrente();

        sistema.alteraEmpregadoMetodoPagamentoBanco(id, banco, agencia, contaCorrente);
    }

    /**
     * Desfaz a alteração do método de pagamento, restaurando todos os dados bancários originais.
     *
     * @throws Exception se o empregado não for encontrado.
     */
    @Override
    public void undo() throws Exception {
        Empregado e = sistema.getEmpregado(id);
        e.setMetodoPagamento(this.metodoAntigo);
        e.setBanco(this.bancoAntigo);
        e.setAgencia(this.agenciaAntiga);
        e.setContaCorrente(this.contaAntiga);
    }
}