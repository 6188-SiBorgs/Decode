package org.firstinspires.ftc.teamcode.atlas.atlasauto;


import android.telecom.Call;

import org.firstinspires.ftc.teamcode.atlas.utils.BezierUtils;
import org.firstinspires.ftc.teamcode.atlas.utils.Node;
import org.firstinspires.ftc.teamcode.atlas.utils.Vector2;
import org.firstinspires.ftc.teamcode.atlas.utils.Waypoint;

import java.util.ArrayList;
import java.util.concurrent.Callable;

public class AtlasPathBuilder {
    private final ArrayList<Node> nodes = new ArrayList<>();
    private double rotation = 0;
    private final AtlasAutoOp opMode;
    public AtlasPathBuilder(AtlasAutoOp opMode, double x, double y, double r) {

        this.opMode = opMode;

        Node initial = new Node(x, y);
        rotation = r;
        nodes.add(initial);
    }

    public void perform() {
        if (nodes.size() < 2) return;
        opMode.canSleep = false;

        opMode.telemetry.addLine(String.format("============= Atlas ============="));
        opMode.telemetry.addLine(String.format("Building waypoints from %d nodes", nodes.size()));
        opMode.telemetry.addLine(String.format("================================="));
        opMode.telemetry.update();
        long timer = System.currentTimeMillis();
        ArrayList<Waypoint> waypoints = new ArrayList<>();
        Node first = nodes.get(0);
        Waypoint firstWaypoint = new Waypoint(first.x, first.y);
        firstWaypoint.onStart = first.runOnStart;
        firstWaypoint.onTick = first.runOnTick;
        firstWaypoint.blocking = first.runBlocking;

        double rotationState = firstWaypoint.r;

        waypoints.add(firstWaypoint);
        // Convert bezier path to list of waypoints for the follower to follow
        for (int i = 0; i < nodes.size() - 1; i++) {
            Node current = nodes.get(i);
            Node next = nodes.get(i + 1);
            Vector2 p0 = new Vector2(current.x, current.y);
            Vector2 p1 = current.h2x == Double.MAX_VALUE ? p0 : new Vector2(current.x, current.y);
            Vector2 p3 = new Vector2(current.x, current.y);
            Vector2 p2 = next.h1x == Double.MAX_VALUE ? p3 : new Vector2(current.x, current.y);
            ArrayList<Vector2> points = BezierUtils.sampleCubicBezier(p0, p1, p2, p3, 0.1);

            if (current.r != Double.MAX_VALUE) {
                rotationState = current.r;
            }

            boolean firstPoint = true;
            for (Vector2 point : points) {
                Waypoint waypoint = new Waypoint(point.x, point.y);
                if (firstPoint) {
                    firstPoint = false;
                    waypoint.onStart = current.runOnStart;
                }
                waypoint.onTick = current.runOnTick;
                waypoint.blocking = current.runBlocking;
                waypoint.moveSpeed = current.moveSpeed;
                waypoint.rotSpeed = current.rotSpeed;
                waypoint.r = rotationState;
                waypoints.add(waypoint);
            }
            if (next.stop) {
                waypoints.get(waypoints.size() - 1).stop = true;
            }
        }
        timer = System.currentTimeMillis() - timer;
        opMode.canSleep = true;
    }

    private int getEnd() {
        return nodes.size() - 1;
    }

    public AtlasPathBuilder andMove(double x, double y) { return andMove(x, y, 1); }
    public AtlasPathBuilder andMove(double x, double y, double speed) {
        Node lastNode = nodes.get(getEnd());
        lastNode.x += x;
        lastNode.y += y;
        lastNode.moveSpeed = speed;
        return this;
    }
    public AtlasPathBuilder andMoveTo(double x, double y) { return andMoveTo(x, y, 1); }
    public AtlasPathBuilder andMoveTo(double x, double y, double speed) {
        Node lastNode = nodes.get(getEnd());
        lastNode.x = x;
        lastNode.y = y;
        lastNode.moveSpeed = speed;
        return this;
    }

    public AtlasPathBuilder andRotate(double degrees) { return andRotate(degrees, 1); }
    public AtlasPathBuilder andRotate(double degrees, double speed) {
        Node lastNode = nodes.get(getEnd());
        lastNode.r = rotation + degrees;
        rotation += degrees;
        lastNode.rotSpeed = speed;
        return this;
    }

    public AtlasPathBuilder andRotateTo(double degrees) { return andRotateTo(degrees, 1); }
    public AtlasPathBuilder andRotateTo(double degrees, double speed) {
        Node lastNode = nodes.get(getEnd());
        lastNode.r = degrees;
        rotation = degrees;
        lastNode.rotSpeed = speed;
        return this;
    }

    public AtlasPathBuilder andRun(Runnable func) {
        Node lastNode = nodes.get(getEnd());
        lastNode.runOnStart.add(func);

        return this;
    }

    public AtlasPathBuilder andRunTick(Runnable func) {
        Node lastNode = nodes.get(getEnd());
        lastNode.runOnStart.add(func);
        return this;
    }

    public AtlasPathBuilder andRunBlocking(Callable<Boolean> func) {
        Node lastNode = nodes.get(getEnd());
        lastNode.runBlocking.add(func);
        return this;
    }
    public AtlasPathBuilder andWaitForComplete() {
        Node lastNode = nodes.get(getEnd());
        lastNode.stop = true;
        return this;
    }

    public AtlasPathBuilder thenRun(Runnable func) {
        Node lastNode = nodes.get(getEnd());
        Node then = new Node(lastNode.x, lastNode.y);
        nodes.add(then);

        then.runOnStart.add(func);
        return this;
    }

    public AtlasPathBuilder thenMove(double x, double y) { return thenMove(x, y, 1); }
    public AtlasPathBuilder thenMove(double x, double y, double speed) {
        nodes.add(new Node(0, 0));
        return andMove(x, y, speed);
    }
    public AtlasPathBuilder thenMoveTo(double x, double y) { return thenMoveTo(x, y, 1); }
    public AtlasPathBuilder thenMoveTo(double x, double y, double speed) {
        nodes.add(new Node(0, 0));
        return andMoveTo(x, y, speed);
    }

    public AtlasPathBuilder thenRotate(double degrees) { return thenRotate(degrees, 1); }
    public AtlasPathBuilder thenRotate(double degrees, double speed) {
        Node lastNode = nodes.get(getEnd());
        Node then = new Node(lastNode.x, lastNode.y);
        then.r = rotation + degrees;
        rotation += degrees;
        then.rotSpeed = speed;

        nodes.add(then);
        return this;
    }

    public AtlasPathBuilder thenRotateTo(double degrees) { return thenRotateTo(degrees, 1); }
    public AtlasPathBuilder thenRotateTo(double degrees, double speed) {
        Node lastNode = nodes.get(getEnd());
        Node then = new Node(lastNode.x, lastNode.y);
        then.r = degrees;
        then.rotSpeed = speed;
        rotation = degrees;

        nodes.add(then);
        return this;
    }

    public AtlasPathBuilder thenBezierTo(double x, double y, double h1x, double h1y, double h2x, double h2y) {
        return thenBezierTo(x, y, h1x, h1y, h2x, h2y, 1.0);
    }
    public AtlasPathBuilder thenBezierTo(double x, double y, double h1x, double h1y, double h2x, double h2y, double speed) {
        Node lastNode = nodes.get(getEnd());
        lastNode.h2x = h1x;
        lastNode.h2y = h1y;
        lastNode.moveSpeed = speed;
        Node then = new Node(x, y);
        then.h1x = h2x;
        then.h1y = h2y;

        nodes.add(then);
        return this;
    }
}