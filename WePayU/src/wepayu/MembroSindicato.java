package wepayu;

import java.util.ArrayList;

public class MembroSindicato
{
    private String idMembro;
    private double taxaSindical;
    private ArrayList<TaxaServico> totalTaxas;

    public  MembroSindicato(String idMembro, double taxaSindical)
    {
        this.idMembro = idMembro;
        this.taxaSindical = taxaSindical;
        this.totalTaxas = new ArrayList<>();
    }

    public void addTaxa(TaxaServico taxa)
    {
        this.totalTaxas.add(taxa);
    }

    public void removeTaxa()
    {
        this.totalTaxas.remove(this.totalTaxas.size()-1);

    }

    public String getIdMembro()
    {
        return idMembro;
    }

    public double getTaxaSindical()
    {
        return taxaSindical;
    }

    public void setTaxaSindical(double taxaSindical)
    {
        this.taxaSindical = taxaSindical;
    }

    public void setIdMembro(String idMembro)
    {
        this.idMembro = idMembro;
    }

    public ArrayList<TaxaServico> getTotalTaxas()
    {
        return totalTaxas;
    }


}
