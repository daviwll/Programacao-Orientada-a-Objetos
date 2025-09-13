package wepayu;

public class AlterarComissaoCommand  implements Command
{
    private Sistema sistema;
    private String atributo;
    private String valor;
    private String id;
    private double comissaoAntiga;

    public AlterarComissaoCommand(Sistema sistema, String id,  String atributo, String valor)
    {
        this.sistema = sistema;
        this.id = id;
        this.atributo = atributo;
        this.valor = valor;
    }

    @Override
    public void execute() throws Exception
    {
        this.comissaoAntiga = ((Comissionado) sistema.getEmpregado(this.id)).getComissao();
        sistema.alteraEmpregado(this.id, this.atributo, this.valor);
    }

    @Override
    public void undo() throws Exception
    {
        sistema.alteraEmpregado(this.id, this.atributo, String.valueOf(this.comissaoAntiga));
    }
}
