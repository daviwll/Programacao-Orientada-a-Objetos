package wepayu;

public class CriarEmpregadoCommand implements Command
{
    private  Sistema sistema;
    private String name;
    private String endereco;
    private String tipo;
    private String id;
    private String comissao;
    private String salario;

    public CriarEmpregadoCommand(Sistema sistema, String name, String endereco, String tipo, String salario)
    {
        this.sistema = sistema;
        this.name = name;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
    }

    public CriarEmpregadoCommand(Sistema sistema, String name, String endereco, String tipo, String salario, String comissao)
    {
        this.sistema = sistema;
        this.name = name;
        this.endereco = endereco;
        this.tipo = tipo;
        this.salario = salario;
        this.comissao = comissao;
    }

    @Override
    public void execute() throws Exception {
        if (this.comissao == null) {
            this.id = sistema.criarEmpregado(this.name, this.endereco, this.tipo, this.salario);
        } else {
            this.id = sistema.criarEmpregado(this.name, this.endereco, this.tipo, this.salario, this.comissao);
        }
    }

    @Override
    public void undo() throws Exception
    {
        sistema.removerEmpregado(this.id);
    }
}
