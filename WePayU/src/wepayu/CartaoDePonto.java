package wepayu;

import java.time.LocalDate;

public class CartaoDePonto
{
    private LocalDate data;
    private double horas;


    public CartaoDePonto(LocalDate data, double horas)
    {
        this.data = data;
        this.horas = horas;
    }

    public LocalDate getData()
    {
        return data;
    }
    public void setData(LocalDate data)
    {
        this.data = data;
    }
    public double getHoras()
    {
        return horas;
    }
    public void setHoras(double horas)
    {
        this.horas = horas;
    }
}
