package ypan01.financify;

import java.sql.Date;

/**
 * Created by Yang on 5/14/2016.
 */
public class Transaction {
    public int transactionId;
    public int userId;
    public int isDeposit;
    public double amount;
    public Date date;
    public int category;

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

    public Transaction(int isDeposit, double amount, Date date) {
        this.isDeposit = isDeposit;
        this.amount = amount;
        this.date = date;
    }

    public Transaction(int transactionId, int isDeposit, double amount, int category) {
        this.transactionId = transactionId;
        this.isDeposit = isDeposit;
        this.amount = amount;
        this.category = category;
    }

    public Transaction(int transactionId, int isDeposit, double amount, Date date, int category) {
        this.transactionId = transactionId;
        this.isDeposit = isDeposit;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public String getCategoryColorHex() {
        switch (category) {
            case 0:
                return "#1188AA";
            case 1:
                return "#FFBB33";
            case 2:
                return "#AA66CC";
            case 3:
                return "#77DD11";
            case 4:
                return "#33AA55";
            case 5:
                return "#DD00FF";
            case 6:
                return "#99CC00";
            default:
                return "#1188AA";
        }
    }

    public String getCategoryName() {
        switch(category) {
            case 0:
                return "Uncategorized";
            case 1:
                return "Food";
            case 2:
                return "Gas";
            case 3:
                return "Clothes";
            case 4:
                return "Technology";
            case 5:
                return "Kitchen Hardware";
            case 6:
                return "Furniture";
            default:
                return "Invalid";
        }
    }
}
