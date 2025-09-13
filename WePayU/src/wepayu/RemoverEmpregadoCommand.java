package wepayu;

public class RemoverEmpregadoCommand implements Command
{
    private  Sistema sistema;
    private Empregado empregado;
    private String id;


    public RemoverEmpregadoCommand(Sistema sistema, String id)
    {
        this.sistema = sistema;
        this.id = id;
    }

    @Override
    public void execute() throws Exception
    {
        this.empregado = sistema.getEmpregado(this.id);
        this.sistema.removerEmpregado(this.id);
    }

    @Override
    public void undo() throws Exception
    {
        String name = this.empregado.getName();
        String endereco = this.empregado.getEndereco();
        String tipo = this.empregado.getTipo();

        if (this.empregado instanceof Comissionado)
        {
            Comissionado comissionado = (Comissionado) this.empregado;
            String salario = String.valueOf(comissionado.getSalarioMensal());
            String comissao = String.valueOf(comissionado.getComissao());
            sistema.criarEmpregado(name, endereco, tipo, salario, comissao);

        }
        else if (this.empregado instanceof Assalariado)
        {
            Assalariado assalariado = (Assalariado) this.empregado;
            String salario = String.valueOf(assalariado.getSalarioMensal());
            sistema.criarEmpregado(name, endereco, tipo, salario);

        }
        else if (this.empregado instanceof Horista)
        {
            Horista horista = (Horista) this.empregado;
            String salario = String.valueOf(horista.getSalarioHora());
            sistema.criarEmpregado(name, endereco, tipo, salario);
        }
    }
}

