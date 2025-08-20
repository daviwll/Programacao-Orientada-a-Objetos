import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

    Scanner sc = new Scanner(System.in);
    System.out.print("Enter the number of tax payers: ");
    int n = sc.nextInt();
    List <Person> totalPerson = new ArrayList<Person>(n);

    for(int i = 0; i < n; i++)
    {
        System.out.print("Tax payer #" + (i + 1) + " data: \n" + 
        "Individual or Company (i/c)? ");

        String personType = sc.next();
        
        if(personType.equals("i"))
        {
            System.out.print("Name: ");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.print("Annual Income: "); 
            double annualIncome = sc.nextDouble();
            System.out.print("Health expenditures: ");
            double healthExpenses = sc.nextDouble();
            Individual p = new Individual(name, annualIncome, healthExpenses);
            totalPerson.add(p);
            
        }
        else if(personType.equals("c"))
        {
            System.out.print("Name: ");
            sc.nextLine();
            String name = sc.nextLine();
            System.out.print("Annual Income: ");
            double annualIncome = sc.nextDouble();
            System.out.print("Number of Employees: ");
            int numberEmployees = sc.nextInt();
            Company p = new Company(name, annualIncome, numberEmployees);
            totalPerson.add(p);
            
        }
    }
    double totalTaxes = 0;

    System.out.println("\nTAXES PAID:");
    for(int i = 0; i < n; i++)
    {
        double tax = totalPerson.get(i).getTax();
        totalTaxes += tax;
        System.out.println(totalPerson.get(i).getName() + ": $ " + 
        String.format("%.2f", tax));   
    }
    System.out.print("\nTOTAL TAXES:" + " $ " + String.format("%.2f", totalTaxes));
    sc.close();
    }

}