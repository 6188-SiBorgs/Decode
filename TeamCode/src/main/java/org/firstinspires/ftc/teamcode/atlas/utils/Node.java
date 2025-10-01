package org.firstinspires.ftc.teamcode.atlas.utils;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public class Node {
    public double x, y;
    public double moveSpeed = 1;
    public double rotSpeed = 1;
    public boolean stop = false;
    public double r = Double.MAX_VALUE;

    public double h1x, h1y, h2x, h2y = Double.MAX_VALUE;

    public ArrayList<Runnable> runOnStart = new ArrayList<>();
    public ArrayList<Runnable> runOnTick = new ArrayList<>();
    public ArrayList<Callable<Boolean>> runBlocking = new ArrayList<>();

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
