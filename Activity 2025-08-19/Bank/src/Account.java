/**
 *
 * @author davi
 */

public class Account {
    private int number;
    private String holder;
    private double balance;
    private double withdrawLimit;
       
    public Account(int number, String holder, double balance, double withdrawLimit)
    {
        this.number = number;
        this.holder = holder;
        this.balance = balance;
        this.withdrawLimit = withdrawLimit;
    }

    public double getBalance() {
        return balance;
    }

    public String getHolder() {
        return holder;
    }

    public int getNumber() {
        return number;
    }

    public double getWithdrawLimit() {
        return withdrawLimit;
    }

    
    public void setHolder(String holder) {
        this.holder = holder;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setWithdrawLimit(double withdrawLimit) {
        this.withdrawLimit = withdrawLimit;
    }
    
    public void deposit(double amount)
    {
        if(amount <= 0)
        {
            throw new IllegalArgumentException("Invalid deposit value");
        }
        else
        {
            setBalance(this.balance + amount);
        } 
    }
    
    public void withdraw(double amount) throws Exception    
    {
        if(amount > this.balance)
        {
            throw new Exception("Withdraw error: Not enough balance");
        }
        else if(this.withdrawLimit < amount)
        {
            throw new Exception("Withdraw error: The amount exceeds withdraw limit");
        }
        else
        {
            this.balance -= amount;
        }
    }

}
