package ypan01.financify.Events;

import android.widget.TextView;

import ypan01.financify.Holograph.BarGraph;

public class SendMonthBalanceEvent {
    private double withdrawTotal;
    private double depositTotal;
    private int month;
    private int year;
    public TextView monthTotal;
    public BarGraph monthGraph;

    public SendMonthBalanceEvent(double withdraw, double deposit, int month, int year, TextView monthTotal, BarGraph monthGraph) {
        withdrawTotal = withdraw;
        depositTotal = deposit;
        this.month = month;
        this.year = year;
        this.monthTotal = monthTotal;
        this.monthGraph = monthGraph;
    }

    public double getWithdrawTotal() {
        return withdrawTotal;
    }

    public double getDepositTotal() {
        return depositTotal;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
