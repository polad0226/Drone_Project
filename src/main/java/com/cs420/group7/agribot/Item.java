package com.cs420.group7.agribot;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Item extends AbstractItem {
    private ItemContainer parent;
    private ItemNode node = new ItemNode();
    private TreeItem<AbstractItem> treeItem = new TreeItem<>(this);
    private int marketValue = 100;

    public Item(String name) {
        super(name);
        this.refreshNode();
    }

    public Item(String name, ItemContainer parent) {
        super(name);
        this.parent = parent;
        this.refreshNode();
    }

    public void setMarketValue(int marketValue) {
        this.marketValue = marketValue;
    }

    public int getMarketValue() {
        return marketValue;
    }

    @Override
    public AbstractItem getParent() {
        return parent;
    }

    @Override
    public TreeItem<AbstractItem> getTreeItem() {
        return treeItem;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    protected void refreshNode() {
        if (node == null) return;
        node.label.setText(getName());
        node.setLayoutX(getLocationX());
        node.setLayoutY(getLocationY());
        node.imageView.setFitWidth(getWidth());
        node.imageView.setFitHeight(getLength());
        node.setRotate(getRotation());
    }

    @Override
    public String toString(){
        return getName();
    }

    @Override
    public void accept(Visitor _vis) { _vis.visit(this); }
}

class ItemNode extends StackPane {
    public ImageView imageView = new ImageView(new Image("file:src/box.png"));
    public Label label = new Label();

    public ItemNode() {
        label.setTranslateY(4);
        getChildren().add(label);
        getChildren().add(imageView);
        setAlignment(Pos.TOP_CENTER);
    }
}