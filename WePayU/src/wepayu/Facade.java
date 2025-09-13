package wepayu;

public class Facade {
    private Sistema sistema;
    private Invoker invoker;
    private static Sistema sistemaGlobal;
    private static Invoker invokerGlobal;

    public Facade() {
        if (Facade.sistemaGlobal == null) {
            Facade.sistemaGlobal = new Sistema();
            Facade.invokerGlobal = new Invoker();
        }
        this.sistema = Facade.sistemaGlobal;
        this.invoker = Facade.invokerGlobal;
    }

    public void zerarSistema() {
        Facade.sistemaGlobal = new Sistema();
        Facade.invokerGlobal = new Invoker();
        this.sistema = Facade.sistemaGlobal;
        this.invoker = Facade.invokerGlobal;
    }

      public void zeraSistema() {
        zerarSistema();
    }

    public void encerrarSistema()
    {

    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario) throws Exception {
        Command comando = new CriarEmpregadoCommand(this.sistema, nome, endereco, tipo, salario);
        this.invoker.executeCommand(comando);
        return String.valueOf(sistema.getId());
    }

    public String criarEmpregado(String nome, String endereco, String tipo, String salario, String comissao) throws Exception {
        Command comando = new CriarEmpregadoCommand(this.sistema, nome, endereco, tipo, salario, comissao);
        this.invoker.executeCommand(comando);
        return String.valueOf(sistema.getId());
    }

    public void removerEmpregado(String id) throws Exception {
        Command comando = new RemoverEmpregadoCommand(this.sistema, id);
        this.invoker.executeCommand(comando);
    }

    public void lancaCartao(String id, String data, String horas) throws Exception {
        Command comando = new LancarCartaoCommand(this.sistema, id, data, horas);
        this.invoker.executeCommand(comando);
    }

    public void lancaVenda(String id, String data, String valor) throws Exception {
        Command comando = new LancarVendaCommand(this.sistema, id, data, valor);
        this.invoker.executeCommand(comando);
    }

    public void lancaTaxaServico(String id, String data, String valor) throws Exception {
        Command comando = new LancarTaxaServicoCommand(this.sistema, id, data, valor);
        this.invoker.executeCommand(comando);
    }

    public void alteraEmpregado(String id, String atributo, String valor1, String valor2, String valor3) throws Exception {
        Command comando = null;

        if (atributo.equalsIgnoreCase("nome")) {
            comando = new AlterarNomeCommand(this.sistema, id, atributo, valor1);

        } else if (atributo.equalsIgnoreCase("endereco")) {
            comando = new AlterarEnderecoCommand(this.sistema, id, atributo, valor1);

        } else if (atributo.equalsIgnoreCase("tipo")) {
            comando = new AlterarTipoCommand(this.sistema, id, atributo, valor1);

        } else if (atributo.equalsIgnoreCase("salario")) {
            comando = new AlterarSalarioCommand(this.sistema, id, atributo, valor1);

        } else if (atributo.equalsIgnoreCase("comissao")) {
            comando = new AlterarComissaoCommand(this.sistema, id, atributo, valor1);
        }
    }

    public void undo() throws Exception {
        this.invoker.undoLastCommand();
    }

    public void redo() throws Exception {
        this.invoker.redoLastCommand();
    }

    public String getAtributoEmpregado(String id, String atributo) throws Exception {
        return this.sistema.getAtributoEmpregado(id, atributo);
    }

    public void rodaFolha(String data) throws Exception {
        this.sistema.rodaFolha(data);
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

}