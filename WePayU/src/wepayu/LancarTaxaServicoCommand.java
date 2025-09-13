package wepayu;

import java.time.LocalDate;

public class LancarTaxaServicoCommand implements Command
{
    private Sistema sistema;
    private String id;
    private String data;
    private String valor;

    public LancarTaxaServicoCommand(Sistema sistema, String id,  String data, String valor)
    {
        this.sistema = sistema;
        this.id = id;
        this.data = data;
        this.valor = valor;
    }

    @Override
    public void execute()throws Exception
    {
        sistema.lancaTaxaServico(this.id, this.data, this.valor);
    }

    @Override
    public void undo() throws Exception
    {
        Empregado empregado = sistema.getEmpregado(this.id);
        empregado.getSindicato().removeTaxa();
    }
}
