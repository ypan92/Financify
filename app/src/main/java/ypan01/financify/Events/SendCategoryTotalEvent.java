package ypan01.financify.Events;

public class SendCategoryTotalEvent {

    public double uncategorizedTotal;
    public double foodTotal;
    public double gasTotal;
    public double clothesTotal;
    public double techTotal;
    public double kitchenTotal;
    public double furnitureTotal;
    public double total;

    public SendCategoryTotalEvent() {}

    public SendCategoryTotalEvent(int uncategorized, int food, int gas,
      int clothes, int tech, int kitchen, int furniture, int total) {
        uncategorizedTotal = uncategorized;
        foodTotal = food;
        gasTotal = gas;
        clothesTotal = clothes;
        techTotal = tech;
        kitchenTotal = kitchen;
        furnitureTotal = furniture;
        this.total = total;
    }

}
