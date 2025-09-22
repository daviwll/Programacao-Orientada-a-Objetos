package wepayu.command;

import wepayu.services.Sistema;

/**
 * Implementa o padrão de projeto Facade para o sistema WePayU.
 * <p>
 * Esta classe fornece uma interface simplificada e unificada para todas as
 * funcionalidades do subsistema de folha de pagamento. É a única porta
 * de entrada para a lógica de negócio, recebendo os comandos dos testes
 * e delegando-os para as classes apropriadas ({@link Sistema} e {@link Command}s).
 *
 * @see Sistema
 * @see Invoker
 * @see Command
 */
public class Facade {
    private Sistema sistema;
    private Invoker invoker;
    private static Sistema sistemaGlobal;
    private static Invoker invokerGlobal;
    private static boolean sistemaEncerrado = false;

    /**
     * Constrói uma nova instância da Facade.
     * <p>
     * Utiliza campos estáticos para garantir que o estado do sistema ({@code sistemaGlobal}) e do
     * histórico de comandos ({@code invokerGlobal}) seja mantido entre diferentes execuções
     * de testes, o que é essencial para os testes de persistência.
     */
    public Facade() {
        if (Facade.sistemaGlobal == null) {
            sistemaGlobal = new Sistema();
            invokerGlobal = new Invoker();
        }
        this.sistema = sistemaGlobal;
        this.invoker = invokerGlobal;
        sistemaEncerrado = false;
    }

    /**
     * Define o estado de encerramento do sistema.
     * Usado por comandos para reativar o sistema.
     *
     * @param estado {@code true} para encerrar, {@code false} para reabrir.
     */
    public void setSistemaEncerrado(boolean estado) {
        Facade.sistemaEncerrado = estado;
    }

    /**
     * Reseta completamente o estado do sistema e o histórico de comandos.
     * @throws Exception se o sistema estiver encerrado.
     */
    public void zerarSistema() throws Exception {
        checkSistemaEncerrado();
        Command comando = new ZerarSistemaCommand(this.sistema);
        this.invoker.executeCommand(comando);
    }

    /**
     * "Encerra" o sistema, bloqueando a execução de novos comandos de ação.
     */
    public void encerrarSistema() {
        Facade.sistemaEncerrado = true;
    }

    /**
     * Verifica se o sistema foi encerrado. Se sim, lança uma exceção.
     *
     * @throws Exception se o sistema estiver no estado "encerrado".
     */
    private void checkSistemaEncerrado() throws Exception {
        if (Facade.sistemaEncerrado) {
            throw new Exception("Nao pode dar comandos depois de encerrarSistema.");
        }
    }

    /**
     * Cria um novo empregado Horista ou Assalariado.
     *
     * @param nome O nome do novo empregado.
     * @param endereco O endereço do novo empregado.
     * @param tipo O tipo do novo empregado ("horista" ou "assalariado").
     * @param salario O salário do novo empregado.
     * @return O ID do empregado recém-criado.
     * @throws Exception se os dados forem inválidos ou o sistema estiver encerrado.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        checkSistemaEncerrado();
        Command comando = new CriarEmpregadoCommand(this.sistema, nome, endereco, tipo, salario);
        this.invoker.executeCommand(comando);
        return String.valueOf(sistema.getId());
    }

    /**
     * Cria um novo empregado Comissionado.
     *
     * @param nome O nome do novo empregado.
     * @param endereco O endereço do novo empregado.
     * @param tipo O tipo do novo empregado (deve ser "comissionado").
     * @param salario O salário base do novo empregado.
     * @param comissao A taxa de comissão do novo empregado.
     * @return O ID do empregado recém-criado.
     * @throws Exception se os dados forem inválidos ou o sistema estiver encerrado.
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        checkSistemaEncerrado();
        Command comando = new CriarEmpregadoCommand(this.sistema, nome, endereco, tipo, salario, comissao);
        this.invoker.executeCommand(comando);
        return String.valueOf(sistema.getId());
    }

    /**
     * Remove um empregado do sistema.
     *
     * @param id O ID do empregado a ser removido.
     * @throws Exception se o empregado não for encontrado ou o sistema estiver encerrado.
     */
    public void removerEmpregado(String id) throws Exception {
        checkSistemaEncerrado();
        Command comando = new RemoverEmpregadoCommand(this.sistema, id);
        this.invoker.executeCommand(comando);
    }

    /**
     * Lança um cartão de ponto para um empregado horista.
     *
     * @param id O ID do empregado.
     * @param data A data do cartão de ponto.
     * @param horas O número de horas trabalhadas.
     * @throws Exception se o empregado não for horista ou os dados forem inválidos.
     */
    public void lancaCartao(String id, String data, String horas) throws Exception {
        checkSistemaEncerrado();
        Command comando = new LancarCartaoCommand(this.sistema, id, data, horas);
        this.invoker.executeCommand(comando);
    }

    /**
     * Lança um resultado de venda para um empregado comissionado.
     *
     * @param id O ID do empregado.
     * @param data A data da venda.
     * @param valor O valor da venda.
     * @throws Exception se o empregado não for comissionado ou os dados forem inválidos.
     */
    public void lancaVenda(String id, String data, String valor) throws Exception {
        checkSistemaEncerrado();
        Command comando = new LancarVendaCommand(this.sistema, id, data, valor);
        this.invoker.executeCommand(comando);
    }

    /**
     * Lança uma taxa de serviço para um membro do sindicato.
     *
     * @param membro O ID do membro no sindicato.
     * @param data A data da taxa de serviço.
     * @param valor O valor da taxa.
     * @throws Exception se o membro não for encontrado ou os dados forem inválidos.
     */
    public void lancaTaxaServico(String membro, String data, String valor) throws Exception {
        checkSistemaEncerrado();
        Command comando = new LancarTaxaServicoCommand(this.sistema, membro, data, valor);
        this.invoker.executeCommand(comando);
    }

    /**
     * Altera um atributo de um empregado (versão para 3 parâmetros).
     * Usado para alterações simples como nome, endereço, ou remover do sindicato.
     *
     * @param emp O ID do empregado.
     * @param atributo O atributo a ser alterado.
     * @param valor O novo valor para o atributo.
     * @throws Exception se a alteração for inválida.
     */
    public void alteraEmpregado(String emp, String atributo, String valor) throws Exception {
        checkSistemaEncerrado();
        Command comando = new AlterarEmpregadoCommand(this.sistema, emp, atributo, valor, null, null);
        this.invoker.executeCommand(comando);
    }

    /**
     * Altera o tipo de um empregado (versão para 4 parâmetros).
     *
     * @param emp O ID do empregado.
     * @param atributo Deve ser "tipo".
     * @param valor O novo tipo do empregado (ex: "horista").
     * @param extra O novo salário ou comissão.
     * @throws Exception se a alteração for inválida.
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String extra) throws Exception {
        checkSistemaEncerrado();
        Command comando = new AlterarEmpregadoCommand(this.sistema, emp, atributo, valor, extra, null);
        this.invoker.executeCommand(comando);
    }

    /**
     * Adiciona um empregado ao sindicato (versão para 5 parâmetros).
     *
     * @param emp O ID do empregado.
     * @param atributo Deve ser "sindicalizado".
     * @param valor Deve ser "true".
     * @param idSindicato O novo ID do empregado no sindicato.
     * @param taxaSindical A nova taxa sindical diária.
     * @throws Exception se a alteração for inválida.
     */
    public void alteraEmpregado(String emp, String atributo, String valor, String idSindicato, String taxaSindical) throws Exception {
        checkSistemaEncerrado();
        Command comando = new AlterarEmpregadoCommand(this.sistema, emp, atributo, valor, idSindicato, taxaSindical);
        this.invoker.executeCommand(comando);
    }

    /**
     * Altera o método de pagamento para banco (versão para 6 parâmetros).
     *
     * @param emp O ID do empregado.
     * @param atributo Deve ser "metodoPagamento".
     * @param valor1 Deve ser "banco".
     * @param banco O nome do banco.
     * @param agencia O número da agência.
     * @param contaCorrente O número da conta corrente.
     * @throws Exception se a alteração for inválida.
     */
    public void alteraEmpregado(String emp, String atributo, String valor1, String banco, String agencia, String contaCorrente) throws Exception {
        checkSistemaEncerrado();
        Command comando = new AlterarMetodoPagamentoBancoCommand(this.sistema, emp, banco, agencia, contaCorrente);
        this.invoker.executeCommand(comando);
    }

    /**
     * Desfaz a última ação executada.
     * @throws Exception se não houver comando para desfazer ou o sistema estiver encerrado.
     */
    public void undo() throws Exception {
        checkSistemaEncerrado();
        this.invoker.undoLastCommand();
    }

    /**
     * Refaz a última ação desfeita.
     * @throws Exception se não houver comando para refazer ou o sistema estiver encerrado.
     */
    public void redo() throws Exception {
        checkSistemaEncerrado();
        this.invoker.redoLastCommand();
    }

    /**
     * Roda a folha de pagamento para uma data específica e gera um arquivo de saída.
     *
     * @param data A data para a qual a folha deve ser rodada.
     * @param saida O caminho do arquivo de saída a ser gerado.
     * @throws Exception se os dados forem inválidos ou ocorrer um erro de arquivo.
     */
    public void rodaFolha(String data, String saida) throws Exception {
        checkSistemaEncerrado();
        Command comando = new RodaFolhaCommand(this.sistema, data, saida);
        this.invoker.executeCommand(comando);
    }

    // --- Métodos de Consulta (Getters) ---

    public String getAtributoEmpregado(String id, String atributo) throws Exception {
        return this.sistema.getAtributoEmpregado(id, atributo);
    }

    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        return this.sistema.getEmpregadoPorNome(nome, indice);
    }

    public String getHorasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getHorasTrabalhadas(id, dataInicial, dataFinal);
    }

    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getHorasNormaisTrabalhadas(id, dataInicial, dataFinal);
    }

    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getHorasExtrasTrabalhadas(id, dataInicial, dataFinal);
    }

    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getVendasRealizadas(id, dataInicial, dataFinal);
    }

    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getTaxasServico(emp, dataInicial, dataFinal);
    }

    public String totalFolha(String data) throws Exception {
        return this.sistema.totalFolha(data);
    }

    public String getNumeroDeEmpregados() {
        return String.valueOf(this.sistema.getNumeroDeEmpregados());
    }
}