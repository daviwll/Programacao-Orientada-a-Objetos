package wepayu;

public class AlterarEnderecoCommand  implements Command
{
    private Sistema sistema;
    private String atributo;
    private String valor;
    private String id;
    private String enderecoAntigo;

    public AlterarEnderecoCommand(Sistema sistema, String id,  String atributo, String valor)
    {
        this.sistema = sistema;
        this.id = id;
        this.atributo = atributo;
        this.valor = valor;
    }


    @Override
    public void execute() throws Exception
    {
        this.enderecoAntigo = sistema.getEmpregado(this.id).getEndereco();
        sistema.alteraEmpregado(this.id, this.atributo, this.valor);
    }

    @Override
    public void undo() throws Exception
    {
        sistema.alteraEmpregado(this.id, this.atributo, this.enderecoAntigo);
    }
}
