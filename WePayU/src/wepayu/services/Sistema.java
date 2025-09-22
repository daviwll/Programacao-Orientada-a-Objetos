package wepayu.services;
import wepayu.command.SistemaMemento;
import wepayu.models.*;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;


/**
 * Payroll core (Sistema).
 *
 * Implements employee creation/updates, timecards, sales receipts,
 * union membership handling, and payroll computation/generation.
 *
 * Design notes
 * ------------
 * - Hourly employees are paid on Fridays for the week (Sat..Fri inclusive).
 * - Salaried employees are paid on month-end day.
 * - Commissioned employees are paid every other Friday (biweekly),
 *   anchored at 2005-01-07 as a payday.
 * - Union deductions: fixed dues (per payday) plus service charges inside the pay period.
 *
 * Formatting
 * ----------
 * Monetary outputs are formatted with comma as decimal separator (pt-BR style), 2 decimals.
 */
public class Sistema
{
    private ArrayList<Empregado> empregados;
    private int id = 0;
    private boolean encerrado = false;

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
            throw new Exception("Identificacao do empregado nao pode ser nula.");
        }
        int idInt;
        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new Exception("Empregado nao existe.");
        }
        for (Empregado empregado : this.empregados) {
            if (Integer.parseInt(empregado.getId()) == idInt) {
                return empregado;
            }
        }
        throw new Exception("Empregado nao existe.");
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
        if (tipo.equals("comissionado"))
        {
            throw new Exception("Tipo nao aplicavel.");
        }
        if (name == null || name.trim().isEmpty())
        {
            throw new Exception("Nome nao pode ser nulo.");
        }
        if (endereco == null || endereco.trim().isEmpty())
        {
            throw new Exception("Endereco nao pode ser nulo.");
        }
        if (salario == null || salario.trim().isEmpty())
        {
            throw new Exception("Salario nao pode ser nulo.");
        }
        salario = salario.replace(",", ".");
        double salarioDouble;
        try
        {
            salarioDouble = Double.parseDouble(salario);
        }
        catch (NumberFormatException e)
        {
            throw new Exception("Salario deve ser numerico.");
        }
        if (salarioDouble < 0)
        {
            throw new Exception("Salario deve ser nao-negativo.");
        }

        if (tipo.equals("horista"))
        {
            this.id += 1;
            Horista novoEmpregado = new Horista(name, endereco, String.valueOf(this.id), salarioDouble, tipo);
            empregados.add(novoEmpregado);
            return novoEmpregado.getId();
        }
        else if (tipo.equals("assalariado"))
        {
            this.id += 1;
            Assalariado novoEmpregado = new Assalariado(name, endereco, String.valueOf(this.id), salarioDouble, tipo);
            empregados.add(novoEmpregado);
            return novoEmpregado.getId();
        }
        throw new Exception("Tipo invalido.");
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
        if (!tipo.equals("comissionado"))
        {
            throw new Exception("Tipo nao aplicavel.");
        }
        if (nome == null || nome.trim().isEmpty())
        {
            throw new Exception("Nome nao pode ser nulo.");
        }
        if (endereco == null || endereco.trim().isEmpty())
        {
            throw new Exception("Endereco nao pode ser nulo.");
        }
        if (comissao == null)
        {
            throw new Exception("Tipo nao aplicavel.");
        }
        if (comissao.equals(""))
        {
            throw new Exception("Comissao nao pode ser nula.");
        }
        if (salario == null || salario.trim().isEmpty())
        {
            throw new Exception("Salario nao pode ser nulo.");
        }

        salario = salario.replace(",", ".");
        double salarioDouble;
        try
        {
            salarioDouble = Double.parseDouble(salario);
        }
        catch (NumberFormatException e)
        {
            throw new Exception("Salario deve ser numerico.");
        }
        if (salarioDouble < 0)
        {
            throw new Exception("Salario deve ser nao-negativo.");
        }

        comissao = comissao.replace(",", ".");
        double comissaoDouble;
        try
        {
            comissaoDouble = Double.parseDouble(comissao);
        }
        catch (NumberFormatException e)
        {
            throw new Exception("Comissao deve ser numerica.");
        }
        if (comissaoDouble < 0)
        {
            throw new Exception("Comissao deve ser nao-negativa.");
        }

        this.id += 1;
        Comissionado novoEmpregado = new Comissionado(nome, endereco, String.valueOf(this.id), salarioDouble, comissaoDouble, tipo);
        empregados.add(novoEmpregado);
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
        Empregado empregado = getEmpregado(id);
        if (!(empregado instanceof Horista)) {
            throw new Exception("Empregado nao eh horista.");
        }

        LocalDate dataLanc;
        try {
            // Este bloco captura o erro de parsing e lança a mensagem correta
            dataLanc = parseDateBR(data);
        } catch (Exception e) {
            throw new Exception("Data invalida.");
        }

        double horasVal;
        try {
            horasVal = Double.parseDouble(horas.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Horas devem ser positivas.");
        }

        if (horasVal <= 0) {
            throw new Exception("Horas devem ser positivas.");
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
        Empregado empregado = getEmpregado(id);
        if (!(empregado instanceof Comissionado)) {
            throw new Exception("Empregado nao eh comissionado.");
        }

        LocalDate dataLanc;
        try {
            dataLanc = parseDateBR(data);
        } catch (Exception e) {
            throw new Exception("Data invalida.");
        }

        double valorNum;
        try {
            valorNum = Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Valor deve ser positivo.");
        }
        if (valorNum <= 0) {
            throw new Exception("Valor deve ser positivo.");
        }

        ResultadoDeVenda venda = new ResultadoDeVenda(dataLanc, valorNum);
        ((Comissionado) empregado).addVenda(venda);

        // Adicione esta linha para retornar o objeto 'venda' criado
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
        Empregado empregado = getEmpregado(id);

        if (atributo.equalsIgnoreCase("nome")) {
            if (valor1 == null || valor1.trim().isEmpty()) throw new Exception("Nome nao pode ser nulo.");
            empregado.setName(valor1);

        } else if (atributo.equalsIgnoreCase("endereco")) {
            if (valor1 == null || valor1.trim().isEmpty()) throw new Exception("Endereco nao pode ser nulo.");
            empregado.setEndereco(valor1);

        } else if (atributo.equalsIgnoreCase("tipo")) {
            // --- INÍCIO DA CORREÇÃO ---
            String novoSalarioOuComissao = valor2;
            // Se um novo salário/comissão não for informado (valor2 é null)...
            if (novoSalarioOuComissao == null) {
                // ...pegamos o salário atual do empregado.
                double salarioAtual;
                if (empregado instanceof Horista) {
                    salarioAtual = ((Horista) empregado).getSalarioHora();
                } else { // Assalariado ou Comissionado
                    salarioAtual = ((Assalariado) empregado).getSalarioMensal();
                }
                // E o formatamos como string para passar para os outros métodos
                novoSalarioOuComissao = String.format(java.util.Locale.US, "%.2f", salarioAtual).replace('.', ',');
            }

            if ("horista".equalsIgnoreCase(valor1)) {
                alteraEmpregadoTipoParaHorista(id, novoSalarioOuComissao);
            } else if ("comissionado".equalsIgnoreCase(valor1)) {
                alteraEmpregadoTipoParaComissionado(id, novoSalarioOuComissao);
            } else if ("assalariado".equalsIgnoreCase(valor1)) {
                alteraEmpregadoTipoParaAssalariado(id, novoSalarioOuComissao);
            } else {
                throw new Exception("Tipo invalido.");
            }
            // --- FIM DA CORREÇÃO ---

        } else if (atributo.equalsIgnoreCase("salario")) {
            if (valor1 == null || valor1.trim().isEmpty()) throw new Exception("Salario nao pode ser nulo.");
            String salarioCorrigido = valor1.replace(",", ".");
            double novoSalario;
            try {
                novoSalario = Double.parseDouble(salarioCorrigido);
            } catch (NumberFormatException e) {
                throw new Exception("Salario deve ser numerico.");
            }
            if (novoSalario < 0) throw new Exception("Salario deve ser nao-negativo.");

            if (empregado instanceof Horista) {
                ((Horista) empregado).setSalarioHora(novoSalario);
            } else if (empregado instanceof Assalariado) {
                ((Assalariado) empregado).setSalarioMensal(novoSalario);
            }

        } else if (atributo.equalsIgnoreCase("comissao")) {
            if (!(empregado instanceof Comissionado)) throw new Exception("Empregado nao eh comissionado.");
            if (valor1 == null || valor1.trim().isEmpty()) throw new Exception("Comissao nao pode ser nula.");
            String comissaoCorrigida = valor1.replace(",", ".");
            double novaComissao;
            try {
                novaComissao = Double.parseDouble(comissaoCorrigida);
            } catch (NumberFormatException e) {
                throw new Exception("Comissao deve ser numerica.");
            }
            if (novaComissao < 0) throw new Exception("Comissao deve ser nao-negativa.");
            ((Comissionado) empregado).setComissao(novaComissao);

        } else if (atributo.equalsIgnoreCase("sindicalizado")) {
            if (!"true".equalsIgnoreCase(valor1) && !"false".equalsIgnoreCase(valor1)) {
                throw new Exception("Valor deve ser true ou false.");
            }
            boolean isSindicalizado = Boolean.parseBoolean(valor1);

            if (isSindicalizado) {
                if (valor2 == null || valor2.isBlank()) throw new Exception("Identificacao do sindicato nao pode ser nula.");
                if (valor3 == null || valor3.isBlank()) throw new Exception("Taxa sindical nao pode ser nula.");

                String taxaCorrigida = valor3.replace(",", ".");
                double taxaSindical;
                try {
                    taxaSindical = Double.parseDouble(taxaCorrigida);
                } catch (NumberFormatException e) {
                    throw new Exception("Taxa sindical deve ser numerica.");
                }
                if (taxaSindical < 0) throw new Exception("Taxa sindical deve ser nao-negativa.");

                for (Empregado outro : this.empregados) {
                    if (outro != empregado && outro.getSindicato() != null &&
                            valor2.equals(outro.getSindicato().getIdMembro())) {
                        throw new Exception("Ha outro empregado com esta identificacao de sindicato");
                    }
                }
                MembroSindicato novoMembro = new MembroSindicato(valor2, taxaSindical);
                empregado.setSindicato(novoMembro);
            } else {
                empregado.setSindicato(null);
            }

        } else if (atributo.equalsIgnoreCase("metodoPagamento")) {
            if (!"emMaos".equalsIgnoreCase(valor1) && !"banco".equalsIgnoreCase(valor1) && !"correios".equalsIgnoreCase(valor1)) {
                throw new Exception("Metodo de pagamento invalido.");
            }
            alteraEmpregadoMetodoPagamentoSimples(id, valor1);

        } else {
            throw new Exception("Atributo nao existe.");
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
            } else { // Assalariado
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
            throw new Exception("Empregado nao eh comissionado.");
        }
        else if (atributo.equalsIgnoreCase("metodoPagamento"))
        {
            return empregado.getMetodoPagamento() == null ? "emMaos" : empregado.getMetodoPagamento();
        }
        else if (atributo.equalsIgnoreCase("banco"))
        {
            if (!"banco".equals(empregado.getMetodoPagamento()))
            {
                throw new Exception("Empregado nao recebe em banco.");
            }
            return empregado.getBanco();
        }
        else if (atributo.equalsIgnoreCase("agencia"))
        {
            if (!"banco".equals(empregado.getMetodoPagamento()))
            {
                throw new Exception("Empregado nao recebe em banco.");
            }
            return empregado.getAgencia();
        }
        else if (atributo.equalsIgnoreCase("contaCorrente"))
        {
            if (!"banco".equals(empregado.getMetodoPagamento()))
            {
                throw new Exception("Empregado nao recebe em banco.");
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
                throw new Exception("Empregado nao eh sindicalizado.");
            }
            return empregado.getSindicato().getIdMembro();
        }
        else if (atributo.equalsIgnoreCase("taxaSindical"))
        {
            if (empregado.getSindicato() == null)
            {
                throw new Exception("Empregado nao eh sindicalizado.");
            }
            double taxa = empregado.getSindicato().getTaxaSindical();
            return String.format(java.util.Locale.US, "%.2f", taxa).replace('.', ',');
        }

        throw new Exception("Atributo nao existe.");
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

        throw new Exception("Nao ha empregado com esse nome.");
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
            throw new Exception("Empregado nao eh comissionado.");
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new Exception("Data inicial invalida.");
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new Exception("Data final invalida.");
        }

        if (ini.isAfter(fim)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        double total = 0.0;
        java.util.List<?> vendas = ((Comissionado) empregado).getListaVendas();

        for (Object obj : vendas) {
            ResultadoDeVenda v = (ResultadoDeVenda) obj;
            LocalDate d = v.getDate();

            // *** AQUI ESTÁ A CORREÇÃO ***
            // Trocamos 'withinInclusive' por 'isDateInRange'
            if (isDateInRange(d, ini, fim)) {
                total += v.getValor();
            }
        }
        return formatValor2(total);
    }
    /**
     * Ativa/desativa sindicalização do empregado, validando unicidade do ID de membro e a taxa.
     *
     * @param id identificador do empregado
     * @param valor {@code "true"} para sindicalizar; {@code "false"} para dessindicalizar
     * @param idMembro identificador único no sindicato (obrigatório se {@code valor} for {@code "true"})
     * @param taxaSindical taxa diária do sindicato (texto; aceita vírgula/ponto; não negativa)
     * @throws Exception se {@code valor} não for {@code true}/{@code false}; se dados obrigatórios estiverem ausentes;
     *                   se taxa inválida/negativa; se {@code idMembro} já estiver em uso por outro empregado
     */
    public void alteraEmpregadoSindicalizado(String id, String valor, String idMembro, String taxaSindical) throws Exception {
        Empregado emp = getEmpregado(id);

        if (!"true".equalsIgnoreCase(valor) && !"false".equalsIgnoreCase(valor)) {
            throw new Exception("Valor deve ser true ou false.");
        }

        if ("false".equalsIgnoreCase(valor)) {
            emp.setSindicato(null);
            return;
        }

        if (idMembro == null || idMembro.trim().isEmpty()) {
            throw new Exception("Identificacao do sindicato nao pode ser nula.");
        }
        if (taxaSindical == null || taxaSindical.trim().isEmpty()) {
            throw new Exception("Taxa sindical nao pode ser nula.");
        }

        double taxa;
        try {
            taxa = Double.parseDouble(taxaSindical.replace(",", "."));
        } catch (Exception e) {
            throw new Exception("Taxa sindical deve ser numerica.");
        }
        if (taxa < 0) {
            throw new Exception("Taxa sindical deve ser nao-negativa.");
        }

        // uniqueness
        for (Empregado outro : this.empregados) {
            if (outro != emp && outro.getSindicato() != null &&
                    idMembro.equals(outro.getSindicato().getIdMembro())) {
                throw new Exception("Ha outro empregado com esta identificacao de sindicato");
            }
        }

        MembroSindicato novo = new MembroSindicato(idMembro, taxa);
        emp.setSindicato(novo);
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
            throw new Exception("Empregado nao eh sindicalizado.");
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new Exception("Data inicial invalida.");
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new Exception("Data final invalida.");
        }

        if (ini.isAfter(fim)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        double total = 0.0;
        // É uma boa prática usar tipos específicos, como List<TaxaServico>
        java.util.List<TaxaServico> taxas = emp.getSindicato().getTotalTaxas();
        for (TaxaServico t : taxas) {
            LocalDate d = t.getData();
            // *** AQUI ESTÁ A CORREÇÃO ***
            // Trocamos a verificação de data para a correta
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
        if (membro == null || membro.trim().isEmpty()) {
            throw new Exception("Identificacao do membro nao pode ser nula.");
        }
        Empregado alvo = null;
        for (Empregado e : this.empregados) {
            if (e.isSindicalizado() && membro.equals(e.getSindicato().getIdMembro())) {
                alvo = e;
                break;
            }
        }
        if (alvo == null) {
            throw new Exception("Membro nao existe.");
        }
        LocalDate dt;
        try {
            dt = parseDateBR(data);
        } catch (Exception e) {
            throw new Exception("Data invalida.");
        }
        double v;
        try {
            v = Double.parseDouble(valor.replace(",", "."));
        } catch (Exception e) {
            throw new Exception("Valor deve ser positivo.");
        }
        if (v <= 0) {
            throw new Exception("Valor deve ser positivo.");
        }

        TaxaServico taxa = new TaxaServico(dt, v);
        alvo.getSindicato().addTaxa(taxa);

        return taxa; // Adicione esta linha de retorno
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
        Empregado emp = getEmpregado(id);

        if (salario == null || salario.trim().isEmpty())
        {
            throw new Exception("Salario nao pode ser nulo.");
        }
        double v;
        try
        {
            v = Double.parseDouble(salario.replace(",", "."));
        }
        catch (NumberFormatException e)
        {
            throw new Exception("Salario deve ser numerico.");
        }
        if (v < 0)
        {
            throw new Exception("Salario deve ser nao-negativo.");
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
        Empregado emp = getEmpregado(id);

        if (salario == null || salario.trim().isEmpty())
        {
            throw new Exception("Salario nao pode ser nulo.");
        }
        double v;
        try
        {
            v = Double.parseDouble(salario.replace(",", "."));
        }
        catch (NumberFormatException e)
        {
            throw new Exception("Salario deve ser numerico.");
        }
        if (v < 0)
        {
            throw new Exception("Salario deve ser nao-negativo.");
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
        Empregado emp = getEmpregado(id);

        if (comissao == null || comissao.trim().isEmpty()) {
            throw new Exception("Comissao nao pode ser nula.");
        }
        double c;
        try {
            c = Double.parseDouble(comissao.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new Exception("Comissao deve ser numerica.");
        }
        if (c < 0) {
            throw new Exception("Comissao deve ser nao-negativa.");
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
            // CORREÇÃO: Compara pelo ID, e não pela referência do objeto.
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
        Empregado emp = getEmpregado(id);

        if (banco == null || banco.trim().isEmpty())
        {
            throw new Exception("Banco nao pode ser nulo.");
        }
        if (agencia == null || agencia.trim().isEmpty())
        {
            throw new Exception("Agencia nao pode ser nulo.");
        }
        if (contaCorrente == null || contaCorrente.trim().isEmpty())
        {
            throw new Exception("Conta corrente nao pode ser nulo.");
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
        Empregado emp = getEmpregado(id);
        if (!"emMaos".equals(metodo) && !"banco".equals(metodo) && !"correios".equals(metodo))
        {
            throw new Exception("Metodo de pagamento invalido.");
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
        try {
            dia = parseDateBR(data);
        } catch (Exception e) {
            throw new Exception("Data invalida.");
        }

        double total = 0.0;
        for (Empregado emp : this.empregados) {
            total += calcularPagamentoDoDia(emp, dia);
        }

        return String.format(java.util.Locale.US, "%.2f", total).replace('.', ',');
    }
    /**
     * Gera o arquivo de folha de pagamento de uma data específica no formato esperado pelos testes.
     *
     * <p>Formato do arquivo:</p>
     * <pre>
     * FOLHA DE PAGAMENTO DO DIA YYYY-MM-DD
     * ====================================
     *
     * ID=..., NOME=..., VALOR=...
     * ...
     * ===============================================================================================================================
     * TOTAL: x,xx
     * </pre>
     *
     * <p>Observação: o cálculo adiciona os pagamentos do dia e atualiza o estado pós-pagamento
     * (por exemplo, dívida sindical de horistas) somente após calcular o valor.</p>
     *
     * @param data data de referência no formato {@code d/M/uuuu}
     * @param saida caminho/arquivo de saída a ser escrito (não nulo/vazio)
     * @throws Exception se a saída for inválida; se a data for inválida; ou se ocorrer erro de escrita
     */
    public void rodaFolha(String data, String saida) throws Exception {
        if (saida == null || saida.trim().isEmpty()) {
            throw new Exception("Arquivo de saida invalido.");
        }

        LocalDate dia;
        try {
            dia = parseDateBR(data);
        } catch (Exception e) {
            throw new Exception("Data invalida.");
        }

        // A classe interna 'Pagamento' está correta e pode continuar como está.
        class Pagamento {
            final int idNum;
            final String idStr;
            final String nome;
            final double valor;
            Pagamento(Empregado e, double v) {
                this.idStr = e.getId();
                this.idNum = Integer.parseInt(e.getId());
                this.nome = e.getName();
                this.valor = v;
            }
        }

        java.util.List<Pagamento> pagos = new java.util.ArrayList<>();
        // Usamos BigDecimal para o total para evitar erros de arredondamento.
        BigDecimal totalNum = BigDecimal.ZERO;

        // --- INÍCIO DA LÓGICA CORRIGIDA ---
        for (Empregado emp : this.empregados) {
            // 1. CALCULA o pagamento usando o método "puro" que apenas lê os dados.
            double v = calcularPagamentoDoDia(emp, dia);

            if (v > 0.0) {
                pagos.add(new Pagamento(emp, v));
                totalNum = totalNum.add(BigDecimal.valueOf(v));
            }

            // 2. ATUALIZA o estado do empregado (dívida, etc.) APÓS o cálculo.
            //    (Este é o método que criamos na etapa anterior).
            atualizarEstadoPosPagamento(emp, dia);
        }
        // --- FIM DA LÓGICA CORRIGIDA ---

        pagos.sort(java.util.Comparator.comparingInt(p -> p.idNum));

        // Formata o total a partir do BigDecimal.
        String totalStr = String.format(java.util.Locale.US, "%.2f", totalNum).replace('.', ',');

        // O restante do método para gerar o arquivo de saída está correto.
        final String HEADER_SEP = "====================================";
        final String FOOTER_SEP = "===============================================================================================================================";

        StringBuilder sb = new StringBuilder();
        sb.append("FOLHA DE PAGAMENTO DO DIA ").append(dia.toString()).append(System.lineSeparator());
        sb.append(HEADER_SEP).append(System.lineSeparator());
        sb.append(System.lineSeparator());

        for (Pagamento p : pagos) {
            String valorFmt = String.format(java.util.Locale.US, "%.2f", p.valor).replace('.', ',');
            sb.append("ID=").append(p.idStr)
                    .append(", NOME=").append(p.nome)
                    .append(", VALOR=").append(valorFmt)
                    .append(System.lineSeparator());
        }

        sb.append(FOOTER_SEP).append(System.lineSeparator());
        sb.append("TOTAL: ").append(totalStr).append(System.lineSeparator());

        java.nio.file.Path p = java.nio.file.Paths.get(saida);
        try {
            java.nio.file.Files.write(p, sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (java.io.IOException e) {
            throw new Exception("Erro ao escrever arquivo de saida.");
        }
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
     * Verifica se a data informada é o último dia do mês (calendário).
     *
     * @param d data a verificar
     * @return {@code true} se for o último dia do mês; caso contrário {@code false}
     */
    private boolean isMonthEnd(LocalDate d) {
        return d.getDayOfMonth() == d.lengthOfMonth();
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
            throw new Exception("Empregado nao eh horista.");
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new Exception("Data inicial invalida.");
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new Exception("Data final invalida.");
        }

        if (ini.isAfter(fim)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
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
            throw new Exception("Empregado nao eh horista.");
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new Exception("Data inicial invalida.");
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new Exception("Data final invalida.");
        }

        if (ini.isAfter(fim)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        double normais = 0.0;
        for (CartaoDePonto c : ((Horista) emp).getListaCartoes()) {
            LocalDate d = c.getData();
            // *** LINHA ALTERADA ***
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
            throw new Exception("Empregado nao eh horista.");
        }

        LocalDate ini;
        try {
            ini = parseDateBR(dataInicial);
        } catch (Exception e) {
            throw new Exception("Data inicial invalida.");
        }

        LocalDate fim;
        try {
            fim = parseDateBR(dataFinal);
        } catch (Exception e) {
            throw new Exception("Data final invalida.");
        }

        if (ini.isAfter(fim)) {
            throw new Exception("Data inicial nao pode ser posterior aa data final.");
        }

        double extras = 0.0;
        for (CartaoDePonto c : ((Horista) emp).getListaCartoes()) {
            LocalDate d = c.getData();
            // *** LINHA ALTERADA ***
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
        // A lógica correta: a data deve ser IGUAL OU POSTERIOR ao início E ANTERIOR ao fim.
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
        // Não pode ser um dia de pagamento se não for dia útil.
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return false;
        }
        // Verifica se existe algum outro dia útil depois dele no mesmo mês.
        LocalDate nextDay = d.plusDays(1);
        while (nextDay.getMonth() == d.getMonth()) {
            DayOfWeek nextDayOfWeek = nextDay.getDayOfWeek();
            if (nextDayOfWeek != DayOfWeek.SATURDAY && nextDayOfWeek != DayOfWeek.SUNDAY) {
                // Encontrou um dia útil posterior, então 'd' não é o último.
                return false;
            }
            nextDay = nextDay.plusDays(1);
        }
        // Se o loop terminou, 'd' é o último dia útil do mês.
        return true;
    }
    /**
     * Retorna a data de início da semana de pagamento (Sábado) para um dia de pagamento (Sexta).
     *
     * @param payday data da sexta-feira (dia de pagamento)
     * @return data do sábado anterior (6 dias antes)
     */
    private LocalDate weeklyStart(LocalDate payday) {
        return payday.minusDays(6); // Sábado a Sexta (7 dias)
    }
    /**
     * Retorna a data de início do período quinzenal (14 dias) para um dia de pagamento (sexta).
     *
     * @param payday data da sexta-feira (dia de pagamento)
     * @return data 13 dias antes (início do período de 14 dias)
     */
    private LocalDate biweeklyStart(LocalDate payday) {
        return payday.minusDays(13); // Período de 14 dias
    }
    /**
     * Determina se a data é um dia de pagamento quinzenal (sextas alternadas) a partir da âncora de 2005-01-14.
     *
     * @param d data a verificar
     * @return {@code true} se for uma sexta de pagamento; caso contrário {@code false}
     */
    private boolean isBiweeklyPayday(LocalDate d) {
        if (d.getDayOfWeek() != DayOfWeek.FRIDAY) return false;
        // Âncora na segunda sexta-feira de trabalho, conforme regra do teste.
        LocalDate anchor = LocalDate.of(2005, 1, 14);
        long weeks = java.time.temporal.ChronoUnit.WEEKS.between(anchor, d);
        return weeks % 2 == 0;
    }
    /**
     * Decide se um empregado deve ser pago no dia informado e calcula o valor líquido devido.
     *
     * <p>Regras:</p>
     * <ul>
     *   <li>Horista: paga às sextas (período Sáb..Sex).</li>
     *   <li>Comissionado: paga em sextas quinzenais (período de 14 dias).</li>
     *   <li>Assalariado: paga no último dia útil do mês.</li>
     * </ul>
     *
     * @param emp empregado a avaliar
     * @param dia data de referência
     * @return valor líquido devido no dia (0.0 se não for dia de pagamento)
     */
    private double calcularPagamentoDoDia(Empregado emp, LocalDate dia) {
        String tipo = emp.getTipo();

        if ("horista".equals(tipo)) {
            if (!isFriday(dia)) return 0.0;
            return calcularPagamentoHorista( (Horista) emp, dia);
        }
        if ("comissionado".equals(tipo)) {
            if (!isBiweeklyPayday(dia)) return 0.0;
            LocalDate ini = biweeklyStart(dia);
            BigDecimal bruto = calcularBrutoComissionado((Comissionado) emp, ini, dia);
            BigDecimal desc = calcularDescontosGerais(emp, ini, dia);
            return bruto.subtract(desc).max(BigDecimal.ZERO).setScale(2, RoundingMode.DOWN).doubleValue();
        }
        if ("assalariado".equals(tipo)) {
            if (!isLastWorkingDayOfMonth(dia)) return 0.0;
            LocalDate ini = dia.withDayOfMonth(1);
            BigDecimal bruto = calcularBrutoAssalariado((Assalariado) emp);
            BigDecimal desc = calcularDescontosGerais(emp, ini, dia);
            return bruto.subtract(desc).max(BigDecimal.ZERO).setScale(2, RoundingMode.DOWN).doubleValue();
        }
        return 0.0;
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

        BigDecimal descontos = calcularDescontosHorista(h, ini, payday, true); // Simula o acúmulo da dívida

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
        BigDecimal base = BigDecimal.valueOf(c.getSalarioMensal() * 12.0 / 52.0 * 2.0);
        BigDecimal vendas = BigDecimal.ZERO;
        for (Object obj : c.getListaVendas()) {
            ResultadoDeVenda v = (ResultadoDeVenda) obj;
            if (!v.getDate().isBefore(ini) && !v.getDate().isAfter(fim)) {
                vendas = vendas.add(BigDecimal.valueOf(v.getValor()));
            }
        }
        BigDecimal comissao = vendas.multiply(BigDecimal.valueOf(c.getComissao()));
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

        // Se for uma simulação (totalFolha), adicionamos a taxa da semana virtualmente
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
        this.empregados = memento.getEmpregadosState();
        this.id = memento.getIdState();
    }
    /**
     * Remove todos os empregados e reinicia o contador de IDs para zero.
     */
    public void zerarDadosInternos() {
        this.empregados.clear(); // Usar clear() é um pouco mais eficiente aqui
        this.id = 0;
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
     * Adiciona um empregado à lista interna (não altera o contador de ID).
     *
     * @param empregado empregado a adicionar
     */
    public void addEmpregado(Empregado empregado) {
        this.empregados.add(empregado);
    }


}
