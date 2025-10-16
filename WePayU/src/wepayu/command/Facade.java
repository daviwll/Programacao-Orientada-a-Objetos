package wepayu.command;

import wepayu.exceptions.NaoPodeComandosAposEncerrarSistemaException;
import wepayu.services.Sistema;

/**
 * Facade do subsistema de folha de pagamento (WePayU).
 *
 * <p>
 * Fornece uma interface única e simplificada para as operações de alto nível
 * do sistema, encapsulando a orquestração entre comandos ({@link Command}),
 * o invocador ({@link Invoker}) e o núcleo de negócios ({@link Sistema}).
 * Esta é a “porta de entrada” usada pelos testes: recebe solicitações,
 * cria e executa os comandos adequados e delega a lógica ao {@link Sistema}.
 * </p>
 *
 * <h3>Responsabilidades</h3>
 * <ul>
 *   <li>Validar o estado de encerramento do sistema antes de executar ações;</li>
 *   <li>Criar comandos e entregá-los ao {@link Invoker} para execução;</li>
 *   <li>Expor operações de consulta que delegam diretamente ao {@link Sistema};</li>
 *   <li>Controlar undo/redo via {@link Invoker}.</li>
 * </ul>
 *
 * <h3>Ciclo de vida e estado</h3>
 * <ul>
 *   <li>O estado de negócio é mantido em um {@code Sistema} estático
 *       ({@code sistemaGlobal}) para persistir entre execuções de teste;</li>
 *   <li>Cada instância da {@code Facade} cria seu próprio {@link Invoker}
 *       (histórico de comandos por instância);</li>
 *   <li>O “encerramento” do sistema é controlado por um flag estático
 *       ({@code sistemaEncerrado}), bloqueando novas ações até ser reativado.</li>
 * </ul>
 *
 * <h3>Erros e exceções</h3>
 * <p>
 * Métodos de ação lançam {@link Exception} quando o sistema está encerrado
 * ou quando os dados de entrada são inválidos (as validações específicas
 * são feitas no {@link Sistema} e nos comandos).
 * </p>
 *
 * @see Sistema
 * @see Invoker
 * @see Command
 */
public class Facade {
    private Sistema sistema;
    private Invoker invoker;
    private static Sistema sistemaGlobal;
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
        }
        this.sistema = sistemaGlobal;
        this.invoker = new Invoker();
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
            throw new NaoPodeComandosAposEncerrarSistemaException();
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
    /**
     * Cria uma nova agenda de pagamentos disponível para uso nas operações do sistema.
     * <p>
     * Exemplos aceitos:
     * <ul>
     *   <li>{@code "semanal 5"} – toda sexta;</li>
     *   <li>{@code "semanal 2 5"} – a cada 2 semanas na sexta;</li>
     *   <li>{@code "mensal $"} – último dia útil do mês;</li>
     *   <li>{@code "mensal 12"} – dia 12 de todo mês.</li>
     * </ul>
     *
     * @param descricao descrição textual da agenda (formato válido conforme regras do {@link Sistema})
     * @throws Exception se o sistema estiver encerrado, a descrição for inválida
     *                   ou a agenda já existir.
     */
    public void criarAgendaDePagamentos(String descricao) throws Exception {
        checkSistemaEncerrado();
        this.sistema.criarAgendaDePagamentos(descricao);
    }

    // --- Métodos de Consulta (Getters) ---

    /**
     * Retorna um atributo textual de um empregado.
     * <p>
     * Atributos aceitos incluem, entre outros: {@code nome}, {@code endereco}, {@code tipo},
     * {@code salario}, {@code comissao}, {@code metodoPagamento}, {@code banco}, {@code agencia},
     * {@code contaCorrente}, {@code sindicalizado}, {@code idSindicato}, {@code taxaSindical},
     * {@code agendaPagamento}.
     *
     * @param id        ID do empregado
     * @param atributo  nome do atributo desejado
     * @return valor do atributo como {@code String}
     * @throws Exception se o sistema estiver encerrado, o empregado/atributo não existir
     *                   ou pré-condições (ex.: receber em banco / ser sindicalizado) não forem atendidas.
     */
    public String getAtributoEmpregado(String id, String atributo) throws Exception {
        return this.sistema.getAtributoEmpregado(id, atributo);
    }

    /**
     * Busca empregados pelo nome (contém) e retorna o ID do resultado na posição informada.
     *
     * @param nome   termo de busca (contém)
     * @param indice posição 1-based do resultado desejado
     * @return ID do empregado na posição solicitada
     * @throws Exception se o sistema estiver encerrado ou não houver resultados suficientes.
     */
    public String getEmpregadoPorNome(String nome, int indice) throws Exception {
        return this.sistema.getEmpregadoPorNome(nome, indice);
    }

    /**
     * Obtém o total de horas trabalhadas por um horista no intervalo informado (inclusive).
     *
     * @param id           ID do empregado horista
     * @param dataInicial  data inicial no formato {@code d/M/uuuu}
     * @param dataFinal    data final no formato {@code d/M/uuuu}
     * @return total de horas (formatado: inteiro sem casas ou com vírgula até 2 casas)
     * @throws Exception se o sistema estiver encerrado, o empregado não for horista,
     *                   datas forem inválidas ou {@code dataInicial} > {@code dataFinal}.
     */
    public String getHorasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getHorasTrabalhadas(id, dataInicial, dataFinal);
    }

    /**
     * Obtém as horas normais (até 8h/dia) de um horista no intervalo informado (inclusive).
     *
     * @param id           ID do empregado horista
     * @param dataInicial  data inicial no formato {@code d/M/uuuu}
     * @param dataFinal    data final no formato {@code d/M/uuuu}
     * @return total de horas normais (formatação textual conforme regras do sistema)
     * @throws Exception se o sistema estiver encerrado, o empregado não for horista,
     *                   datas forem inválidas ou {@code dataInicial} > {@code dataFinal}.
     */
    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getHorasNormaisTrabalhadas(id, dataInicial, dataFinal);
    }

    /**
     * Obtém as horas extras (acima de 8h/dia) de um horista no intervalo informado (inclusive).
     *
     * @param id           ID do empregado horista
     * @param dataInicial  data inicial no formato {@code d/M/uuuu}
     * @param dataFinal    data final no formato {@code d/M/uuuu}
     * @return total de horas extras (formatação textual conforme regras do sistema)
     * @throws Exception se o sistema estiver encerrado, o empregado não for horista,
     *                   datas forem inválidas ou {@code dataInicial} > {@code dataFinal}.
     */
    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getHorasExtrasTrabalhadas(id, dataInicial, dataFinal);
    }

    /**
     * Obtém o total de vendas realizadas por um empregado comissionado no período.
     *
     * @param id           ID do empregado comissionado
     * @param dataInicial  data inicial no formato {@code d/M/uuuu}
     * @param dataFinal    data final no formato {@code d/M/uuuu}
     * @return soma das vendas no período, formatada com vírgula e 2 casas
     * @throws Exception se o sistema estiver encerrado, o empregado não for comissionado,
     *                   datas forem inválidas ou {@code dataInicial} > {@code dataFinal}.
     */
    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getVendasRealizadas(id, dataInicial, dataFinal);
    }

    /**
     * Obtém a soma das taxas de serviço do sindicato de um empregado no período informado.
     *
     * @param emp          ID do empregado (deve estar sindicalizado)
     * @param dataInicial  data inicial no formato {@code d/M/uuuu}
     * @param dataFinal    data final no formato {@code d/M/uuuu}
     * @return total das taxas de serviço, formatado com vírgula e 2 casas
     * @throws Exception se o sistema estiver encerrado, o empregado não for sindicalizado,
     *                   datas forem inválidas ou {@code dataInicial} > {@code dataFinal}.
     */
    public String getTaxasServico(String emp, String dataInicial, String dataFinal) throws Exception {
        return this.sistema.getTaxasServico(emp, dataInicial, dataFinal);
    }

    /**
     * Calcula o total bruto da folha de pagamento para a data informada.
     * <p>
     * Respeita as regras de pagamento por tipo (horista/sextas; comissionado/quinzenal;
     * assalariado/último dia útil) e agendas personalizadas cadastradas.
     *
     * @param data data de referência no formato {@code d/M/uuuu}
     * @return total da folha formatado com vírgula e 2 casas decimais
     * @throws Exception se o sistema estiver encerrado ou a data for inválida.
     */
    public String totalFolha(String data) throws Exception {
        return this.sistema.totalFolha(data);
    }

    /**
     * Retorna a quantidade atual de empregados cadastrados no sistema.
     *
     * @return número de empregados como {@code String} (compatível com a suíte de testes).
     */
    public String getNumeroDeEmpregados() {
        return String.valueOf(this.sistema.getNumeroDeEmpregados());
    }
}