package com.cs420.group7.agribot;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;

public abstract class AbstractItem {
    private String name;
    private int locationX = 100;
    private int locationY = 100;
    private int locationZ = 0;
    private int width = 100;
    private int height = 100;
    private int length = 100;
    private int price = 100;
    private int rotation = 0;

    public AbstractItem(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
        refreshNode();
    }

    public String getName() {
        return name;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
        refreshNode();
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
        refreshNode();
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationZ(int locationZ) {
        this.locationZ = locationZ;
        refreshNode();
    }

    public int getLocationZ() {
        return locationZ;
    }

    public void setWidth(int width) {
        this.width = width;
        refreshNode();
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
        refreshNode();
    }

    public int getHeight() {
        return height;
    }

    public void setLength(int length) {
        this.length = length;
        refreshNode();
    }

    public int getLength() {
        return length;
    }

    public void setPrice(int price) {
        this.price = price;
        refreshNode();
    }

    public int getPrice() {
        return price;
    }

    public void setRotation(int degrees) {
        while (degrees < -360) {
            degrees += 360;
        }
        while (degrees > 360) {
            degrees -= 360;
        }
        this.rotation = degrees;
        refreshNode();
    }

    public int getRotation() {
        return rotation;
    }

    /**
     * Gets the parent instance of the item.
     * @return A parent item.
     */
    public abstract AbstractItem getParent();

    /**
     * Gets the JavaFX tree item component to be displayed in the items tree view.
     * @return A JavaFX Tree Item.
     */
    public abstract TreeItem<AbstractItem> getTreeItem();

    /**
     * Gets the JavaFX node component to be rendered.
     * @return A JavaFX Scene Node.
     */
    public abstract Node getNode();

    /**
     * Refreshes the node component to reflect all class attributes.
     */
    protected abstract void refreshNode();

    /**
     * Accepts a visitor.
     * @param _vis
     */
    public abstract void accept(Visitor _vis);
}
