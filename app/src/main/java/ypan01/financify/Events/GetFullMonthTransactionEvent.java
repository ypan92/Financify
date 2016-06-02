package ypan01.financify.Events;

/**
 * Created by yangpan on 6/1/16.
 */
public class GetFullMonthTransactionEvent {

    private int month;
    private int year;

    public GetFullMonthTransactionEvent(int month, int year) {
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
