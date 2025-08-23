import java.text.ParseException;
import java.util.Scanner;
import java.text.SimpleDateFormat;

public class Main
{
    public static void main(String[] args) throws ParseException {
        Scanner sc = new Scanner(System.in);

        while(true)
        {
            try
            {
                int roomNumber; 
                String checkIn, checkOut;
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy"); 
                System.out.print("Room number:");
                roomNumber = sc.nextInt();
                sc.nextLine();
                System.out.print("Check-in date (dd/MM/yyyy): ");
                checkIn = sc.nextLine();
                System.out.print("Check-out date (dd/MM/yyyy): ");
                checkOut = sc.nextLine();

                Reservation people = new Reservation(roomNumber, format.parse(checkIn), format.parse(checkOut));
                System.out.println("Reservation: Room " + people.getRoomNumber() + ", check-in: " + format.format((people.getCheckIn())) + 
                ", check-out: " + format.format((people.getCheckOut())) + ", " + people.duration() + " nights");
                
                System.out.println("\nEnter data to update the reservation:");
                System.out.print("Check-in date (dd/MM/yyyy): ");
                checkIn = sc.nextLine();
                System.out.print("Check-out date (dd/MM/yyyy): ");
                checkOut = sc.nextLine();
                people.updateDates(format.parse(checkIn), format.parse(checkOut));

                System.out.println("Reservation: Room " + people.getRoomNumber() + ", check-in: " + format.format((people.getCheckIn())) + 
                ", check-out: " + format.format((people.getCheckOut())) + ", " + people.duration() + " nights\n");
            }
                catch(IllegalArgumentException | ParseException e)
                {
                    System.out.println(e.getMessage());
                    break;
                    
                }
            
        }
        
        
    }
}