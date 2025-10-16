package wepayu.services;
import wepayu.command.SistemaMemento;
import wepayu.models.*;
import wepayu.exceptions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
/**
 * Núcleo de folha de pagamento (Sistema).
 *
 * <p>Responsável por cadastrar e manter empregados, lançar cartões de ponto e vendas,
 * gerenciar filiação sindical (incluindo taxas/serviços) e calcular/gerar a folha de
 * pagamento em diferentes agendas (padrão e customizadas).</p>
 *
 * <h3>Escopo e responsabilidades</h3>
 * <ul>
 *   <li>CRUD de empregados (horista, assalariado, comissionado) e mudanças de tipo.</li>
 *   <li>Lançamento de cartões de ponto (horistas) e resultados de venda (comissionados).</li>
 *   <li>Filiação sindical (ID único, taxa diária) e registro de taxas de serviço.</li>
 *   <li>Cálculo de salários bruto/líquido por período e geração do arquivo de folha.</li>
 *   <li>Histórico com <em>undo/redo</em> via padrão Memento para operações de estado.</li>
 *   <li>Agendas de pagamento: padrões e criação de agendas customizadas verificadas por regras.</li>
 * </ul>
 *
 * <h3>Regras de pagamento (agenda padrão)</h3>
 * <ul>
 *   <li><b>Horista</b>: pago às sextas, cobrindo a semana de sáb a sex (horas extras 1,5× acima de 8h/dia).</li>
 *   <li><b>Assalariado</b>: pago no último dia útil do mês.</li>
 *   <li><b>Comissionado</b>: pago de forma quinzenal (sextas alternadas), base proporcional + comissão.</li>
 * </ul>
 *
 * <h3>Agendas customizadas</h3>
 * <ul>
 *   <li><code>mensal $</code>: último dia útil do mês.</li>
 *   <li><code>mensal N</code>: dia fixo (1..28) do mês.</li>
 *   <li><code>semanal [N] D</code>: a cada N semanas no dia D (1=Seg..7=Dom), ancorado na contratação.</li>
 * </ul>
 *
 * <h3>Arredondamentos</h3>
 * <ul>
 *   <li>Valores de exibição: 2 casas decimais.</li>
 *   <li>Base proporcional (ex.: mensal→semanal/quinzenal): regra típica FLOOR(2) quando indicado.</li>
 *   <li>Comissão sobre vendas: geralmente FLOOR(2) antes de compor o total.</li>
 *   <li>Totais e exibição: HALF_UP(2), salvo quando especificado de outra forma nos cálculos.</li>
 * </ul>
 *
 * <h3>Formatação e idioma</h3>
 * <ul>
 *   <li>Moedas: vírgula como separador decimal (pt-BR), 2 casas.</li>
 *   <li>Datas de entrada: formato estrito <code>d/M/uuuu</code>.</li>
 * </ul>
 *
 * <h3>Validações e exceções</h3>
 * <ul>
 *   <li>Campos obrigatórios, numéricos e faixas (ex.: salários/horas/taxas não negativos).</li>
 *   <li>Consistência de tipos (ex.: operações de horista/comissionado apenas para o tipo correto).</li>
 *   <li>IDs sindicais únicos entre empregados sindicalizados.</li>
 *   <li>Erros sinalizados por exceções específicas do pacote <code>wepayu.exceptions</code>.</li>
 * </ul>
 *
 * <h3>Estado e histórico</h3>
 * <ul>
 *   <li>Snapshots com {@link wepayu.command.SistemaMemento} para suportar <code>undo()</code>/<code>redo()</code>.</li>
 *   <li><code>checkpoint()</code> é chamado no início de operações que alteram estado.</li>
 * </ul>
 *
 * @see wepayu.models.Empregado
 * @see wepayu.models.Horista
 * @see wepayu.models.Assalariado
 * @see wepayu.models.Comissionado
 * @see wepayu.models.MembroSindicato
 * @see wepayu.command.SistemaMemento
 */

public class Sistema
{
    private ArrayList<Empregado> empregados;
    private int id = 0;
    private boolean encerrado = false;
    private final java.util.ArrayDeque<SistemaMemento> undoStack = new java.util.ArrayDeque<>();
    private final java.util.ArrayDeque<SistemaMemento> redoStack = new java.util.ArrayDeque<>();
    private final java.util.Set<String> agendasDisponiveis =
            new java.util.LinkedHashSet<>(java.util.Arrays.asList(
                    "semanal 5", "mensal $", "semanal 2 5"
            ));

    /**
     * Cria uma instância vazia do sistema, inicializando a lista de empregados.
     */
    public Sistema()
    {
        empregados = new ArrayList<>();
    }

    /**
     * Obtém um empregado pelo identificador numérico em {@code String}.
     *
     * @param id identificador do empregado (numérico em texto, não nulo/nem vazio)
     * @return a instância de {@link Empregado} correspondente
     * @throws Exception se {@code id} for nulo/vazio, não numérico, ou não houver empregado correspondente
     */
    public Empregado getEmpregado(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new IdentificacaoEmpregadoNulaException();
        }
        int idInt;
        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new EmpregadoNaoExisteException();
        }
        for (Empregado empregado : this.empregados) {
            if (Integer.parseInt(empregado.getId()) == idInt) {
                return empregado;
            }
        }
        throw new EmpregadoNaoExisteException();
    }

    /**
     * Cria um empregado do tipo horista ou assalariado.
     *
     * @param name nome do empregado (não nulo/nem vazio)
     * @param endereco endereço do empregado (não nulo/nem vazio)
     * @param tipo tipo do empregado: {@code "horista"} ou {@code "assalariado"}
     * @param salario valor de salário (texto; aceita vírgula ou ponto; não negativo)
     * @return o ID gerado do novo empregado
     * @throws Exception se tipo for inválido/diferente de horista/assalariado; se nome/endereço/salário estiverem nulos/vazios;
     *                   se salário não for numérico ou for negativo
     */
    public String criarEmpregado(String name, String endereco, String tipo, String salario) throws Exception
    {
        checkpoint();
        if (tipo.equals("comissionado"))
        {
            throw new TipoNaoAplicavelException();
        }
        if (name == null || name.trim().isEmpty())
        {
            throw new NomeNuloException();
        }
        if (endereco == null || endereco.trim().isEmpty())
        {
            throw new EnderecoNuloException();
        }
        if (salario == null || salario.trim().isEmpty())
        {
            throw new SalarioNuloException();
        }
        salario = salario.replace(",", ".");
        double salarioDouble;
        try
        {
            salarioDouble = Double.parseDouble(salario);
        }
        catch (NumberFormatException e)
        {
            throw new SalarioDeveSerNumericoException();
        }
        if (salarioDouble < 0)
        {
            throw new SalarioNaoNegativoException();
        }

        if (tipo.equals("horista"))
        {
            this.id += 1;
            Horista novoEmpregado = new Horista(name, endereco, String.valueOf(this.id), salarioDouble, tipo);
            novoEmpregado.setAgendaPagamento("semanal 5");
            empregados.add(novoEmpregado);
            aplicarAgendaDefaultSeVazia(novoEmpregado);
            return novoEmpregado.getId();
        }
        else if (tipo.equals("assalariado"))
        {
            this.id += 1;
            Assalariado novoEmpregado = new Assalariado(name, endereco, String.valueOf(this.id), salarioDouble, tipo);
            novoEmpregado.setAgendaPagamento("mensal $");
            empregados.add(novoEmpregado);
            aplicarAgendaDefaultSeVazia(novoEmpregado);
            return novoEmpregado.getId();
        }
        throw new TipoInvalidoException();
    }

    /**
     * Cria um empregado do tipo comissionado.
     *
     * @param nome nome do empregado (não nulo/nem vazio)
     * @param endereco endereço do empregado (não nulo/nem vazio)
     * @param tipo deve ser exatamente {@code "comissionado"}
     * @param salario salário base (mensal, texto; aceita vírgula/ponto; não negativo)
     * @param comissao taxa de comissão (texto; aceita vírgula/ponto; não negativa)
     * @return o ID gerado do novo empregado
     * @throws Exception se tipo não for {@code "comissionado"}; se nome/endereço/salário/comissão nulos ou vazios;
     *                   se salário ou comissão não forem numéricos ou forem negativos
     */
    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception
    {
        checkpoint();
        if (!tipo.equals("comissionado"))
        {
            throw new TipoNaoAplicavelException();
        }
        if (nome == null || nome.trim().isEmpty())
        {
            throw new NomeNuloException();
        }
        if (endereco == null || endereco.trim().isEmpty())
        {
            throw new EnderecoNuloException();
        }
        if (comissao == null)
        {
            throw new TipoNaoAplicavelException();
        }
        if (comissao.equals(""))
        {
            throw new ComissaoNulaException();
        }
        if (salario == null || salario.trim().isEmpty())
        {
            throw new SalarioNuloException();
        }

        salario = salario.replace(",", ".");
        double salarioDouble;
        try
        {
            salarioDouble = Double.parseDouble(salario);
        }
        catch (NumberFormatException e)
        {
            throw new SalarioDeveSerNumericoException();
        }
        if (salarioDouble < 0)
        {
            throw new SalarioNaoNegativoException();
        }

        comissao = comissao.replace(",", ".");
        double comissaoDouble;
        try
        {
            comissaoDouble = Double.parseDouble(comissao);
        }
        catch (NumberFormatException e)
        {
            throw new ComissaoDeveSerNumericaException();
        }
        if (comissaoDouble < 0)
        {
            throw new ComissaoNaoNegativaException();
        }

        this.id += 1;
        Comissionado novoEmpregado = new Comissionado(nome, endereco, String.valueOf(this.id), salarioDouble, comissaoDouble, tipo);
        novoEmpregado.setAgendaPagamento("semanal 2 5");
        empregados.add(novoEmpregado);
        aplicarAgendaDefaultSeVazia(novoEmpregado);
        return novoEmpregado.getId();
    }
    /**
     * Remove um empregado existente.
     *
     * @param id identificador do empregado em texto
     * @throws Exception se o empregado não existir ou {@code id} for inválido
     */
    public void removerEmpregado(String id) throws Exception
    {
        Empregado empregado = getEmpregado(id);
        empregados.remove(empregado);
    }

    /**
     * Lança um cartão de ponto para um empregado horista.
     *
     * @param id identificador do empregado horista
     * @param data data no formato {@code d/M/uuuu} (ex.: "3/9/2025")
     * @param horas quantidade de horas (texto; aceita vírgula/ponto; deve ser positiva)
     * @return o {@link CartaoDePonto} criado
     * @throws Exception se o empregado não for horista; se a data for inválida;
     *                   se horas não forem numéricas ou não positivas
     */
    public CartaoDePonto lancaCartao(String id, String data, String horas) throws Exception {
        checkpoint();
        Empregado empregado = getEmpregado(id);
        if (!(empregado instanceof Horista)) {
            throw new EmpregadoNaoEhHoristaException();
        }

        LocalDate dataLanc;
        try {
            dataLanc = parseDateBR(data);
        } catch (Exception e) {
            throw new DataInvalidaException();
        }

        double horasVal;
        try {
            horasVal = Double.parseDouble(horas.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new HorasDevemSerPositivasException();
        }

        if (horasVal <= 0) {
            throw new HorasDevemSerPositivasException();
        }

        CartaoDePonto novoCartao = new CartaoDePonto(dataLanc, horasVal);
        ((Horista) empregado).addCartaoDePonto(novoCartao);
        return novoCartao;
    }

    /**
     * Lança um resultado de venda para um empregado comissionado.
     *
     * @param id identificador do empregado comissionado
     * @param data data no formato {@code d/M/uuuu}
     * @param valor valor da venda (texto; aceita vírgula/ponto; deve ser positivo)
     * @return o {@link ResultadoDeVenda} criado
     * @throws Exception se o empregado não for comissionado; se a data for inválida;
     *                   se o valor não for numérico ou não positivo
     */
    public ResultadoDeVenda lancaVenda(String id, String data, String valor) throws Exception {
        checkpoint();
        Empregado empregado = getEmpregado(id);
        if (!(empregado instanceof Comissionado)) {
            throw new EmpregadoNaoEhComissionadoException();
        }

        LocalDate dataLanc;
        try {
            dataLanc = parseDateBR(data);
        } catch (Exception e) {
            throw new DataInvalidaException();
        }

        double valorNum;
        try {
            valorNum = Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValorDeveSerPositivoException();
        }
        if (valorNum <= 0) {
            throw new ValorDeveSerPositivoException();
        }

        ResultadoDeVenda venda = new ResultadoDeVenda(dataLanc, valorNum);
        ((Comissionado) empregado).addVenda(venda);
        return venda;
    }

    /**
     * Atualiza atributos de um empregado (nome, endereço, tipo, salário, comissão, sindicato, método de pagamento).
     *
     * <p>Regras principais:</p>
     * <ul>
     *   <li><b>nome</b>/<b>endereco</b>: {@code valor1} não pode ser nulo/vazio.</li>
     *   <li><b>tipo</b>: {@code valor1} ∈ {horista, assalariado, comissionado}. Se {@code valor2} (salário/comissão) for nulo,
     *       usa o valor atual do empregado como padrão.</li>
     *   <li><b>salario</b>: para horista/assalariado; {@code valor1} numérico e não negativo.</li>
     *   <li><b>comissao</b>: somente comissionado; {@code valor1} numérico e não negativo.</li>
     *   <li><b>sindicalizado</b>: {@code valor1} ∈ {true,false}. Se {@code true}, exige {@code valor2}=id do sindicato (único)
     *       e {@code valor3}=taxa sindical não negativa.</li>
     *   <li><b>metodoPagamento</b>: {@code valor1} ∈ {emMaos, banco, correios}; para banco, use o método dedicado.</li>
     * </ul>
     *
     * @param id identificador do empregado
     * @param atributo nome do atributo a alterar
     * @param valor1 primeiro valor (significado depende do atributo)
     * @param valor2 segundo valor (opcional; p.ex. salário/comissão/ID sindicato)
     * @param valor3 terceiro valor (opcional; p.ex. taxa sindical)
     * @throws Exception se o empregado não existir; se o atributo for inválido; se os valores exigidos forem nulos/vazios;
     *                   se numéricos inválidos/negativos; se houver conflito de ID sindical
     */
    public void alteraEmpregado(String id, String atributo, String valor1, String valor2, String valor3) throws Exception {
        checkpoint();
        Empregado empregado = getEmpregado(id);

        if (atributo.equalsIgnoreCase("nome")) {
            if (valor1 == null || valor1.trim().isEmpty()) throw new NomeNuloException();
            empregado.setName(valor1);

        } else if (atributo.equalsIgnoreCase("endereco")) {
            if (valor1 == null || valor1.trim().isEmpty()) throw new EnderecoNuloException();
            empregado.setEndereco(valor1);

        } else if (atributo.equalsIgnoreCase("tipo")) {
            String novoSalarioOuComissao = valor2;
            if (novoSalarioOuComissao == null) {
                double salarioAtual;
                if (empregado instanceof Horista) {
                    salarioAtual = ((Horista) empregado).getSalarioHora();
                } else {
                    salarioAtual = ((Assalariado) empregado).getSalarioMensal();
                }
                novoSalarioOuComissao = String.format(java.util.Locale.US, "%.2f", salarioAtual).replace('.', ',');
                Empregado atualizado = getEmpregado(id);
                aplicarAgendaDefaultSeVazia(atualizado);
            }

            if ("horista".equalsIgnoreCase(valor1)) {
                alteraEmpregadoTipoParaHorista(id, novoSalarioOuComissao);
            } else if ("comissionado".equalsIgnoreCase(valor1)) {
                alteraEmpregadoTipoParaComissionado(id, novoSalarioOuComissao);
            } else if ("assalariado".equalsIgnoreCase(valor1)) {
                alteraEmpregadoTipoParaAssalariado(id, novoSalarioOuComissao);
            } else {
                throw new TipoInvalidoException();
            }
        } else if (atributo.equalsIgnoreCase("salario")) {
            if (valor1 == null || valor1.trim().isEmpty()) throw new SalarioNuloException();
            String salarioCorrigido = valor1.replace(",", ".");
            double novoSalario;
            try {
                novoSalario = Double.parseDouble(salarioCorrigido);
            } catch (NumberFormatException e) {
                throw new SalarioDeveSerNumericoException();
            }
            if (novoSalario < 0) throw new SalarioNaoNegativoException();

            if (empregado instanceof Horista) {
                ((Horista) empregado).setSalarioHora(novoSalario);
            } else if (empregado instanceof Assalariado) {
                ((Assalariado) empregado).setSalarioMensal(novoSalario);
            }

        } else if (atributo.equalsIgnoreCase("comissao")) {
            if (!(empregado instanceof Comissionado)) throw new EmpregadoNaoEhComissionadoException();
            if (valor1 == null || valor1.trim().isEmpty()) throw new ComissaoNulaException();
            String comissaoCorrigida = valor1.replace(",", ".");
            double novaComissao;
            try {
                novaComissao = Double.parseDouble(comissaoCorrigida);
            } catch (NumberFormatException e) {
                throw new ComissaoDeveSerNumericaException();
            }
            if (novaComissao < 0) throw new ComissaoNaoNegativaException();
            ((Comissionado) empregado).setComissao(novaComissao);

        } else if (atributo.equalsIgnoreCase("sindicalizado")) {
            if (!"true".equalsIgnoreCase(valor1) && !"false".equalsIgnoreCase(valor1)) {
                throw new ValorDeveSerTrueOuFalseException();
            }
            boolean isSindicalizado = Boolean.parseBoolean(valor1);

            if (isSindicalizado) {
                if (valor2 == null || valor2.isBlank()) throw new IdentificacaoSindicatoNulaException();
                if (valor3 == null || valor3.isBlank()) throw new TaxaSindicalNulaException();

                String taxaCorrigida = valor3.replace(",", ".");
                double taxaSindical;
                try {
                    taxaSindical = Double.parseDouble(taxaCorrigida);
                } catch (NumberFormatException e) {
                    throw new TaxaSindicalDeveSerNumericaException();
                }
                if (taxaSindical < 0) throw new TaxaSindicalNaoNegativaException();

                for (Empregado outro : this.empregados) {
                    if (outro != empregado && outro.getSindicato() != null &&
                            valor2.equals(outro.getSindicato().getIdMembro())) {
                        throw new OutroEmpregadoComMesmoIdSindicatoException();
                    }
                }
                MembroSindicato novoMembro = new MembroSindicato(valor2, taxaSindical);
                empregado.setSindicato(novoMembro);
            } else {
                empregado.setSindicato(null);
            }

        } else if (atributo.equalsIgnoreCase("metodoPagamento")) {
            if (!"emMaos".equalsIgnoreCase(valor1) && !"banco".equalsIgnoreCase(valor1) && !"correios".equalsIgnoreCase(valor1)) {
                throw new MetodoPagamentoInvalidoException();
            }
            alteraEmpregadoMetodoPagamentoSimples(id, valor1);

        } else if (atributo.equalsIgnoreCase("agendaPagamento")){
            if (valor1 == null || !agendasDisponiveis.contains(valor1)) {
                throw new AgendaDePagamentoNaoEstaDisponivelException();
            }
            empregado.setAgendaPagamento(valor1);
            return;
        }

        else {
            throw new AtributoNaoExisteException();
        }
    }

    /**
     * Retorna um atributo específico do empregado em formato de {@code String}.
     *
     * <p>Atributos suportados:</p>
     * <ul>
     *   <li>{@code nome}, {@code endereco}, {@code tipo}</li>
     *   <li>{@code salario} (formata com 2 casas, vírgula decimal)</li>
     *   <li>{@code comissao} (apenas para comissionado; 2 casas, vírgula decimal)</li>
     *   <li>{@code metodoPagamento} (padrão: {@code emMaos} se nulo)</li>
     *   <li>{@code banco}, {@code agencia}, {@code contaCorrente} (só se método = {@code banco})</li>
     *   <li>{@code sindicalizado} (true/false)</li>
     *   <li>{@code idSindicato}, {@code taxaSindical} (apenas se sindicalizado)</li>
     * </ul>
     *
     * @param id identificador do empregado
     * @param atributo nome do atributo
     * @return valor textual do atributo
     * @throws Exception se o atributo não existir; se exigir pré-condição (ex.: sindicato ou banco) e ela não for atendida
     */
    public String getAtributoEmpregado(String id, String atributo) throws Exception
    {
        Empregado empregado = getEmpregado(id);

        if (atributo.equalsIgnoreCase("nome"))
        {
            return empregado.getName();
        }
        else if (atributo.equalsIgnoreCase("endereco"))
        {
            return empregado.getEndereco();
        }
        else if (atributo.equalsIgnoreCase("tipo"))
        {
            return empregado.getTipo();
        }
        else if (atributo.equalsIgnoreCase("salario")) {
            double salario;
            if (empregado instanceof Horista) {
                salario = ((Horista) empregado).getSalarioHora();
            } else if (empregado instanceof Comissionado) {
                salario = ((Comissionado) empregado).getSalarioMensal();
            } else {
                salario = ((Assalariado) empregado).getSalarioMensal();
            }
            return String.format("%.2f", salario).replace('.', ',');
        }
        else if (atributo.equalsIgnoreCase("comissao"))
        {
            if (empregado instanceof Comissionado)
            {
                double comissao = ((Comissionado) empregado).getComissao();
                return String.format("%.2f", comissao).replace('.', ',');
            }
            throw new EmpregadoNaoEhComissionadoException();
        }
        else if (atributo.equalsIgnoreCase("metodoPagamento"))
        {
            return empregado.getMetodoPagamento() == null ? "emMaos" : empregado.getMetodoPagamento();
        }
        else if (atributo.equalsIgnoreCase("banco"))
        {
            if (!"banco".equals(empregado.getMetodoPagamento()))
            {
                throw new EmpregadoNaoRecebeEmBancoException();
            }
            return empregado.getBanco();
        }
        else if (atributo.equalsIgnoreCase("agencia"))
        {
            if (!"banco".equals(empregado.getMetodoPagamento()))
            {
                throw new EmpregadoNaoRecebeEmBancoException();
            }
            return empregado.getAgencia();
        }
        else if (atributo.equalsIgnoreCase("contaCorrente"))
        {
            if (!"banco".equals(empregado.getMetodoPagamento()))
            {
                throw new EmpregadoNaoRecebeEmBancoException();
            }
            return empregado.getContaCorrente();
        }
        else if (atributo.equalsIgnoreCase("sindicalizado"))
        {
            return String.valueOf(empregado.isSindicalizado());
        }
        else if (atributo.equalsIgnoreCase("idSindicato"))
        {
            if (empregado.getSindicato() == null)
            {
                throw new EmpregadoNaoEhSindicalizadoException();
            }
            return empregado.getSindicato().getIdMembro();
        }
        else if (atributo.equalsIgnoreCase("taxaSindical"))
        {
            if (empregado.getSindicato() == null)
            {
                throw new EmpregadoNaoEhSindicalizadoException();
            }
            double taxa = empregado.getSindicato().getTaxaSindical();
            return String.format(java.util.Locale.US, "%.2f", taxa).replace('.', ',');
        }
        else if (atributo.equalsIgnoreCase("agendaPagamento")) {
            return empregado.getAgendaPagamento();
        }

        throw new AtributoNaoExisteException();
    }
    /**
     * Busca empregados cujo nome contém o termo informado e retorna o ID do resultado
     * na posição solicitada.
     *
     * @param nome termo a procurar (contém)
     * @param indice índice 1-based do resultado desejado
     * @return ID do empregado correspondente
     * @throws Exception se não houver resultados suficientes para o índice informado
     */
    public String getEmpregadoPorNome(String nome, int indice) throws Exception
    {
        ArrayList<Empregado> encontrados = new ArrayList<>();

        for (Empregado empregado : this.empregados)
        {
            if (empregado.getName().contains(nome))
            {
                encontrados.add(empregado);
            }
        }

        if (indice > 0 && indice <= encontrados.size())
        {
            return encontrados.get(indice - 1).getId();
        }

        throw new NaoHaEmpregadoComEsseNomeException();
    }
    /**
     * Converte uma string de data no formato {@code d/M/uuuu} (estrito) em {@link LocalDate}.
     *
     * @param data data no formato brasileiro curto (ex.: {@code "1/12/2025"})
     * @return a data como {@link LocalDate}
     * @throws java.time.format.DateTimeParseException se a string não obedecer ao formato estrito
     */
    private java.time.LocalDate parseDateBR(String data)
    {
        java.time.format.DateTimeFormatter fmt =
                java.time.format.DateTimeFormatter.ofPattern("d/M/uuuu")
                        .withResolverStyle(java.time.format.ResolverStyle.STRICT);
        return java.time.LocalDate.parse(data, fmt);
    }
    /**
     * Verifica se {@code d} está dentro do intervalo inclusivo [{@code ini}, {@code fim}].
     *
     * @param d data a testar
     * @param ini data inicial (inclusiva)
     * @param fim data final (inclusiva)
     * @return {@code true} se {@code d} ∈ [ini, fim]; caso contrário, {@code false}
     */

    private boolean withinInclusive(LocalDate d, LocalDate ini, LocalDate fim)
    {
        return (d.isEqual(ini) || d.isAfter(ini)) && (d.isEqual(fim) || d.isBefore(fim));
    }
    /**
     * Formata um total de horas removendo zeros desnecessários e usando vírgula como decimal.
     *
     * @param total quantidade de horas
     * @return texto com horas formatadas (sem casas quando inteiro; senão até 2 casas, vírgula decimal)
     */
    private String formatHoras(double total)
    {
        long arred = Math.round(total);
        if (Math.abs(total - arred) < 1e-9)
        {
            return String.valueOf(arred);
        }
        String s = String.format(java.util.Locale.US, "%.2f", total).replace('.', ',');
        while (s.contains(",") && (s.endsWith("0") || s.endsWith(",")))
        {
            s = s.substring(0, s.length() - 1);
            if (s.endsWith(","))
            {
                s = s.substring(0, s.length() - 1);
                break;
            }
        }
        return s;
    }
    /**
     * Formata um valor monetário com 2 casas decimais e vírgula como separador.
     *
     * @param v valor numérico
     * @return representação textual com 2 casas e vírgula decimal
     */
    private String formatValor2(double v)
    {
        return String.format(java.util.Locale.US, "%.2f", v).replace('.', ',');
    }
    /**
     * Soma das vendas realizadas por um comissionado em um intervalo de datas.
     *
     * @param id identificador do empregado comissionado
     * @param dataInicial data inicial no formato {@code d/M/uuuu}
     * @param dataFinal data final no formato {@code d/M/uuuu}
     * @return total das vendas no período, formatado com 2 casas e vírgula decimal
     * @throws Exception se o empregado não for comissionado; se datas forem inválidas;
     *                   se {@code dataInicial} for posterior a {@code dataFinal}
     */
    public String getVendasRealizadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado empregado = getEmpregado(id);
        if (!(empregado instanceof Comissionado)) {
            throw new EmpregadoNaoEhComissionadoException();
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new DataInicialInvalidaException();
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new DataFinalInvalidaException();
        }

        if (ini.isAfter(fim)) {
            throw new DataInicialPosteriorADataFinalException();
        }

        double total = 0.0;
        java.util.List<?> vendas = ((Comissionado) empregado).getListaVendas();

        for (Object obj : vendas) {
            ResultadoDeVenda v = (ResultadoDeVenda) obj;
            LocalDate d = v.getDate();
            if (isDateInRange(d, ini, fim)) {
                total += v.getValor();
            }
        }
        return formatValor2(total);
    }
    /**
     * Soma as taxas de serviço do sindicato em um intervalo de datas.
     *
     * @param id identificador do empregado (deve estar sindicalizado)
     * @param dataInicial data inicial no formato {@code d/M/uuuu}
     * @param dataFinal data final no formato {@code d/M/uuuu}
     * @return total das taxas de serviço no período, formatado com 2 casas e vírgula decimal
     * @throws Exception se o empregado não for sindicalizado; se datas forem inválidas;
     *                   se {@code dataInicial} for posterior a {@code dataFinal}
     */
    public String getTaxasServico(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado emp = getEmpregado(id);
        if (emp.getSindicato() == null) {
            throw new EmpregadoNaoEhSindicalizadoException();
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new DataInicialInvalidaException();
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new DataFinalInvalidaException();
        }

        if (ini.isAfter(fim)) {
            throw new DataInicialPosteriorADataFinalException();
        }

        double total = 0.0;
        java.util.List<TaxaServico> taxas = emp.getSindicato().getTotalTaxas();
        for (TaxaServico t : taxas) {
            LocalDate d = t.getData();
            if (isDateInRange(d, ini, fim)) {
                total += t.getValor();
            }
        }
        return formatValor2(total);
    }
    /**
     * Lança uma taxa de serviço ao membro do sindicato informado.
     *
     * @param membro ID do membro no sindicato (não nulo/nem vazio)
     * @param data data da taxa no formato {@code d/M/uuuu}
     * @param valor valor da taxa (texto; aceita vírgula/ponto; deve ser positivo)
     * @return a {@link TaxaServico} criada
     * @throws Exception se o membro não existir; se a data for inválida;
     *                   se o valor não for numérico ou não positivo
     */
    public TaxaServico lancaTaxaServicoPorMembro(String membro, String data, String valor) throws Exception {
        checkpoint();
        if (membro == null || membro.trim().isEmpty()) {
            throw new IdentificacaoMembroNulaException();
        }
        Empregado alvo = null;
        for (Empregado e : this.empregados) {
            if (e.isSindicalizado() && membro.equals(e.getSindicato().getIdMembro())) {
                alvo = e;
                break;
            }
        }
        if (alvo == null) {
            throw new MembroNaoExisteException();
        }
        LocalDate dt;
        try {
            dt = parseDateBR(data);
        } catch (Exception e) {
            throw new DataInvalidaException();
        }
        double v;
        try {
            v = Double.parseDouble(valor.replace(",", "."));
        } catch (Exception e) {
            throw new ValorDeveSerPositivoException();
        }
        if (v <= 0) {
            throw new ValorDeveSerPositivoException();
        }

        TaxaServico taxa = new TaxaServico(dt, v);
        alvo.getSindicato().addTaxa(taxa);

        return taxa;
    }
    /**
     * Converte o empregado para o tipo horista, validando e aplicando o novo salário-hora.
     *
     * @param id identificador do empregado
     * @param salario valor do salário-hora (texto; aceita vírgula/ponto; não negativo)
     * @throws Exception se o empregado não existir; se o salário for nulo/vazio;
     *                   se não for numérico ou for negativo
     */
    public void alteraEmpregadoTipoParaHorista(String id, String salario) throws Exception
    {
        checkpoint();
        Empregado emp = getEmpregado(id);

        if (salario == null || salario.trim().isEmpty())
        {
            throw new SalarioNuloException();
        }
        double v;
        try
        {
            v = Double.parseDouble(salario.replace(",", "."));
        }
        catch (NumberFormatException e)
        {
            throw new SalarioDeveSerNumericoException();
        }
        if (v < 0)
        {
            throw new SalarioNaoNegativoException();
        }

        Horista novo = new Horista(emp.getName(), emp.getEndereco(), emp.getId(), v, "horista");
        copiarDadosBasicos(emp, novo);
        substituirEmpregado(emp, novo);
    }
    /**
     * Converte o empregado para o tipo assalariado, validando e aplicando o novo salário mensal.
     *
     * @param id identificador do empregado
     * @param salario valor do salário mensal (texto; aceita vírgula/ponto; não negativo)
     * @throws Exception se o empregado não existir; se o salário for nulo/vazio;
     *                   se não for numérico ou for negativo
     */

    public void alteraEmpregadoTipoParaAssalariado(String id, String salario) throws Exception
    {
        checkpoint();
        Empregado emp = getEmpregado(id);

        if (salario == null || salario.trim().isEmpty())
        {
            throw new SalarioNuloException();
        }
        double v;
        try
        {
            v = Double.parseDouble(salario.replace(",", "."));
        }
        catch (NumberFormatException e)
        {
            throw new SalarioDeveSerNumericoException();
        }
        if (v < 0)
        {
            throw new SalarioNaoNegativoException();
        }

        Assalariado novo = new Assalariado(emp.getName(), emp.getEndereco(), emp.getId(), v, "assalariado");
        copiarDadosBasicos(emp, novo);
        substituirEmpregado(emp, novo);
    }

    /**
     * Converte o empregado para o tipo comissionado ou atualiza a comissão se já for comissionado.
     *
     * <p>Se o empregado não for comissionado, preserva dados básicos e define salário base a partir
     * do tipo anterior (assalariado: mensal; horista: salário-hora), mantendo o ID.</p>
     *
     * @param id identificador do empregado
     * @param comissao taxa de comissão (texto; aceita vírgula/ponto; não negativa)
     * @throws Exception se o empregado não existir; se a comissão for nula/vazia;
     *                   se não for numérica ou for negativa
     */
    public void alteraEmpregadoTipoParaComissionado(String id, String comissao) throws Exception {
        checkpoint();
        Empregado emp = getEmpregado(id);

        if (comissao == null || comissao.trim().isEmpty()) {
            throw new ComissaoNulaException();
        }
        double c;
        try {
            c = Double.parseDouble(comissao.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ComissaoDeveSerNumericaException();
        }
        if (c < 0) {
            throw new ComissaoNaoNegativaException();
        }

        if (emp instanceof Comissionado) {
            ((Comissionado) emp).setComissao(c);
            return;
        }

        double salarioBase = 0.0;
        if (emp instanceof Assalariado) {
            salarioBase = ((Assalariado) emp).getSalarioMensal();
        } else if (emp instanceof Horista) {
            salarioBase = ((Horista) emp).getSalarioHora();
        }

        Comissionado novo = new Comissionado(
                emp.getName(), emp.getEndereco(), emp.getId(), salarioBase, c, "comissionado"
        );
        copiarDadosBasicos(emp, novo);
        substituirEmpregado(emp, novo);
    }

    /**
     * Substitui um empregado no armazenamento interno, comparando por ID.
     *
     * @param antigo instância atualmente armazenada
     * @param novo instância que substituirá a antiga (mesmo ID)
     */
    public void substituirEmpregado(Empregado antigo, Empregado novo) {
        for (int i = 0; i < empregados.size(); i++) {
            if (empregados.get(i).getId().equals(antigo.getId())) {
                empregados.set(i, novo);
                break;
            }
        }
    }

    /**
     * Altera o método de pagamento para depósito em banco e define os dados bancários.
     *
     * @param id identificador do empregado
     * @param banco nome do banco (não nulo/vazio)
     * @param agencia agência (não nula/vazia)
     * @param contaCorrente conta corrente (não nula/vazia)
     * @throws Exception se o empregado não existir; se algum dado estiver nulo/vazio
     */
    public void alteraEmpregadoMetodoPagamentoBanco(String id, String banco, String agencia, String contaCorrente) throws Exception
    {
        checkpoint();
        Empregado emp = getEmpregado(id);

        if (banco == null || banco.trim().isEmpty())
        {
            throw new BancoNuloException();
        }
        if (agencia == null || agencia.trim().isEmpty())
        {
            throw new AgenciaNulaException();
        }
        if (contaCorrente == null || contaCorrente.trim().isEmpty())
        {
            throw new ContaCorrenteNulaException();
        }

        emp.setMetodoPagamento("banco");
        emp.setBanco(banco);
        emp.setAgencia(agencia);
        emp.setContaCorrente(contaCorrente);
    }

    /**
     * Altera o método de pagamento para um modo simples ({@code emMaos} ou {@code correios}).
     * Limpa dados bancários quando não for {@code banco}.
     *
     * @param id identificador do empregado
     * @param metodo um dos valores: {@code emMaos}, {@code banco}, {@code correios}
     * @throws Exception se o empregado não existir; se o método for inválido
     */
    public void alteraEmpregadoMetodoPagamentoSimples(String id, String metodo) throws Exception
    {
        checkpoint();
        Empregado emp = getEmpregado(id);
        if (!"emMaos".equals(metodo) && !"banco".equals(metodo) && !"correios".equals(metodo))
        {
            throw new MetodoPagamentoInvalidoException();
        }

        if (!"banco".equals(metodo))
        {
            emp.setMetodoPagamento(metodo);
            emp.setBanco(null);
            emp.setAgencia(null);
            emp.setContaCorrente(null);
        }
    }

    /**
     * Copia atributos compartilhados (sindicato e pagamento) de um empregado origem para outro destino.
     *
     * @param origem empregado de origem
     * @param destino empregado de destino
     */
    private void copiarDadosBasicos(Empregado origem, Empregado destino)
    {
        destino.setSindicato(origem.getSindicato());
        destino.setMetodoPagamento(origem.getMetodoPagamento());
        destino.setBanco(origem.getBanco());
        destino.setAgencia(origem.getAgencia());
        destino.setContaCorrente(origem.getContaCorrente());
    }

    /**
     * Calcula o total da folha de pagamento de uma data, somando o pagamento devido de cada empregado.
     *
     * @param data data de referência no formato {@code d/M/uuuu}
     * @return total formatado com vírgula decimal e 2 casas
     * @throws Exception se a data for inválida
     */
    public String totalFolha(String data) throws Exception {
        LocalDate dia;
        try { dia = parseDateBR(data); } catch (Exception e) { throw new DataInvalidaException(); }

        if (haAgendaCustomizada()) {
            java.math.BigDecimal totalAgenda = calcularTotalFolhaPorAgenda(dia);
            return totalAgenda.setScale(2, java.math.RoundingMode.HALF_UP)
                    .toPlainString()
                    .replace('.', ',');
        }

        BigDecimal totalBruto = BigDecimal.ZERO;

        if (isFriday(dia)) {
            LocalDate ini = weeklyStart(dia);
            for (Empregado emp : this.empregados) {
                if (!"horista".equals(emp.getTipo())) continue;
                BigDecimal bruto = calcularBrutoHorista((Horista) emp, ini, dia);
                totalBruto = totalBruto.add(bruto.setScale(2, java.math.RoundingMode.HALF_UP));
            }
        }
        if (isBiweeklyPayday(dia)) {
            LocalDate ini = biweeklyStart(dia);
            for (Empregado emp : this.empregados) {
                if (!"comissionado".equals(emp.getTipo())) continue;
                Comissionado c = (Comissionado) emp;
                BigDecimal base = BigDecimal.valueOf(c.getSalarioMensal())
                        .multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(26), 2, java.math.RoundingMode.FLOOR);
                BigDecimal vendas = BigDecimal.ZERO;
                for (ResultadoDeVenda v : c.getListaVendas()) {
                    LocalDate d = v.getDate();
                    if (!d.isBefore(ini) && !d.isAfter(dia)) vendas = vendas.add(BigDecimal.valueOf(v.getValor()));
                }
                BigDecimal comissao = vendas.multiply(BigDecimal.valueOf(c.getComissao()))
                        .setScale(2, java.math.RoundingMode.FLOOR);
                totalBruto = totalBruto.add(base.add(comissao));
            }
        }
        if (isLastWorkingDayOfMonth(dia)) {
            for (Empregado emp : this.empregados) {
                if (!"assalariado".equals(emp.getTipo())) continue;
                BigDecimal bruto = BigDecimal.valueOf(((Assalariado) emp).getSalarioMensal());
                totalBruto = totalBruto.add(bruto.setScale(2, java.math.RoundingMode.HALF_UP));
            }
        }

        return String.format(java.util.Locale.FRANCE, "%.2f", totalBruto);
    }

    /**
     * Gera o arquivo de folha de pagamento de uma data específica no formato esperado pelos testes.
     *
     * @param data data de referência no formato {@code d/M/uuuu}
     * @param saida caminho/arquivo de saída a ser escrito (não nulo/vazio)
     * @throws Exception se a saída for inválida; se a data for inválida; ou se ocorrer erro de escrita
     */
    private static final String HEADER_SEP = "====================================";
    private static final String FOOTER_SEP  = "===============================================================================================================================";

    private static final String SECTION_SEP = "===============================================================================================================================";
    private static final String SECTION_HORISTAS = "===================== HORISTAS ================================================================================================";
    private static final String SECTION_COMISSIONADOS = "===================== COMISSIONADOS ===========================================================================================";
    private static final String SECTION_ASSALARIADOS = "===================== ASSALARIADOS ============================================================================================";

    public void rodaFolha(String data, String saida) throws Exception {
        if (saida == null || saida.trim().isEmpty()) throw new ArquivoDeSaidaInvalidoException();

        LocalDate dia;
        try { dia = parseDateBR(data); } catch (Exception e) { throw new DataInvalidaException(); }

        // estruturas de linha
        class LH { String nome, metodo; BigDecimal hN=BigDecimal.ZERO,hX=BigDecimal.ZERO,br=BigDecimal.ZERO,ds=BigDecimal.ZERO,liq=BigDecimal.ZERO; }
        class LC { String nome, metodo; BigDecimal fixo=BigDecimal.ZERO,vendas=BigDecimal.ZERO,com=BigDecimal.ZERO,br=BigDecimal.ZERO,ds=BigDecimal.ZERO,liq=BigDecimal.ZERO; }
        class LA { String nome, metodo; BigDecimal br=BigDecimal.ZERO,ds=BigDecimal.ZERO,liq=BigDecimal.ZERO; }

        java.util.List<LH> hor = new java.util.ArrayList<>();
        java.util.List<LC> com = new java.util.ArrayList<>();
        java.util.List<LA> ass = new java.util.ArrayList<>();

        BigDecimal totalBrutoGeral = BigDecimal.ZERO;

        // ================= HORISTAS (sexta) =================
        BigDecimal tHn=BigDecimal.ZERO, tHx=BigDecimal.ZERO, tHbr=BigDecimal.ZERO, tHds=BigDecimal.ZERO, tHliq=BigDecimal.ZERO;
        if (isFriday(dia)) {
            LocalDate ini = weeklyStart(dia);
            for (Empregado e : this.empregados) {
                if (!"horista".equals(e.getTipo())) continue;
                Horista h = (Horista) e;

                BigDecimal n = BigDecimal.ZERO, x = BigDecimal.ZERO;
                for (CartaoDePonto c : h.getListaCartoes()) {
                    LocalDate d = c.getData();
                    if (!d.isBefore(ini) && !d.isAfter(dia)) {
                        double horas = c.getHoras();
                        n = n.add(BigDecimal.valueOf(Math.min(8.0, horas)));
                        x = x.add(BigDecimal.valueOf(Math.max(0.0, horas - 8.0)));
                    }
                }
                BigDecimal salH = BigDecimal.valueOf(h.getSalarioHora());
                BigDecimal bruto = n.multiply(salH).add(x.multiply(salH.multiply(BigDecimal.valueOf(1.5))));
                BigDecimal descontos = BigDecimal.ZERO;

                if (e.isSindicalizado()) {
                    MembroSindicato s = e.getSindicato();

                    if (bruto.compareTo(BigDecimal.ZERO) > 0) {
                        LocalDate ultimoPago = findUltimoDiaComPagamentoHorista((Horista) e, dia.minusDays(1));

                        LocalDate inicioTaxa;
                        if (ultimoPago != null) {
                            inicioTaxa = ultimoPago.plusDays(1);
                        } else {
                            inicioTaxa = weeklyStart(dia);
                        }

                        long diasParaCobrar = java.time.temporal.ChronoUnit.DAYS.between(inicioTaxa, dia) + 1;
                        if (diasParaCobrar < 0) diasParaCobrar = 0;

                        BigDecimal taxa = BigDecimal.valueOf(s.getTaxaSindical());
                        descontos = descontos.add(taxa.multiply(BigDecimal.valueOf(diasParaCobrar)));
                        for (TaxaServico t : s.getTotalTaxas()) {
                            LocalDate d = t.getData();
                            if (!d.isBefore(ini) && !d.isAfter(dia)) {
                                descontos = descontos.add(BigDecimal.valueOf(t.getValor()));
                            }
                        }
                    } else {
                        descontos = BigDecimal.ZERO;
                    }
                }

                BigDecimal liquido = bruto.subtract(descontos);
                if (liquido.signum() < 0) liquido = BigDecimal.ZERO;

                LH l = new LH();
                l.nome = e.getName();
                l.metodo = getMetodoPagamentoString(e);
                l.hN = n;
                l.hX = x;
                l.br = bruto.setScale(2, java.math.RoundingMode.HALF_UP);
                l.ds = descontos.setScale(2, java.math.RoundingMode.HALF_UP);
                l.liq = liquido.setScale(2, java.math.RoundingMode.HALF_UP);
                hor.add(l);

                tHn=tHn.add(n); tHx=tHx.add(x); tHbr=tHbr.add(l.br); tHds=tHds.add(l.ds); tHliq=tHliq.add(l.liq);
                totalBrutoGeral = totalBrutoGeral.add(l.br);
            }
            hor.sort(java.util.Comparator.comparing(a -> a.nome));
        }

        // ============== COMISSIONADOS (quinzenal) ===========
        BigDecimal tCf=BigDecimal.ZERO,tCv=BigDecimal.ZERO,tCcom=BigDecimal.ZERO,tCbr=BigDecimal.ZERO,tCds=BigDecimal.ZERO,tCliq=BigDecimal.ZERO;
        if (isBiweeklyPayday(dia)) {
            LocalDate ini = biweeklyStart(dia);
            for (Empregado e : this.empregados) {
                if (!"comissionado".equals(e.getTipo())) continue;
                Comissionado c = (Comissionado) e;

                BigDecimal fixo = BigDecimal.valueOf(c.getSalarioMensal())
                        .multiply(BigDecimal.valueOf(12)).divide(BigDecimal.valueOf(26), 2, java.math.RoundingMode.FLOOR);

                BigDecimal vendas = BigDecimal.ZERO;
                for (ResultadoDeVenda v : c.getListaVendas()) {
                    LocalDate d = v.getDate();
                    if (!d.isBefore(ini) && !d.isAfter(dia)) vendas = vendas.add(BigDecimal.valueOf(v.getValor()));
                }
                BigDecimal comissao = vendas.multiply(BigDecimal.valueOf(c.getComissao()))
                        .setScale(2, java.math.RoundingMode.FLOOR);

                BigDecimal bruto = fixo.add(comissao);
                BigDecimal descontos = BigDecimal.ZERO;

                if (e.isSindicalizado()) {
                    long dias = java.time.temporal.ChronoUnit.DAYS.between(ini, dia) + 1;
                    BigDecimal taxa = BigDecimal.valueOf(e.getSindicato().getTaxaSindical());
                    descontos = descontos.add(taxa.multiply(BigDecimal.valueOf(dias)));
                    for (TaxaServico t : e.getSindicato().getTotalTaxas()) {
                        LocalDate d = t.getData();
                        if (!d.isBefore(ini) && !d.isAfter(dia)) descontos = descontos.add(BigDecimal.valueOf(t.getValor()));
                    }
                    descontos = descontos.setScale(2, java.math.RoundingMode.HALF_UP);
                }

                BigDecimal liquido = bruto.subtract(descontos);
                if (liquido.signum() < 0) liquido = BigDecimal.ZERO;

                LC l = new LC();
                l.nome = e.getName();
                l.metodo = getMetodoPagamentoString(e);
                l.fixo = fixo;
                l.vendas = vendas.setScale(2, java.math.RoundingMode.HALF_UP);
                l.com = comissao;
                l.br = bruto.setScale(2, java.math.RoundingMode.HALF_UP);
                l.ds = descontos.setScale(2, java.math.RoundingMode.HALF_UP);
                l.liq = liquido.setScale(2, java.math.RoundingMode.HALF_UP);
                com.add(l);

                tCf=tCf.add(l.fixo); tCv=tCv.add(l.vendas); tCcom=tCcom.add(l.com);
                tCbr=tCbr.add(l.br); tCds=tCds.add(l.ds); tCliq=tCliq.add(l.liq);
                totalBrutoGeral = totalBrutoGeral.add(l.br);
            }
            com.sort(java.util.Comparator.comparing(a -> a.nome));
        }

        // ================= ASSALARIADOS (mês) ===============
        BigDecimal tAbr=BigDecimal.ZERO,tAds=BigDecimal.ZERO,tAliq=BigDecimal.ZERO;
        if (isLastWorkingDayOfMonth(dia)) {
            LocalDate ini = dia.withDayOfMonth(1);
            for (Empregado e : this.empregados) {
                if (!"assalariado".equals(e.getTipo())) continue;
                Assalariado a = (Assalariado) e;

                BigDecimal bruto = BigDecimal.valueOf(a.getSalarioMensal());
                BigDecimal descontos = BigDecimal.ZERO;

                if (e.isSindicalizado()) {
                    int diasMes = dia.lengthOfMonth();
                    BigDecimal taxa = BigDecimal.valueOf(e.getSindicato().getTaxaSindical());
                    descontos = descontos.add(taxa.multiply(BigDecimal.valueOf(diasMes)));
                    for (TaxaServico t : e.getSindicato().getTotalTaxas()) {
                        LocalDate d = t.getData();
                        if (!d.isBefore(ini) && !d.isAfter(dia)) descontos = descontos.add(BigDecimal.valueOf(t.getValor()));
                    }
                    descontos = descontos.setScale(2, java.math.RoundingMode.HALF_UP);
                }

                BigDecimal liquido = bruto.subtract(descontos);
                if (liquido.signum() < 0) liquido = BigDecimal.ZERO;

                LA l = new LA();
                l.nome = e.getName();
                l.metodo = getMetodoPagamentoString(e);
                l.br = bruto.setScale(2, java.math.RoundingMode.HALF_UP);
                l.ds = descontos.setScale(2, java.math.RoundingMode.HALF_UP);
                l.liq = liquido.setScale(2, java.math.RoundingMode.HALF_UP);
                ass.add(l);

                tAbr=tAbr.add(l.br); tAds=tAds.add(l.ds); tAliq=tAliq.add(l.liq);
                totalBrutoGeral = totalBrutoGeral.add(l.br);
            }
            ass.sort(java.util.Comparator.comparing(a -> a.nome));
        }

        // ================== Monta arquivo ===================
        String ln = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("FOLHA DE PAGAMENTO DO DIA ").append(dia.toString()).append(ln);
        sb.append(HEADER_SEP).append(ln);
        sb.append(ln);

        sb.append(SECTION_SEP).append(ln);
        sb.append(SECTION_HORISTAS).append(ln);
        sb.append(SECTION_SEP).append(ln);
        sb.append("Nome                                 Horas Extra Salario Bruto Descontos Salario Liquido Metodo").append(ln);
        sb.append("==================================== ===== ===== ============= ========= =============== ======================================").append(ln);
        for (LH r : hor) {
            sb.append(String.format(java.util.Locale.FRANCE,
                    "%-36s %5.0f %5.0f %13.2f %9.2f %15.2f %s",
                    r.nome, r.hN, r.hX, r.br, r.ds, r.liq, r.metodo)).append(ln);
        }
        sb.append(ln);
        sb.append(String.format(java.util.Locale.FRANCE,
                "TOTAL HORISTAS %27.0f %5.0f %13.2f %9.2f %15.2f",
                tHn, tHx, tHbr, tHds, tHliq)).append(ln);
        sb.append(ln);

        sb.append(SECTION_SEP).append(ln);
        sb.append(SECTION_ASSALARIADOS).append(ln);
        sb.append(SECTION_SEP).append(ln);
        sb.append("Nome                                             Salario Bruto Descontos Salario Liquido Metodo").append(ln);
        sb.append("================================================ ============= ========= =============== ======================================").append(ln);
        for (LA r : ass) {
            sb.append(String.format(java.util.Locale.FRANCE,
                    "%-48s %13.2f %9.2f %15.2f %s",
                    r.nome, r.br, r.ds, r.liq, r.metodo)).append(ln);
        }
        sb.append(ln);
        sb.append(String.format(java.util.Locale.FRANCE,
                "TOTAL ASSALARIADOS %43.2f %9.2f %15.2f",
                tAbr, tAds, tAliq)).append(ln);
        sb.append(ln);

        sb.append(SECTION_SEP).append(ln);
        sb.append(SECTION_COMISSIONADOS).append(ln);
        sb.append(SECTION_SEP).append(ln);
        sb.append("Nome                  Fixo     Vendas   Comissao Salario Bruto Descontos Salario Liquido Metodo").append(ln);
        sb.append("===================== ======== ======== ======== ============= ========= =============== ======================================").append(ln);
        for (LC r : com) {
            sb.append(String.format(java.util.Locale.FRANCE,
                    "%-21s %8.2f %8.2f %8.2f %13.2f %9.2f %15.2f %s",
                    r.nome, r.fixo, r.vendas, r.com, r.br, r.ds, r.liq, r.metodo)).append(ln);
        }
        sb.append(ln);
        sb.append(String.format(java.util.Locale.FRANCE,
                "TOTAL COMISSIONADOS %10.2f %8.2f %8.2f %13.2f %9.2f %15.2f",
                tCf, tCv, tCcom, tCbr, tCds, tCliq)).append(ln);
        sb.append(ln);

        sb.append("TOTAL FOLHA: ").append(String.format(java.util.Locale.FRANCE, "%.2f", totalBrutoGeral)).append(ln);

        java.nio.file.Path path = java.nio.file.Paths.get(saida);
        try {
            java.nio.file.Files.write(path, sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (java.io.IOException e) {
            throw new ErroAoEscreverArquivoDeSaidaException(e);
        }
        for (Empregado emp : this.empregados) {
            atualizarEstadoPosPagamento(emp, dia);
        }
    }
    /**
     * Gera a representação textual do método de pagamento para exibição em relatórios.
     *
     * <p>Regras:
     * <ul>
     *   <li>{@code emMaos} → "Em maos"</li>
     *   <li>{@code correios} → "Correios, &lt;endereco&gt;"</li>
     *   <li>{@code banco} → "&lt;Banco&gt;, Ag. &lt;Agencia&gt; CC &lt;Conta&gt;"</li>
     * </ul>
     * Campos nulos são tratados como strings vazias.</p>
     *
     * @param emp empregado a consultar
     * @return string amigável do método de pagamento
     */
    private String getMetodoPagamentoString(Empregado emp) {
        String metodo = emp.getMetodoPagamento();
        if (metodo == null || "emMaos".equalsIgnoreCase(metodo)) return "Em maos";
        if ("correios".equalsIgnoreCase(metodo)) return "Correios, " + emp.getEndereco();
        if ("banco".equalsIgnoreCase(metodo)) {
            return (emp.getBanco() == null ? "" : emp.getBanco()) +
                    ", Ag. " + (emp.getAgencia() == null ? "" : emp.getAgencia()) +
                    " CC " + (emp.getContaCorrente() == null ? "" : emp.getContaCorrente());
        }
        return "";
    }


    /**
     * Retorna o último ID numérico emitido pelo sistema (contador interno).
     *
     * @return valor atual do contador de IDs
     */
    public int getId() { return id; }

    /**
     * Verifica se a data informada é uma sexta-feira.
     *
     * @param d data a verificar
     * @return {@code true} se for sexta-feira; caso contrário {@code false}
     */
    private boolean isFriday(LocalDate d) {
        return d.getDayOfWeek() == java.time.DayOfWeek.FRIDAY;
    }
    /**
     * Obtém as horas trabalhadas por um horista no intervalo informado (inclusive).
     *
     * @param id identificador do empregado horista
     * @param dataInicial data inicial no formato {@code d/M/uuuu}
     * @param dataFinal data final no formato {@code d/M/uuuu}
     * @return total de horas formatado (inteiro sem casas, ou com vírgula e até 2 casas)
     * @throws Exception se o empregado não for horista; se datas forem inválidas;
     *                   se {@code dataInicial} for posterior a {@code dataFinal}
     */
    public String getHorasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado emp = getEmpregado(id);
        if (!(emp instanceof Horista)) {
            throw new EmpregadoNaoEhHoristaException();
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new DataInicialInvalidaException();
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new DataFinalInvalidaException();
        }

        if (ini.isAfter(fim)) {
            throw new DataInicialPosteriorADataFinalException();
        }

        double total = 0.0;
        for (CartaoDePonto c : ((Horista) emp).getListaCartoes()) {
            LocalDate d = c.getData();
            if (withinInclusive(d, ini, fim)) {
                total += c.getHoras();
            }
        }
        return formatHoras(total);
    }

    /**
     * Obtém as horas normais (até 8h/dia) de um horista no intervalo informado.
     *
     * @param id identificador do empregado horista
     * @param dataInicial data inicial no formato {@code d/M/uuuu}
     * @param dataFinal data final no formato {@code d/M/uuuu}
     * @return total de horas normais formatado
     * @throws Exception se o empregado não for horista; se datas forem inválidas;
     *                   se {@code dataInicial} for posterior a {@code dataFinal}
     */
    public String getHorasNormaisTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado emp = getEmpregado(id);
        if (!(emp instanceof Horista)) {
            throw new EmpregadoNaoEhHoristaException();
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new DataInicialInvalidaException();
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new DataFinalInvalidaException();
        }

        if (ini.isAfter(fim)) {
            throw new DataInicialPosteriorADataFinalException();
        }

        double normais = 0.0;
        for (CartaoDePonto c : ((Horista) emp).getListaCartoes()) {
            LocalDate d = c.getData();
            if (isDateInRange(d, ini, fim)) {
                double horasNoDia = c.getHoras();
                normais += Math.min(horasNoDia, 8.0);
            }
        }
        return formatHoras(normais);
    }

    /**
     * Obtém as horas extras (acima de 8h/dia) de um horista no intervalo informado.
     *
     * @param id identificador do empregado horista
     * @param dataInicial data inicial no formato {@code d/M/uuuu}
     * @param dataFinal data final no formato {@code d/M/uuuu}
     * @return total de horas extras formatado
     * @throws Exception se o empregado não for horista; se datas forem inválidas;
     *                   se {@code dataInicial} for posterior a {@code dataFinal}
     */
    public String getHorasExtrasTrabalhadas(String id, String dataInicial, String dataFinal) throws Exception {
        Empregado emp = getEmpregado(id);
        if (!(emp instanceof Horista)) {
            throw new EmpregadoNaoEhHoristaException();
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new DataInicialInvalidaException();
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new DataFinalInvalidaException();
        }

        if (ini.isAfter(fim)) {
            throw new DataInicialPosteriorADataFinalException();
        }

        double extras = 0.0;
        for (CartaoDePonto c : ((Horista) emp).getListaCartoes()) {
            LocalDate d = c.getData();
            if (isDateInRange(d, ini, fim)) {
                double horasNoDia = c.getHoras();
                extras += Math.max(0.0, horasNoDia - 8.0);
            }
        }
        return formatHoras(extras);
    }

    /**
     * Verifica se uma data está no intervalo [início, fim), isto é, maior/igual ao início e estritamente anterior ao fim.
     *
     * @param dateToCheck data a verificar
     * @param startDate data inicial (inclusive)
     * @param endDateExclusive data final (exclusiva)
     * @return {@code true} se estiver no intervalo; caso contrário {@code false}
     */
    private boolean isDateInRange(LocalDate dateToCheck, LocalDate startDate, LocalDate endDateExclusive) {
        return !dateToCheck.isBefore(startDate) && dateToCheck.isBefore(endDateExclusive);
    }
    /**
     * Verifica se a data informada é o último dia útil do mês (desconsidera sábados e domingos).
     *
     * @param d data a verificar
     * @return {@code true} se for o último dia útil do mês; caso contrário {@code false}
     */
    private boolean isLastWorkingDayOfMonth(LocalDate d) {
        DayOfWeek day = d.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return false;
        }
        LocalDate nextDay = d.plusDays(1);
        while (nextDay.getMonth() == d.getMonth()) {
            DayOfWeek nextDayOfWeek = nextDay.getDayOfWeek();
            if (nextDayOfWeek != DayOfWeek.SATURDAY && nextDayOfWeek != DayOfWeek.SUNDAY) {
                return false;
            }
            nextDay = nextDay.plusDays(1);
        }
        return true;
    }
    /**
     * Encontra a última sexta-feira anterior (ou igual) à data informada em que o horista
     * efetivamente recebeu pagamento (ou seja, cujo salário bruto semanal foi maior que zero).
     *
     * <p>A busca retrocede de sexta em sexta, calculando o bruto da semana [Sáb..Sex] por meio de
     * {@link #calcularBrutoHorista(Horista, java.time.LocalDate, java.time.LocalDate)} e retorna a
     * sexta correspondente ao primeiro bruto positivo encontrado.</p>
     *
     * <p>Para evitar varreduras indefinidas, a busca é limitada até 31/12/2004.</p>
     *
     * @param h horista a verificar
     * @param antesDe data limite superior exclusiva; a verificação começa na sexta-feira anterior
     *                (ou na própria data, se ela já for uma sexta)
     * @return a data da última sexta com pagamento (bruto > 0), ou {@code null} se nenhuma for encontrada
     */
    private LocalDate findUltimoDiaComPagamentoHorista(Horista h, LocalDate antesDe) {
        LocalDate f = antesDe;
        while (f.getDayOfWeek() != java.time.DayOfWeek.FRIDAY) {
            f = f.minusDays(1);
        }
        LocalDate limite = LocalDate.of(2004, 12, 31);
        while (f.isAfter(limite)) {
            LocalDate ini = weeklyStart(f);
            java.math.BigDecimal bruto = calcularBrutoHorista(h, ini, f);
            if (bruto.compareTo(java.math.BigDecimal.ZERO) > 0) {
                return f;
            }
            f = f.minusWeeks(1);
        }
        return null;
    }

    /**
     * Retorna a data de início da semana de pagamento (Sábado) para um dia de pagamento (Sexta).
     *
     * @param payday data da sexta-feira (dia de pagamento)
     * @return data do sábado anterior (6 dias antes)
     */
    private LocalDate weeklyStart(LocalDate payday) {
        return payday.minusDays(6);
    }
    /**
     * Retorna a data de início do período quinzenal (14 dias) para um dia de pagamento (sexta).
     *
     * @param payday data da sexta-feira (dia de pagamento)
     * @return data 13 dias antes (início do período de 14 dias)
     */
    private LocalDate biweeklyStart(LocalDate payday) {
        return payday.minusDays(13);
    }
    /**
     * Determina se a data é um dia de pagamento quinzenal (sextas alternadas) a partir da âncora de 2005-01-14.
     *
     * @param d data a verificar
     * @return {@code true} se for uma sexta de pagamento; caso contrário {@code false}
     */
    private boolean isBiweeklyPayday(LocalDate d) {
        if (d.getDayOfWeek() != DayOfWeek.FRIDAY) return false;
        LocalDate anchor = LocalDate.of(2005, 1, 14);
        long weeks = java.time.temporal.ChronoUnit.WEEKS.between(anchor, d);
        return weeks % 2 == 0;
    }
    /**
     * Calcula o pagamento líquido do horista no dia de pagamento (sexta), considerando semana Sáb..Sex.
     *
     * @param h horista
     * @param payday data da sexta-feira de pagamento
     * @return valor líquido (>= 0.0)
     */
    private double calcularPagamentoHorista(Horista h, LocalDate payday) {
        LocalDate ini = weeklyStart(payday);

        BigDecimal salarioBruto = calcularBrutoHorista(h, ini, payday);
        if (salarioBruto.compareTo(BigDecimal.ZERO) == 0) return 0.0;

        BigDecimal descontos = calcularDescontosHorista(h, ini, payday, true);

        BigDecimal salarioLiquido = salarioBruto.subtract(descontos);
        return salarioLiquido.max(BigDecimal.ZERO).setScale(2, RoundingMode.DOWN).doubleValue();
    }
    /**
     * Calcula o salário bruto do horista no intervalo [ini, fim], com horas extras a 1,5x acima de 8h/dia.
     *
     * @param h horista
     * @param ini data inicial (inclusive)
     * @param fim data final (inclusive)
     * @return total bruto como {@link BigDecimal}
     */
    private BigDecimal calcularBrutoHorista(Horista h, LocalDate ini, LocalDate fim) {
        BigDecimal salarioBruto = BigDecimal.ZERO;
        for (CartaoDePonto c : h.getListaCartoes()) {
            if (!c.getData().isBefore(ini) && !c.getData().isAfter(fim)) {
                double horas = c.getHoras();
                double normais = Math.min(8.0, horas);
                double extras = Math.max(0.0, horas - 8.0);
                double valorDia = (normais * h.getSalarioHora()) + (extras * h.getSalarioHora() * 1.5);
                salarioBruto = salarioBruto.add(BigDecimal.valueOf(valorDia));
            }
        }
        return salarioBruto;
    }
    /**
     * Calcula o bruto do comissionado no período: base proporcional (2 semanas) + comissão sobre vendas no intervalo.
     *
     * @param c comissionado
     * @param ini início do período (inclusive)
     * @param fim fim do período (inclusive)
     * @return total bruto como {@link BigDecimal}
     */
    private BigDecimal calcularBrutoComissionado(Comissionado c, LocalDate ini, LocalDate fim) {
        BigDecimal base = BigDecimal.valueOf(c.getSalarioMensal())
                .multiply(new BigDecimal("12"))
                .divide(new BigDecimal("26"), 2, RoundingMode.FLOOR);

        BigDecimal vendas = BigDecimal.ZERO;
        for (Object obj : c.getListaVendas()) {
            ResultadoDeVenda v = (ResultadoDeVenda) obj;
            if (!v.getDate().isBefore(ini) && !v.getDate().isAfter(fim)) {
                vendas = vendas.add(BigDecimal.valueOf(v.getValor()));
            }
        }

        BigDecimal comissao = vendas.multiply(BigDecimal.valueOf(c.getComissao()))
                .setScale(2, RoundingMode.FLOOR);

        return base.add(comissao);
    }
    /**
     * Retorna o salário bruto mensal do assalariado.
     *
     * @param a assalariado
     * @return salário mensal como {@link BigDecimal}
     */
    private BigDecimal calcularBrutoAssalariado(Assalariado a) {
        return BigDecimal.valueOf(a.getSalarioMensal());
    }
    /**
     * Calcula descontos do horista no período, incluindo dívida sindical acumulada e taxas de serviço.
     *
     * @param h horista
     * @param ini início do período (inclusive)
     * @param fim fim do período (inclusive)
     * @param simularAcumulo se {@code true}, simula a adição da taxa semanal à dívida (sem persistir)
     * @return total de descontos como {@link BigDecimal}
     */
    private BigDecimal calcularDescontosHorista(Horista h, LocalDate ini, LocalDate fim, boolean simularAcumulo) {
        if (!h.isSindicalizado()) return BigDecimal.ZERO;

        MembroSindicato sindicato = h.getSindicato();
        BigDecimal dividaAtual = BigDecimal.valueOf(sindicato.getDividaSindical());


        if (simularAcumulo) {
            dividaAtual = dividaAtual.add(BigDecimal.valueOf(sindicato.getTaxaSindical() * 7));
        }

        BigDecimal taxasServico = BigDecimal.ZERO;
        for (TaxaServico taxa : sindicato.getTotalTaxas()) {
            if (!taxa.getData().isBefore(ini) && !taxa.getData().isAfter(fim)) {
                taxasServico = taxasServico.add(BigDecimal.valueOf(taxa.getValor()));
            }
        }
        return dividaAtual.add(taxasServico);
    }
    /**
     * Calcula descontos gerais (taxa sindical diária proporcional ao período + taxas de serviço no intervalo).
     *
     * @param emp empregado (deve estar sindicalizado para haver descontos)
     * @param ini início do período (inclusive)
     * @param fim fim do período (inclusive)
     * @return total de descontos como {@link BigDecimal}
     */
    private BigDecimal calcularDescontosGerais(Empregado emp, LocalDate ini, LocalDate fim) {
        if (!emp.isSindicalizado()) return BigDecimal.ZERO;
        MembroSindicato sindicato = emp.getSindicato();
        BigDecimal desc = BigDecimal.ZERO;
        long diasNoPeriodo = java.time.temporal.ChronoUnit.DAYS.between(ini, fim) + 1;
        desc = desc.add(BigDecimal.valueOf(sindicato.getTaxaSindical() * diasNoPeriodo));
        for (TaxaServico taxa : sindicato.getTotalTaxas()) {
            if (!taxa.getData().isBefore(ini) && !taxa.getData().isAfter(fim)) {
                desc = desc.add(BigDecimal.valueOf(taxa.getValor()));
            }
        }
        return desc;
    }
    public void criarAgendaDePagamentos(String descricao) throws Exception {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new wepayu.exceptions.DescricaoAgendaInvalidaException();
        }
        String desc = descricao.trim();
        if (agendasDisponiveis.contains(desc)) {
            throw new wepayu.exceptions.AgendaDePagamentosJaExisteException();
        }
        if (desc.startsWith("semanal")) {
            String[] p = desc.split("\\s+");
            if (p.length == 2) {
                int d;
                try { d = Integer.parseInt(p[1]); } catch (Exception e) { throw new wepayu.exceptions.DescricaoAgendaInvalidaException(); }
                if (d < 1 || d > 7) throw new wepayu.exceptions.DescricaoAgendaInvalidaException();
            } else if (p.length == 3) {
                int n, d;
                try {
                    n = Integer.parseInt(p[1]);
                    d = Integer.parseInt(p[2]);
                } catch (Exception e) { throw new wepayu.exceptions.DescricaoAgendaInvalidaException(); }
                if (n < 1 || n > 52) throw new wepayu.exceptions.DescricaoAgendaInvalidaException();
                if (d < 1 || d > 7)  throw new wepayu.exceptions.DescricaoAgendaInvalidaException();
            } else {
                throw new wepayu.exceptions.DescricaoAgendaInvalidaException();
            }
        } else if (desc.startsWith("mensal")) {
            String[] p = desc.split("\\s+");
            if (p.length != 2) throw new wepayu.exceptions.DescricaoAgendaInvalidaException();
            String v = p[1];
            if ("$".equals(v)) {
            } else {
                int dia;
                try { dia = Integer.parseInt(v); } catch (Exception e) { throw new wepayu.exceptions.DescricaoAgendaInvalidaException(); }
                if (dia < 1 || dia > 28) throw new wepayu.exceptions.DescricaoAgendaInvalidaException();
            }
        } else {
            throw new wepayu.exceptions.DescricaoAgendaInvalidaException();
        }
        agendasDisponiveis.add(desc);
    }


    /**
     * Atualiza o estado de sindicato do horista após o pagamento do dia (ex.: dívida sindical e marca último dia pago).
     *
     * @param emp empregado processado
     * @param dia data do processamento/pagamento
     */
    private void atualizarEstadoPosPagamento(Empregado emp, LocalDate dia) {
        if ("horista".equals(emp.getTipo()) && emp.isSindicalizado()) {
            Horista h = (Horista) emp;
            MembroSindicato sindicato = h.getSindicato();

            if (sindicato.getUltimoDiaPago() != null && sindicato.getUltimoDiaPago().isEqual(dia)) {
                return;
            }

            BigDecimal taxaSemanal = BigDecimal.valueOf(sindicato.getTaxaSindical() * 7);
            BigDecimal dividaAtual = BigDecimal.valueOf(sindicato.getDividaSindical());
            sindicato.setDividaSindical(dividaAtual.add(taxaSemanal).doubleValue());

            LocalDate ini = weeklyStart(dia);
            BigDecimal salarioBruto = calcularBrutoHorista(h, ini, dia);
            BigDecimal descontos = calcularDescontosHorista(h, ini, dia, false);
            BigDecimal salarioLiquido = salarioBruto.subtract(descontos);

            if (salarioLiquido.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal dividaRestante = descontos.subtract(salarioBruto);
                sindicato.setDividaSindical(dividaRestante.doubleValue());
            } else {
                sindicato.setDividaSindical(0.0);
            }
            sindicato.setUltimoDiaPago(dia);
        }
    }
    /**
     * Retorna o número de empregados atualmente cadastrados.
     *
     * @return quantidade de empregados
     */
    public int getNumeroDeEmpregados() {
        return this.empregados.size();
    }
    /**
     * Retorna a lista interna de empregados.
     *
     * @return lista mutável de empregados
     */
    public ArrayList<Empregado> getEmpregados() {
        return this.empregados;
    }
    /**
     * Retorna a lista interna de empregados.
     *
     * @return lista mutável de empregados
     */
    public SistemaMemento save() {
        return new SistemaMemento(this.empregados, this.id);
    }
    /**
     * Restaura o estado do sistema a partir de um {@link SistemaMemento}.
     *
     * @param memento memento previamente obtido por {@link #save()}
     */
    public void restore(SistemaMemento memento) {
        this.empregados = new ArrayList<>(memento.getEmpregadosState());
        this.id = memento.getIdState();
    }
    /**
     * Remove todos os empregados e reinicia o contador de IDs para zero.
     */
    public void zerarDadosInternos() {
        checkpoint();
        this.empregados.clear();
        this.id = 0;
        this.agendasDisponiveis.clear();
        this.agendasDisponiveis.addAll(
                java.util.Arrays.asList("semanal 5", "mensal $", "semanal 2 5")
        );
    }
    /**
     * Indica se o sistema está encerrado.
     *
     * @return {@code true} se estiver encerrado; caso contrário {@code false}
     */
    public boolean isEncerrado() {
        return encerrado;
    }
    /**
     * Define o estado de encerramento do sistema.
     *
     * @param encerrado {@code true} para encerrar; {@code false} para reabrir
     */
    public void setEncerrado(boolean encerrado) {
        this.encerrado = encerrado;
    }
    /**
     * Cria um ponto de restauração do estado atual do sistema (snapshot) para permitir operações de {@code undo()}.
     *
     * <p>Regras de uso:
     * <ul>
     *   <li>Chamar <b>apenas uma vez</b> por comando lógico, <b>depois</b> de todas as validações e
     *       <b>antes</b> da primeira mutação real de estado.</li>
     *   <li>Ao criar um novo checkpoint, a pilha de <i>redo</i> é limpa para manter um histórico linear
     *       (comportamento padrão de editores: após desfazer e aplicar um novo comando, não há mais “refazer”).</li>
     * </ul>
     * </p>
     *
     * <p>Implementação: empilha o {@link SistemaMemento} retornado por {@link #save()} em {@code undoStack}
     * e esvazia {@code redoStack}.</p>
     */
    private void checkpoint() {
        undoStack.push(save());
        redoStack.clear();
    }

    /**
     * Desfaz o último comando aplicado, restaurando o snapshot anterior do sistema.
     *
     * <p>Semântica:
     * <ul>
     *   <li>Se houver estado anterior, o estado <b>atual</b> é salvo em {@code redoStack} para permitir {@link #redo()}.</li>
     *   <li>Em seguida, o topo de {@code undoStack} é desempilhado e restaurado via {@link #restore(SistemaMemento)}.</li>
     * </ul>
     * </p>
     *
     * @throws Exception se não houver nenhum comando a desfazer
     *                   (mensagem: {@code "Nao ha comando a desfazer."})
     */
    public void undo() throws Exception {
        if (undoStack.isEmpty()) {
            throw new NaoHaComandoDesfazer();
        }
        redoStack.push(save());
        SistemaMemento prev = undoStack.pop();
        restore(prev);
    }

    /**
     * Refaz o último {@link #undo()} aplicado, avançando um passo no histórico.
     *
     * <p>Semântica:
     * <ul>
     *   <li>Se houver um estado para refazer, o estado <b>atual</b> é salvo em {@code undoStack}.</li>
     *   <li>Em seguida, o topo de {@code redoStack} é desempilhado e restaurado via {@link #restore(SistemaMemento)}.</li>
     * </ul>
     * </p>
     *
     * <p>Observação: qualquer chamada a {@link #checkpoint()} após um {@code undo()} limpa a pilha de
     * <i>redo</i>, conforme o comportamento esperado de histórico linear.</p>
     *
     * @throws Exception se não houver nenhum comando a refazer
     *                   (mensagem: {@code "Nao ha comando a refazer."})
     */
    public void redo() throws Exception {
        if (redoStack.isEmpty()) {
            throw new NaoHaComandoRefazer();
        }
        undoStack.push(save());
        SistemaMemento next = redoStack.pop();
        restore(next);
    }
    /**
     * Avalia se um empregado deve ser pago na data informada segundo sua agenda.
     *
     * <p>Agendas suportadas:
     * <ul>
     *   <li>{@code mensal $}: último dia útil do mês</li>
     *   <li>{@code mensal N}: dia N do mês (1..28)</li>
     *   <li>{@code semanal [N] D}: a cada N semanas no dia da semana D (1=Seg..7=Dom).
     *       A primeira ocorrência é ancorada na próxima data útil D após a contratação.</li>
     * </ul></p>
     *
     * @param emp empregado com agenda configurada
     * @param data data a verificar
     * @return {@code true} se a data é dia de pagamento para o empregado; caso contrário {@code false}
     */
    private boolean deveSerPago(Empregado emp, LocalDate data) {
        String agenda = emp.getAgendaPagamento();
        if (agenda == null) return false;

        if (agenda.startsWith("mensal")) {
            String[] p = agenda.split("\\s+");
            if (p.length == 2 && "$".equals(p[1])) {
                return isUltimoDiaUtilDoMes(data);
            }
            if (p.length == 2) {
                try {
                    int diaMes = Integer.parseInt(p[1]);
                    return data.getDayOfMonth() == diaMes;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        }

        if (agenda.startsWith("semanal")) {
            String[] parts = agenda.trim().split("\\s+");
            int n = (parts.length == 3) ? Integer.parseInt(parts[1]) : 1;
            int dow = Integer.parseInt(parts[parts.length - 1]);
            DayOfWeek target = DayOfWeek.of(dow);

            if (data.getDayOfWeek() != target) return false;

            LocalDate contrato = getDataContratacao(emp);
            if (contrato == null) return false;
            LocalDate firstOnTarget = contrato.with(TemporalAdjusters.nextOrSame(target));
            LocalDate firstPay = (n > 1) ? firstOnTarget.plusWeeks(n - 1) : firstOnTarget;

            if (data.isBefore(firstPay)) return false;

            long weeks = ChronoUnit.WEEKS.between(firstPay, data);
            return weeks % n == 0;
        }

        return false;
    }
    /**
     * Determina a data de "contratação" usada como âncora para cálculo de agendas semanais.
     *
     * <p>Regras:
     * <ul>
     *   <li>Horista: menor data de cartão de ponto lançado</li>
     *   <li>Assalariado/Comissionado: usa {@code 2005-01-01}</li>
     * </ul></p>
     *
     * @param emp empregado
     * @return data de contratação/âncora; pode ser {@code null} se horista sem cartões
     */
    private LocalDate getDataContratacao(Empregado emp) {
        switch (emp.getTipo()) {
            case "horista":
                return getPrimeiraDataDeCartao((Horista) emp);
            case "assalariado":
            case "comissionado":
                return LocalDate.of(2005, 1, 1);
            default:
                return null;
        }
    }
    /**
     * Verifica se a data é o último dia útil do mês, desconsiderando sábados e domingos.
     *
     * @param d data a verificar
     * @return {@code true} se for o último dia útil; {@code false} caso contrário
     */
    private boolean isUltimoDiaUtilDoMes(LocalDate d) {
        LocalDate last = d.with(TemporalAdjusters.lastDayOfMonth());
        while (last.getDayOfWeek() == DayOfWeek.SATURDAY || last.getDayOfWeek() == DayOfWeek.SUNDAY) {
            last = last.minusDays(1);
        }
        return d.equals(last);
    }
    /**
     * Obtém a menor (mais antiga) data de cartão de ponto do horista.
     *
     * @param h horista
     * @return primeira data de cartão ou {@code null} se não houver
     */
    private LocalDate getPrimeiraDataDeCartao(Horista h) {
        LocalDate min = null;
        for (CartaoDePonto c : h.getListaCartoes()) {
            LocalDate d = c.getData();
            if (min == null || d.isBefore(min)) {
                min = d;
            }
        }
        return min;
    }
    /**
     * Calcula o total bruto da folha para a data informada respeitando agendas personalizadas.
     *
     * <p>Mensal:
     * <ul>
     *   <li>{@code mensal $}: assalariado recebe salário mensal; comissionado recebe salário mensal + comissão do mês (FLOOR/2); horista recebe bruto do mês</li>
     *   <li>{@code mensal N}: idem, mas apenas no dia N</li>
     * </ul>
     * Semanal:
     * <ul>
     *   <li>{@code semanal [N] D}: se for dia de pagamento, considera período de N semanas até {@code dia}</li>
     *   <li>Assalariado: base proporcional = mensal*12/52*N (FLOOR/2)</li>
     *   <li>Comissionado: mesma base proporcional + comissão das vendas no período (FLOOR/2)</li>
     *   <li>Horista: bruto apurado no período</li>
     * </ul></p>
     *
     * @param dia data de referência
     * @return somatório bruto como {@link BigDecimal} (duas casas aplicadas onde indicado)
     */
    private BigDecimal calcularTotalFolhaPorAgenda(LocalDate dia) {
        BigDecimal total = BigDecimal.ZERO;

        for (Empregado emp : this.empregados) {
            String agenda = emp.getAgendaPagamento();
            if (agenda == null) continue;

            // --------- MENSAL "$" ou "mensal N" ----------
            if (agenda.startsWith("mensal")) {
                String[] p = agenda.trim().split("\\s+");
                if (p.length != 2) continue;

                boolean payday;
                if ("$".equals(p[1])) {
                    payday = isUltimoDiaUtilDoMes(dia);
                } else {
                    int diaMes;
                    try { diaMes = Integer.parseInt(p[1]); } catch (Exception e) { continue; }
                    payday = (dia.getDayOfMonth() == diaMes);
                }
                if (!payday) continue;

                LocalDate iniMes = dia.withDayOfMonth(1);

                switch (emp.getTipo()) {
                    case "horista": {
                        Horista h = (Horista) emp;
                        BigDecimal bruto = calcularBrutoHorista(h, iniMes, dia);
                        total = total.add(bruto.setScale(2, RoundingMode.HALF_UP));
                        break;
                    }
                    case "assalariado": {
                        Assalariado a = (Assalariado) emp;
                        BigDecimal bruto = BigDecimal.valueOf(a.getSalarioMensal());
                        total = total.add(bruto.setScale(2, RoundingMode.HALF_UP));
                        break;
                    }
                    case "comissionado": {
                        Comissionado c = (Comissionado) emp;
                        BigDecimal base = BigDecimal.valueOf(c.getSalarioMensal());
                        BigDecimal vendas = BigDecimal.ZERO;
                        for (ResultadoDeVenda v : c.getListaVendas()) {
                            LocalDate dv = v.getDate();
                            if (!dv.isBefore(iniMes) && !dv.isAfter(dia)) {
                                vendas = vendas.add(BigDecimal.valueOf(v.getValor()));
                            }
                        }
                        BigDecimal comissao = vendas.multiply(BigDecimal.valueOf(c.getComissao()))
                                .setScale(2, RoundingMode.FLOOR);
                        total = total.add(base.add(comissao).setScale(2, RoundingMode.HALF_UP));
                        break;
                    }
                }
                continue;
            }

            // --------- SEMANAL [N] D ----------
            if (agenda.startsWith("semanal")) {
                String[] parts = agenda.trim().split("\\s+");
                int n = (parts.length == 3) ? Integer.parseInt(parts[1]) : 1;

                if (!deveSerPago(emp, dia)) continue;

                LocalDate iniSem = weeklyStart(dia).minusWeeks(Math.max(0, n - 1));

                switch (emp.getTipo()) {
                    case "horista": {
                        Horista h = (Horista) emp;
                        BigDecimal bruto = calcularBrutoHorista(h, iniSem, dia);
                        total = total.add(bruto.setScale(2, RoundingMode.HALF_UP));
                        break;
                    }
                    case "assalariado": {
                        Assalariado a = (Assalariado) emp;
                        BigDecimal base = BigDecimal.valueOf(a.getSalarioMensal())
                                .multiply(new BigDecimal("12"))
                                .multiply(new BigDecimal(n))
                                .divide(new BigDecimal("52"), 2, RoundingMode.FLOOR);
                        total = total.add(base);
                        break;
                    }
                    case "comissionado": {
                        Comissionado c = (Comissionado) emp;
                        BigDecimal base = BigDecimal.valueOf(c.getSalarioMensal())
                                .multiply(new BigDecimal("12"))
                                .multiply(new BigDecimal(n))
                                .divide(new BigDecimal("52"), 2, RoundingMode.HALF_UP);

                        LocalDate ini = weeklyStart(dia).minusWeeks(Math.max(0, n - 1));
                        BigDecimal vendas = BigDecimal.ZERO;
                        for (ResultadoDeVenda v : c.getListaVendas()) {
                            LocalDate dv = v.getDate();
                            if (!dv.isBefore(ini) && !dv.isAfter(dia)) {
                                vendas = vendas.add(BigDecimal.valueOf(v.getValor()));
                            }
                        }
                        BigDecimal comissao = vendas.multiply(BigDecimal.valueOf(c.getComissao()))
                                .setScale(2, RoundingMode.FLOOR);

                        total = total.add(base.add(comissao).setScale(2, RoundingMode.HALF_UP));
                        break;
                    }
                }
            }
        }
        return total;
    }
    /**
     * Retorna a agenda de pagamento padrão para um tipo de empregado.
     *
     * <ul>
     *   <li>horista → {@code "semanal 5"}</li>
     *   <li>assalariado → {@code "mensal $"}</li>
     *   <li>comissionado → {@code "semanal 2 5"}</li>
     * </ul>
     *
     * @param tipo tipo do empregado ({@code horista}, {@code assalariado}, {@code comissionado})
     * @return string da agenda padrão ou {@code null} se tipo desconhecido
     */
    private String agendaDefault(String tipo) {
        switch (tipo) {
            case "horista":      return "semanal 5";
            case "assalariado":  return "mensal $";
            case "comissionado": return "semanal 2 5";
            default:             return null;
        }
    }
    /**
     * Indica se existe ao menos um empregado com agenda diferente da agenda padrão do seu tipo.
     *
     * @return {@code true} se houver alguma agenda customizada; {@code false} caso contrário
     */
    private boolean haAgendaCustomizada() {
        for (Empregado e : this.empregados) {
            String ag = e.getAgendaPagamento();
            if (ag != null && !ag.equals(agendaDefault(e.getTipo()))) {
                return true;
            }
        }
        return false;
    }
    /**
     * Aplica a agenda padrão ao empregado caso este não tenha uma agenda definida (nula).
     *
     * @param e empregado alvo
     */
    private void aplicarAgendaDefaultSeVazia(Empregado e) {
        if (e.getAgendaPagamento() == null) {
            e.setAgendaPagamento(agendaDefault(e.getTipo()));
        }
    }
}