package com.cs420.group7.agribot;

public class MarketVisitor implements Visitor{
    private int marketValue;

    public MarketVisitor() {
        marketValue = 0;
    }

    public int marketValue() {
        return marketValue;
    }

    @Override
    public void visit(Item _item){
        marketValue += _item.getMarketValue();
    }

    @Override
    public void visit(ItemContainer _itemContainer) {
        for (AbstractItem item : _itemContainer.getItems()) {
            if (item instanceof Item) {
                marketValue += ((Item) item).getMarketValue();
            } else {
                // DFS into children
                visit((ItemContainer) item);
            }
        }
    }
}
