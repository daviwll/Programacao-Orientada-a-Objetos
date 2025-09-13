package wepayu;

import java.util.ArrayList;

public class Comissionado extends Assalariado {
    private double comissao;
    private ArrayList<ResultadoDeVenda> listaVendass;

    public Comissionado(String name, String endereco, String id, double salarioMensal, double comissao, String tipo) {
        super(name, endereco, id, salarioMensal, tipo);
        this.comissao = comissao;
        this.listaVendass = new ArrayList();
    }

    public double getComissao() {
        return comissao;
    }

    public void setComissao(double comissao) {
        this.comissao = comissao;
    }

    public void addVenda(ResultadoDeVenda venda)
    {
        this.listaVendass.add(venda);
    }

    public void removeVenda()
    {
        this.listaVendass.remove(listaVendass.size() - 1);
    }
}
