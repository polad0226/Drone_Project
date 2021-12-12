package com.cs420.group7.agribot;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;

public class ItemContainer extends AbstractItem {
    private ItemContainer parent;
    private ItemContainerNode node = new ItemContainerNode();
    private TreeItem<AbstractItem> treeItem = new TreeItem<>(this);
    // Can contain any child of AbstractItem
    private ArrayList<AbstractItem> items = new ArrayList<>();

    public ItemContainer(String name) {
        super(name);
        this.treeItem.setExpanded(true);
        refreshNode();
    }

    public ItemContainer(String name, ItemContainer parent) {
        super(name);
        this.parent = parent;
        this.treeItem.setExpanded(true);
        refreshNode();
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

    /**
     * Gets all the child items of the container.
     * @return An array of AbstractItem.
     */
    public AbstractItem[] getItems() {
        return items.toArray(new AbstractItem[items.size()]);
    }

    /**
     * Adds a child item to the container.
     * @param item The item to add.
     */
    public void addItem(AbstractItem item){
        items.add(item);
        treeItem.getChildren().add(item.getTreeItem());
    }

    /**
     * Deletes a child item from the container.
     * @param item The item to delete.
     */
    public void deleteItem(AbstractItem item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(item)) {
                treeItem.getChildren().remove(i);
                items.remove(i);
            }
        }
    }

    @Override
    public String toString(){
        return getName();
    }

    @Override
    public void accept(Visitor _vis) { _vis.visit(this); }
}

class ItemContainerNode extends StackPane {
    public ImageView imageView = new ImageView(new Image("file:src/box.png"));
    public Label label = new Label();

    public ItemContainerNode() {
        label.setTranslateY(4);
        getChildren().add(label);
        getChildren().add(imageView);
        setAlignment(Pos.TOP_CENTER);
    }
}
