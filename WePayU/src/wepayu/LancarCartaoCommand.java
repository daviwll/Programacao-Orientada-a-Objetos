package wepayu;

public class LancarCartaoCommand implements Command
{
    private  Sistema sistema;
    private String id;
    private String data;
    private String horas;

    public LancarCartaoCommand(Sistema sistema, String id, String data, String horas)
    {
        this.sistema = sistema;
        this.id = id;
        this.data = data;
        this.horas = horas;
    }

    @Override
    public void execute() throws Exception
    {
        this.sistema.lancaCartao(this.id, this.data, this.horas);
    }
    @Override
    public void undo() throws Exception
    {
        Empregado empregado = sistema.getEmpregado(this.id);
        ((Horista) empregado).removeCartaoDePonto();
    }
}
