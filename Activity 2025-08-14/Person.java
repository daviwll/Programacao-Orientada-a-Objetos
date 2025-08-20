public abstract class Person {
    private String name;
    private double annualIncome;

    public abstract double getTax();

    public final void setName(String name)
    {
        this.name = name;
    }

    public final String getName()
    {
        return this.name;
    }

    public final void setAnnualIncome(double annualIncome)
    {
        this.annualIncome = annualIncome;
    }

    public final double getAnnualIncome()
    {
        return this.annualIncome;
    }   

}
