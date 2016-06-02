package ypan01.financify.Events;


public class GetMonthCategoryTotalEvent {

    private int month;
    private int year;

    public GetMonthCategoryTotalEvent(int month, int year) {
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
