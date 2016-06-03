package ypan01.financify;

/**
 * Created by Yang on 5/23/2016.
 */
public class CategoryLabel {
    public String color;
    public String name;
    public double percent;
    public double amount;

    public CategoryLabel(String color, String name) {
        this.color = color;
        this.name = name;
    }

    public CategoryLabel(String color, String name, double percent, double amount) {
        this.color = color;
        this.name = name;
        this.percent = percent;
        this.amount = amount;
    }
}
