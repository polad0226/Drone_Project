package com.cs420.group7.agribot;

public interface Visitor {
    void visit(Item _item);
    void visit(ItemContainer _itemContainer);
}
