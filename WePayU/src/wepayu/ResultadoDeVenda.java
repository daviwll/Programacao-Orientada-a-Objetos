package wepayu;

import java.time.LocalDate;

public class ResultadoDeVenda
{
    private LocalDate date;
    private double valor;

    public  ResultadoDeVenda(LocalDate date, double valor)
    {
        this.date = date;
        this.valor = valor;
    }

    public LocalDate getDate()
    {
        return date;
    }
    public void setDate(LocalDate date)
    {
        this.date = date;
    }
    public double getValor()
    {
        return valor;
    }
    public void setValor(double valor)
    {
        this.valor = valor;
    }
}
