public class Company extends Person {
    private int numberEmployees;

    public Company(String name, double annualIncome, int numberEmployees)
    {
        setAnnualIncome(annualIncome);
        setName(name);
        setNumberEmployees(numberEmployees);
    }

    public int getNumberEmployees() {
        return this.numberEmployees;
    }
    public void setNumberEmployees(int numberEmployees) {
        this.numberEmployees = numberEmployees;
    }

    @Override
    public double getTax() {
        double totalValue = 0;
        if(getNumberEmployees() <= 10)
        {
            totalValue += getAnnualIncome() * 0.16;
        }
        else
        {
            totalValue += getAnnualIncome() * 0.14;
        }

        return totalValue;
    }
}
