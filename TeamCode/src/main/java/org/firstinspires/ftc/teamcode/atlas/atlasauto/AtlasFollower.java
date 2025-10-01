package org.firstinspires.ftc.teamcode.atlas.atlasauto;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.atlas.AtlasChassis;
import org.firstinspires.ftc.teamcode.atlas.utils.Vector2;
import org.firstinspires.ftc.teamcode.atlas.utils.Waypoint;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static java.lang.Math.*;

public class AtlasFollower {
    private final AtlasAutoOp opMode;
    private final AtlasChassis chassis;
    
    public double tolerance;
    public double stopTolerance;

    private final Telemetry telemetry;

    private int currentWaypoint = 0;

    public AtlasFollower(AtlasAutoOp opMode, AtlasChassis chassis, double t, double st) {
        this.opMode = opMode;
        this.chassis = chassis;
        this.telemetry = opMode.telemetry;

        this.tolerance = t;
        this.stopTolerance = st;
    }

    boolean paused = false;

    public void run(ArrayList<Waypoint> waypoints) {
        boolean stopFunctionPassed = false;
        while (opMode.opModeIsActive()) {
            chassis.update(null);
            
            Waypoint waypoint = waypoints.get(currentWaypoint);

            double distanceToTarget = distance(waypoint.x, waypoint.y,
                    chassis.pose.x, chassis.pose.y);

            telemetry.addLine(String.format("============= Atlas ============="));
            telemetry.addLine(String.format("Performing waypoint %d of %d", currentWaypoint, waypoints.size()));
            telemetry.addLine(String.format("Distance from waypoint %.2fm", distanceToTarget));
            telemetry.addLine(String.format("Pose: (%.2f, %.2f) yaw=%.2f (%.2f)", chassis.pose.x, chassis.pose.y, chassis.yawDeg, rotationTargetValue(waypoint.r, chassis.yawDeg)));
            telemetry.addLine(String.format("Running %d tick loops and %d blocking loops", waypoint.onTick.size(), waypoint.blocking.size()));
            telemetry.addLine(String.format("=================================\n"));

            if (waypoint.stop || currentWaypoint == waypoints.size() - 1) {
                if (distanceToTarget < stopTolerance && abs(getRotationAngle(waypoint.r, chassis.yawDeg)) < 1) {
                    chassis.movePower(0.0, 0.0, 0.0);
                    currentWaypoint += 1;
                    if (currentWaypoint == waypoints.size()) break;
                    waypoint = waypoints.get(currentWaypoint);
                    distanceToTarget = distance(waypoint.x, waypoint.y,chassis.pose.x, chassis.pose.y);
                    opMode.canSleep = true;
                    for (Runnable func : waypoint.onStart) {
                        func.run();
                    }
                    opMode.canSleep = false;
                }
            } else {
                while (distanceToTarget < tolerance && !waypoint.stop && opMode.opModeIsActive()) {
                    currentWaypoint += 1;
                    waypoint = waypoints.get(currentWaypoint);
                    distanceToTarget = distance(waypoint.x, waypoint.y,chassis.pose.x, chassis.pose.y);
                    for (Runnable func : waypoint.onStart) {
                        func.run();
                    }
                }
                if (waypoint.stop) {
                    continue;
                }
            }

            if (waypoint.stop) {
                stopFunctionPassed = true;
                for (Callable<Boolean> func : waypoint.blocking) {
                    try {
                        stopFunctionPassed = stopFunctionPassed && func.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            else {
                for (Callable<Boolean> func : waypoint.blocking) {
                    try {
                        func.call();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                for (Runnable func : waypoint.onTick) {
                    func.run();
                }
            }
            opMode.tickLoop();

            Vector2 moveVector = new Vector2(waypoint.x - chassis.pose.x, waypoint.y - chassis.pose.y);
            Vector2 moveDirection = Vector2.normalize(moveVector);
            double moveMagnitude = Vector2.magnitude(moveVector);

            double dx = moveDirection.x;
            double dy = moveDirection.y;
            if (waypoint.stop) {
                dx = positionTargetValue(waypoint.x - chassis.pose.x);
                dy = positionTargetValue(waypoint.y - chassis.pose.y);
            }
            double dr = rotationTargetValue(waypoint.r, chassis.yawDeg);

            chassis.moveFieldRelative(
                    clamp(dx, -1.0, 1.0),
                    clamp(dy, -1.0, 1.0),
                    dr
            );

            telemetry.update();
        }

        chassis.movePower(0.0, 0.0, 0.0);
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private double positionTargetValue(double value) {
        if (-tolerance < value && value < tolerance) {
            return value * 0.01;
        }
        return value * 0.25;
    }

    private double rotationTargetValue(double target, double current) {
        double angle = getRotationAngle(target, current);
        if (abs(angle) < 1) return 0.0;
        return clamp(-angle / 45.0, -1.0, 1.0);
    }

    private double getRotationAngle(double target, double current) {
        return (target - current + 180) % 360 - 180;
    }

    private double clamp(double value, double min, double max) {
        return max(min, min(value, max));
    }
}
