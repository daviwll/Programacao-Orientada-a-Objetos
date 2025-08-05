import java.util.Scanner;

public class Main
{
	public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product data; \nName: ");
        String name = sc.nextLine();
        System.out.print("Price: ");
        double price = sc.nextDouble();
        System.out.print("Quantity in stock: ");
        int quantity = sc.nextInt();
        
        Product p1 = new Product(name, price, quantity);
        
        System.out.println("Product data: " + p1.getName() + ", $ " + String.format("%.2f",p1.getPrice()) 
        + " " + p1.getQuantity() + " units, Total: $" + String.format("%.2f",p1.totalValueInStock()));
        
        System.out.print("Enter the number of produts to be added in stock: ");
        quantity = sc.nextInt();
        p1.addProducts(quantity);
        System.out.println("Updated data: " + p1.getName() + ", $ " + 
        String.format("%.2f", p1.getPrice()) + " " +  p1.getQuantity() 
        + " units, Total: $" + String.format("%.2f",p1.totalValueInStock()));
        
        System.out.print("Enter the number of produts to be removed from stock: ");
        quantity = sc.nextInt();
        p1.removeProducts(quantity);
        System.out.print("Updated data: " + p1.getName() + ", $ " + String.format("%.2f", p1.getPrice()) +
        " " + p1.getQuantity() + " units, Total: $" + String.format("%.2f",p1.totalValueInStock()));
	}
}
