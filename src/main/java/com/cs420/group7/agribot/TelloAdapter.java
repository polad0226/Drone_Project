package com.cs420.group7.agribot;

import main.java.surelyhuman.jdrone.Constants;
import main.java.surelyhuman.jdrone.control.physical.tello.TelloDrone;
import java.io.IOException;

public class TelloAdapter implements DroneFunction {

    private final TelloDrone tDrone;
    private final CommandCenter commandCenter;
    //private int rotation = 90;
    private int rotation = 0;
    private int locationX;
    private int locationY;
    private int locationZ = 100;


    public TelloAdapter(TelloDrone _tDrone, CommandCenter _commandCenter) throws IOException {
        this.tDrone = _tDrone;
        this.commandCenter = _commandCenter;
        tDrone.activateSDK();
    }

    @Override
    public void visitItem(AbstractItem item) throws IOException, InterruptedException {
        tDrone.activateSDK();
        if (item == null) {
            System.out.println("ERROR: Cannot visit null item.");
            return;
        }

        int startX = getCenterX(commandCenter);
        int startY = getCenterY(commandCenter);
        this.setLocationX(startX);
        this.setLocationY(startY);
        this.setLocationZ(item.getLocationZ());

        // Calculate the location at the center of the item
        int x = getCenterX(item);
        int y = getCenterY(item);

        tDrone.takeoff();
        tDrone.increaseAltitude(item.getHeight() + 20);

        rotateTo((int) Math.toDegrees(Math.atan2(y - this.getLocationY(), x - this.getLocationX())) - 90);
        moveTo(x, y);
        turnLeft(360);
        tDrone.hoverInPlace(3);
        rotateTo((int) Math.toDegrees(Math.atan2(startY - this.getLocationY(), startX - this.getLocationX())) - 90);
        moveTo(startX, startY);
        rotateTo(0);

        tDrone.land();
        tDrone.end();
    }

    @Override
    public void scanFarm(AbstractItem commandCenter) throws IOException, InterruptedException {
        tDrone.activateSDK();
        int startX = getCenterX(commandCenter);
        int startY = getCenterY(commandCenter);
        this.setLocationX(startX);
        this.setLocationY(startY);
        this.setRotation(rotation);

        tDrone.takeoff();
        tDrone.increaseAltitude(this.getLocationZ() + 20);

        // Move the drone to the top left corner
        rotateTo((int) Math.toDegrees(Math.atan2(- this.getLocationY(), - this.getLocationX())) - 90);
        moveTo(0, 0);
        rotateTo((int) Math.toDegrees(Math.atan2(800 - this.getLocationY(), - this.getLocationX())) - 90);

        final int numIterations = 2;
        int verticalAmount = ((MainController.FARM_HEIGHT - 10) / 50) * Constants.CENTIMETERS_PER_MODEL_FOOT;
        int horizontalStep = (((MainController.FARM_WIDTH - 10) / 50) * Constants.CENTIMETERS_PER_MODEL_FOOT) / 3;

        // Begin the farm scanning
        for (int i = 0; i < numIterations; i++) {
            moveForward(verticalAmount);
            this.setLocationY(this.getLocationY() + verticalAmount);
            turnLeft(90);
            moveForward(horizontalStep);
            this.setLocationX(this.getLocationX() + horizontalStep);
            turnLeft(90);
            moveForward(verticalAmount);
            this.setLocationY(this.getLocationY() - verticalAmount);
            if (i == numIterations - 1) break;
            turnRight(90);
            moveForward(horizontalStep);
            this.setLocationX(this.getLocationX() + horizontalStep);
            turnRight(90);
        }

        // Return to command center
        int x = getCenterX(commandCenter);
        int y = getCenterY(commandCenter);
        rotateTo((int) Math.toDegrees(Math.atan2(y - this.getLocationY(), x - this.getLocationX())) - 90);
        moveTo(x, y);
        rotateTo(0);

        tDrone.land();
        tDrone.end();
    }

    @Override
    public void moveTo(int x, int y) throws IOException {
        double distance = Math.sqrt(Math.pow(getLocationX() - x, 2) + Math.pow(getLocationY() - y, 2));
        distance = pixelConversion((int) distance);
        this.moveForward((int) distance);
        this.setLocationX(x);
        this.setLocationY(y);
    }

    @Override
    public void rotateTo(int degrees) throws IOException {
        System.out.println(degrees);
        while (degrees < 0) {
            degrees += 360;
        }
        while (degrees > 360) {
            degrees -= 360;
        }

        int distance = degrees - this.getRotation();

        if (distance > 0) {

            if (distance <= 180) {
                tDrone.turnCW(distance);
            } else if (distance > 180) {
                tDrone.turnCCW(Math.abs(360 - distance));
            }
        } else if (distance < 0) {
            if (distance <= -180) {
                distance += 360;
                if (distance > 180) {
                    tDrone.turnCCW(Math.abs(distance - 360));
                } else {
                    tDrone.turnCW(distance);
                }
            } else if (distance > 180) {
                tDrone.turnCCW(Math.abs(distance - 360));
            } else {
                tDrone.turnCCW(Math.abs(distance));
            }
        }

        this.setRotation(degrees);
    }
    
    @Override
    public void moveForward(int amount) throws IOException {
        tDrone.flyForward(amount);
    }


    @Override
    public void turnLeft() throws IOException {
        tDrone.turnCCW(90);
    }

    public void turnLeft(int degrees) throws IOException {
        tDrone.turnCCW(degrees);
        this.setRotation((this.getRotation() - degrees) % 360);
    }

    @Override
    public void turnRight() throws IOException {
        tDrone.turnCW(90);
    }

    public void turnRight(int degrees) throws IOException {
        tDrone.turnCW(degrees);
        this.setRotation((this.getRotation() + degrees) % 360);
    }

    public int getCenterX(AbstractItem item){
        int droneWidth = 50;
        return item.getLocationX() + ((item.getWidth() / 2) - (droneWidth / 2));
    }

    public int getCenterY(AbstractItem item){
        int droneLength = 50;
        return item.getLocationY() + ((item.getLength() / 2) - (droneLength / 2));
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public int getLocationZ() {
        return locationZ;
    }

    public void setLocationZ(int locationZ) {
        this.locationZ = locationZ;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int pixelConversion(int pixels) {
        return (pixels / 50) * Constants.CENTIMETERS_PER_MODEL_FOOT;
    }

}
