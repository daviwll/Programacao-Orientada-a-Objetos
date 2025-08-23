import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Reservation {
    private int roomNumber;
    private Date checkIn, checkOut;

    public Date getCheckIn() {  
        return this.checkIn;
    }
    
    public Date getCheckOut() {
        return this.checkOut;
    }

    
    public int getRoomNumber()
    {
        return this.roomNumber; 
    }
    
    public Reservation(int roomNumber, Date checkIn, Date checkOut)
    {
        validateDates(checkIn, checkOut);
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    private void validateDates(Date checkIn, Date checkOut)
    {
        Date now = new Date();
        
        if(checkIn.before(now) || checkOut.before(now))
        {
            throw new IllegalArgumentException("Error in reservation: Reservation dates for update must be future dates");
        }
        if (!checkOut.after(checkIn)) {
            throw new IllegalArgumentException("Error in reservation: Check-out date must be after check-in date.");
        }
    }
    public int duration()
    {   
        int duration = (int) TimeUnit.MILLISECONDS.toDays(this.getCheckOut().getTime() - 
                         this.getCheckIn().getTime());
        return (int) duration;
    }

    public void updateDates(Date checkIn, Date checkOut)
    {
        validateDates(checkIn, checkOut);
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public void setRoomNumber(int roomNumber)
    {
        this.roomNumber = roomNumber;
    }

}
