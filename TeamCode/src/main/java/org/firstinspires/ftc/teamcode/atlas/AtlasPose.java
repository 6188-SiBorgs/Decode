package org.firstinspires.ftc.teamcode.atlas;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class AtlasPose {
    private final double metersPerTick;

    public static final long PAST_STATE_HOLD_TIME = 2000; // ms

    public double x = 0.0;
    public double y = 0.0;

    private double limelightX = 0.0;
    private double limelightY = 0.0;
    private double limelightT = 0.0;

    private final List<PastState> pastStates = new ArrayList<>();

    public AtlasPose(double metersPerTick) {
        this.metersPerTick = metersPerTick;
    }

    public void updateEncoders(int deltaFrontLeft,
                               int deltaFrontRight,
                               int deltaBackLeft,
                               int deltaBackRight,
                               double yawRads) {
        double dxLocal = (deltaFrontLeft + deltaFrontRight + deltaBackLeft + deltaBackRight) / 4.0;
        double dyLocal = (-deltaFrontLeft + deltaFrontRight + deltaBackLeft - deltaBackRight) / 4.0;

        double dx = sin(yawRads) * dxLocal + cos(yawRads) * dyLocal;
        double dy = cos(yawRads) * dxLocal - sin(yawRads) * dyLocal;

        x -= dx * metersPerTick;
        y += dy * metersPerTick;

        long time = System.currentTimeMillis();
        pastStates.add(new PastState(time, dx, dy, x, y));

        while (!pastStates.isEmpty() &&
                time - pastStates.get(0).time > PAST_STATE_HOLD_TIME) {
            pastStates.remove(0);
        }
    }

//    public void updateLimelight(LLResult result) {
//        if (result == null) return;
//        Position mt2 = result.getBotpose_MT2().getPosition();
//        limelightX = mt2.getX();
//        limelightY = mt2.getY();
//        limelightT = mt2.getAcquisitionTime();
//
//        double k = visionConfidence * (processNoise / (processNoise + visionNoise));
//
//        x = (1 - k) * pose.x + k * visionPose.x;
//        y = (1 - k) * pose.y + k * visionPose.y;
//    }

    public String toString() {
        return String.format("<AtlasPose %.2f, %.2f>", x, y);
    }

    public static class PastState {
        public final long time;
        public final double dx;
        public final double dy;
        public final double x;
        public final double y;

        public PastState(long time, double dx, double dy, double x, double y) {
            this.time = time;
            this.dx = dx;
            this.dy = dy;
            this.x = x;
            this.y = y;
        }
    }
}
