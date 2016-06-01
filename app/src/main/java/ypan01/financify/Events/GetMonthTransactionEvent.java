package ypan01.financify.Events;


import android.widget.TextView;

import ypan01.financify.Holograph.BarGraph;

public class GetMonthTransactionEvent {
    private int month;
    private int year;
    public TextView monthTotal;
    public BarGraph monthGraph;

    public GetMonthTransactionEvent(int month, int year, TextView monthTotal, BarGraph monthGraph) {
        this.month = month;
        this.year = year;
        this.monthTotal = monthTotal;
        this.monthGraph = monthGraph;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
