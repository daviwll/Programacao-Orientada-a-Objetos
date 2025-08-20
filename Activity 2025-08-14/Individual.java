public class Individual extends Person{
    private double healthExpenses;

    public Individual(String name, double annualIncome, double healthExpenses)
    {
        setName(name);
        setAnnualIncome(annualIncome);
        setHealthExpenses(healthExpenses);
    }
 
    public double getHealthExpenses() {
        return this.healthExpenses;
    }

    public void setHealthExpenses(double healthExpenses) {
        this.healthExpenses = healthExpenses;
    }

    @Override
    public double getTax() {
        double totalValue = 0; 

        if(getAnnualIncome() < 20000)
        {
            totalValue += getAnnualIncome() * 0.15;
            totalValue -= getHealthExpenses() * 0.5;
        }
        else
        {
            totalValue += getAnnualIncome() * 0.25;
            totalValue -= getHealthExpenses() * 0.5;
        }
        return totalValue;
        
    }
}
