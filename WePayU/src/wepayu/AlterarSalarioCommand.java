package wepayu;

public class AlterarSalarioCommand  implements Command
{
    private Sistema sistema;
    private String atributo;
    private String valor;
    private String id;
    private double salarioAntigo;

    public AlterarSalarioCommand(Sistema sistema, String id,  String atributo, String valor)
    {
        this.sistema = sistema;
        this.id = id;
        this.atributo = atributo;
        this.valor = valor;
    }

    @Override
    public void execute() throws  Exception
    {
        Empregado empregado = sistema.getEmpregado(this.id);

        if(empregado.getTipo().equals("comissionado") || empregado.getTipo().equals("assalariado"))
        {
            this.salarioAntigo = ((Assalariado) empregado).getSalarioMensal();
        }
        else if(empregado.getTipo().equals("horista"))
        {
            this.salarioAntigo = ((Horista) empregado).getSalarioHora();
        }
        sistema.alteraEmpregado(this.id, this.atributo, this.valor);
    }

    @Override
    public void undo() throws Exception
    {
        sistema.alteraEmpregado(this.id, this.atributo, String.valueOf(this.salarioAntigo));
    }
}
