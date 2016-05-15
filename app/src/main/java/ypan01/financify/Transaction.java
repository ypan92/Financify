package ypan01.financify;

import java.util.Date;

/**
 * Created by Yang on 5/14/2016.
 */
public class Transaction {
    public int userId;
    public int isDeposit;
    public double amount;
    public Date date;

    public Transaction() {
        userId = -1;
        amount = 0;
        date = null;
    }

    public Transaction(int isDeposit, double amount) {
        this.isDeposit = isDeposit;
        this.amount = amount;
    }

    public Transaction(int userId, int isDeposit, double amount) {
        this.userId = userId;
        this.isDeposit = isDeposit;
        this.amount = amount;
    }

    public Transaction(int userId, int isDeposit, double amount, Date date) {
        this.userId = userId;
        this.isDeposit = isDeposit;
        this.amount = amount;
        this.date = date;
    }
}
