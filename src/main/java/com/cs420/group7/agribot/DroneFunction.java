package com.cs420.group7.agribot;

import java.io.IOException;

public interface DroneFunction {
    void visitItem(AbstractItem item) throws IOException, InterruptedException;
    void scanFarm(AbstractItem commandCenter) throws IOException, InterruptedException;
    void moveTo(int x, int y) throws IOException;
    void rotateTo(int degrees) throws IOException;
    void moveForward(int amount) throws IOException;
    void turnLeft() throws IOException;
    void turnRight() throws IOException;
}
