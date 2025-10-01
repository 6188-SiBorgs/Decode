package org.firstinspires.ftc.teamcode.atlas.atlasauto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.atlas.AtlasChassis;

public abstract class AtlasAutoOp extends LinearOpMode {
    public AtlasChassis chassis;
    public AtlasFollower follower;

    public boolean canSleep = true;
    AtlasParameters parameters;
    @Override
    public void runOpMode() {
        parameters = create();
        if (parameters == null) {
            throw new NullPointerException("AtlasAuto create() method must return an AtlasParameters object");
        }
        chassis = parameters.chassis;

        follower = new AtlasFollower(this, chassis, parameters.tolerance, parameters.stopTolerance);

        while (opModeInInit()) {
            telemetry.addLine(String.format("============= Atlas ============="));
            telemetry.addLine(String.format("Atlas is ready! Waiting for start..."));
            telemetry.addLine(String.format("================================="));
            initLoop();
            telemetry.update();
        }

        waitForStart();
        perform();
        chassis.movePower(0, 0, 0);
        while (opModeIsActive()) {
            telemetry.addLine(String.format("============= Atlas ============="));
            telemetry.addLine(String.format("Finished performing."));
            telemetry.addLine(String.format("================================="));
            telemetry.update();
        }
    }

    // Override these functions
    public abstract AtlasParameters create();
    public abstract void perform();
    private void initLoop() {}
    void tickLoop() {}

    public AtlasPathBuilder startPath() {
        return new AtlasPathBuilder(this, chassis.pose.x, chassis.pose.y, chassis.yawDeg, parameters.distancePerWaypoint);
    }
    public AtlasPathBuilder move(double x, double y) { return move(x, y, 1); }
    public AtlasPathBuilder move(double x, double y, double speed) { return moveTo(chassis.pose.x + x, chassis.pose.y + y, speed); }
    public AtlasPathBuilder moveTo(double x, double y) { return moveTo(x, y, 1); }
    public AtlasPathBuilder moveTo(double x, double y, double speed) {
        return startPath().thenMoveTo(x, y);
    }

    public AtlasPathBuilder rotate(double degrees) { return rotate(degrees, 1); }
    public AtlasPathBuilder rotate(double degrees, double speed) { return rotateTo(chassis.yawDeg + degrees); }
    public AtlasPathBuilder rotateTo(double degrees) { return rotateTo(degrees, 1); }
    public AtlasPathBuilder rotateTo(double degrees, double speed) {
        return startPath().andRotateTo(degrees);
    }
    public void loop(Runnable func) {
        while (opModeIsActive()) {
            chassis.update();
            func.run();
            telemetry.update();
        }
    }

    public void eep(long milliseconds) {
        if (!canSleep) {
            throw new IllegalStateException("Cannot use sleep when auto is moving. Do linear stuff after .perform() finishes. You may need to use multiple path builders.");
        }
        long startTime = System.currentTimeMillis();
        while (startTime + milliseconds < System.currentTimeMillis() && opModeIsActive()) {
            chassis.update();
        }
    }

}
