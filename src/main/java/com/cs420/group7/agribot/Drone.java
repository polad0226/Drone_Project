package com.cs420.group7.agribot;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.io.IOException;

public class Drone extends Item implements DroneFunction {
    private DroneNode node = new DroneNode();
    SequentialTransition transitionQueue = new SequentialTransition();
    private final double MOVE_SPEED = 6;
    private final double ROTATION_SPEED = 7;

    // Constructor
    public Drone(String _name){
        super(_name);
        setRotation(90);
    }

    public Drone(String _name, ItemContainer _parent){
        super(_name, _parent);
        setRotation(90);
    }

    // Optional Constructor if Command Center has not been created.
    public Drone(String _name, int _locationX, int _locationY){
        super(_name);
        setLocationX(_locationX);
        setLocationY(_locationY);
        setRotation(90);
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    protected void refreshNode() {
        if (node == null) return;
        node.setLayoutX(getLocationX());
        node.setLayoutY(getLocationY());
        node.setFitWidth(getWidth());
        node.setFitHeight(getLength());
        node.setRotate(getRotation());
    }

    /**
     * Animates the drone flying to the given item's location.
     * @param item The item to visit.
     */
    @Override
    public void visitItem(AbstractItem item) {
        // Ensure all reference variables are valid
        if (item == null) {
            System.out.println("ERROR: Cannot visit null item.");
            return;
        }

        // Stop any ongoing animation
        stopAllDroneAnimations();

        // Setup for animation method calls
        transitionQueue = new SequentialTransition();
        transitionQueue.setCycleCount(1);
        transitionQueue.setOnFinished(actionEvent -> refreshDroneState());
        int startX = this.getLocationX();
        int startY = this.getLocationY();
        int startRotation = this.getRotation();

        // Calculate the location at the center of the item
        int x = item.getLocationX() + (item.getWidth() / 2) - (this.getWidth() / 2);
        int y = item.getLocationY() + (item.getLength() / 2) - (this.getLength() / 2);

        rotateTo((int) Math.toDegrees(Math.atan2(y - this.getLocationY(), x - this.getLocationX())));
        moveTo(x, y);
        rotateTo(90);

        // Begin the animation
        this.setLocationX(startX);
        this.setLocationY(startY);
        this.setRotation(startRotation);
        transitionQueue.play();
    }

    /**
     * Animates the drone to fly across the entire farm area.
     * The drone returns to the command center at the end of the scan.
     */
    @Override
    public void scanFarm(AbstractItem commandCenter) {
        // Stop any ongoing animation
        stopAllDroneAnimations();

        // Setup for animation method calls
        transitionQueue = new SequentialTransition();
        transitionQueue.setCycleCount(1);
        transitionQueue.setOnFinished(actionEvent -> refreshDroneState());
        int startX = this.getLocationX();
        int startY = this.getLocationY();
        int startRotation = this.getRotation();

        // Move the drone to the top left corner
        rotateTo((int) Math.toDegrees(Math.atan2(- this.getLocationY(), - this.getLocationX())));
        moveTo(0, 0);
        rotateTo(90);

        final int verticalAmount = MainController.FARM_HEIGHT - this.getLength();
        final int horizontalStep = 50;
        final int numIterations = 6;

        // Begin the farm scanning
        for (int i = 0; i < numIterations; i++) {
            moveForward(verticalAmount);
            turnLeft();
            moveForward(horizontalStep);
            turnLeft();
            moveForward(verticalAmount);
            if (i == numIterations - 1) break;
            turnRight();
            moveForward(horizontalStep);
            turnRight();
        }

        // Return to command center
        int x = commandCenter.getLocationX() + (commandCenter.getWidth() / 2) - (this.getWidth() / 2);
        int y = commandCenter.getLocationY() + (commandCenter.getLength() / 2) - (this.getLength() / 2);
        rotateTo((int) Math.toDegrees(Math.atan2(y - this.getLocationY(), x - this.getLocationX())));
        moveTo(x, y);
        rotateTo(90);

        // Begin the animation
        this.setLocationX(startX);
        this.setLocationY(startY);
        this.setRotation(startRotation);
        transitionQueue.play();
    }

    @Override
    public void moveTo(int x, int y) {
        KeyValue kvx = new KeyValue(this.getNode().layoutXProperty(), x);
        KeyValue kvy = new KeyValue(this.getNode().layoutYProperty(), y);

        double distance = Math.sqrt(Math.pow(getLocationX() - x, 2) + Math.pow(getLocationY() - y, 2));
        KeyFrame kf = new KeyFrame(Duration.seconds(Math.pow(distance, 0.5) * MOVE_SPEED / 100), kvx, kvy);

        Timeline timeline = new Timeline(kf);

        SequentialTransition transition = new SequentialTransition(timeline);
        transition.setOnFinished(actionEvent -> refreshDroneState());
        transitionQueue.getChildren().add(transition);

        // Update layout for future animations added to the transition queue
        this.setLocationX(x);
        this.setLocationY(y);
    }

    @Override
    public void rotateTo(int degrees) {
        while (degrees < -360) {
            degrees += 360;
        }
        while (degrees > 360) {
            degrees -= 360;
        }

        KeyValue kv = new KeyValue(this.getNode().rotateProperty(), degrees);

        double distance = Math.abs(getRotation() - degrees);
        KeyFrame kf = new KeyFrame(Duration.seconds(Math.pow(distance, 0.5) * ROTATION_SPEED / 100), kv);

        Timeline timeline = new Timeline(kf);

        SequentialTransition transition = new SequentialTransition(timeline);
        transition.setOnFinished(actionEvent -> refreshDroneState());
        transitionQueue.getChildren().add(transition);

        // Update rotation for future animations added to the transition queue
        this.setRotation(degrees);
    }

    @Override
    public void moveForward(int amount) {
        double angle = this.getRotation();
        double amountX = Math.cos(Math.toRadians(angle)) * amount;
        double amountY = Math.sin(Math.toRadians(angle)) * amount;
        moveTo(this.getLocationX() + (int) amountX, this.getLocationY() + (int) amountY);
    }

    @Override
    public void turnLeft() {
        rotateTo(this.getRotation() - 90);
    }

    @Override
    public void turnRight() {
        rotateTo(this.getRotation() + 90);
    }

    /**
     * Refreshes the drone's state to match its UI state.
     */
    private void refreshDroneState() {
        if (this == null || this.getNode() == null) return;
        int newLayoutX = (int) this.getNode().getLayoutX();
        int newLayoutY = (int) this.getNode().getLayoutY();
        int newRotation = (int) this.getNode().getRotate();
        this.setRotation(newRotation);
        this.setLocationX(newLayoutX);
        this.setLocationY(newLayoutY);
    }

    /**
     * Stops all active drone animations in the transition queue.
     */
    private void stopAllDroneAnimations() {
        // Save the current state of the drone node
        refreshDroneState();
        // Stop the transition queue
        transitionQueue.stop();
        transitionQueue.getChildren().clear();
    }
}

class DroneNode extends ImageView {
    public DroneNode() {
        setImage(new Image("file:src/drone.png"));
    }
}
