package org.firstinspires.ftc.teamcode.testing;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Chassis;

@TeleOp(name="MecanumTester")
public class MecanumChassisTester extends LinearOpMode {
    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {
        waitForStart();
        Chassis chassis = new Chassis(hardwareMap);
        int lastPosition = chassis.frontLeft.getCurrentPosition();

        long startTime = System.currentTimeMillis();
        long lastMaxSpeedTime = System.currentTimeMillis();
        long stopTime = System.currentTimeMillis();
        long revolutionTime = 0;

        double lastVelocity = 0;
        boolean stopping = false;
        boolean testing = true;

        chassis.movePower(0, -1, 0);
        while (opModeIsActive() && testing) {
            double dt = chassis.update(telemetry);
            int currentPosition = chassis.frontLeftTicks;
            double velocity = (lastPosition - currentPosition) / dt;
            lastPosition = currentPosition;

            if (!stopping && System.currentTimeMillis() - startTime > 2000) {
                stopping = true;
                chassis.movePower(0, 0, 0);
                stopTime = System.currentTimeMillis();
            }
            if (stopping) {
                if (velocity == 0) {
                    stopTime = System.currentTimeMillis() - stopTime;
                    testing = false;
                }
            } else {
                if (velocity > lastVelocity) {
                    lastMaxSpeedTime = System.currentTimeMillis();
                    lastVelocity = velocity;
                }
            }
            telemetry.addLine(String.format("Test results:"));
            telemetry.addLine(String.format("Acceleration time: %dms (Max encoder vel: %s/sec)", lastMaxSpeedTime - startTime, lastVelocity));
            telemetry.addLine(String.format("Stopping time: %dms", stopTime));
            telemetry.addLine(String.format("Yaw: %f", chassis.yawDeg));
            telemetry.update();
        }
        while (opModeIsActive()) {}

    }
}
