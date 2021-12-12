package com.cs420.group7.agribot;

public class PriceVisitor implements Visitor{
    private int purchasePrice;
    private int marketValue;

    public PriceVisitor() {
        purchasePrice = 0;
    }

    public int purchasePrice() {
        return purchasePrice;
    }

    @Override
    public void visit(Item _item){
        purchasePrice += _item.getPrice();
    }

    @Override
    public void visit(ItemContainer _itemContainer) {
        purchasePrice += _itemContainer.getPrice();
        for (AbstractItem item : _itemContainer.getItems()) {
            if (item instanceof Item) {
                purchasePrice += item.getPrice();
            } else {
                // DFS into children
                visit((ItemContainer) item);
            }
        }
    }
}
