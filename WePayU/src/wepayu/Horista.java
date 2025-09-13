package wepayu;

import java.util.ArrayList;

public class Horista extends Empregado {
    private double salarioHora;
    private ArrayList <CartaoDePonto>  listaCartoes;

    public Horista(String name, String endereco, String id, double salarioHora, String tipo) {
        super(name, endereco, tipo, id);
        this.salarioHora = salarioHora;
        this.listaCartoes = new ArrayList<>();
    }

    public void addCartaoDePonto(CartaoDePonto cartao)
    {
        this.listaCartoes.add(cartao);
    }

    public void removeCartaoDePonto()
    {
        this.listaCartoes.remove(this.listaCartoes.size()-1);
    }

    public double getSalarioHora() {
        return salarioHora;
    }

    public void setSalarioHora(double salarioHora) {
        this.salarioHora = salarioHora;
    }

    public ArrayList <CartaoDePonto> getListaCartoes() {
        return listaCartoes;
    }



}
