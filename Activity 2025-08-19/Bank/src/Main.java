import java.util.Scanner;
/**
 *
 * @author davi
 */
public class Main {
    public static void main(String[] args) {
        int number;
        String holder;
        double balance, withdrawLimit;
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter account data\nNumber: ");
        number = sc.nextInt();
        System.out.print("Holder: ");
        holder = sc.nextLine();
        holder = sc.nextLine();
        System.out.print("Initial balance: ");
        balance = sc.nextDouble();
        System.out.print("Withdraw limit: ");
        withdrawLimit = sc.nextDouble();
        
        Account account = new Account(number, holder, balance, withdrawLimit);
        double amount;
        
        System.out.print("\n\nEnter amount for withdraw: ");
        amount = sc.nextDouble();
        
        try 
        {
            account.withdraw(amount);
            System.out.println("New balance: " + String.format("%.2f", account.getBalance()));
        } 
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
