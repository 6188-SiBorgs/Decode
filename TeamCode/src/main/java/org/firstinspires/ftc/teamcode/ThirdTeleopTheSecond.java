package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utils.Artifacts;
import org.firstinspires.ftc.teamcode.utils.Motif;
import org.firstinspires.ftc.teamcode.utils.ThirdChassis;

@TeleOp(name=".Teleop")
public class ThirdTeleopTheSecond extends LinearOpMode {
    public static final int TIME_BEFORE_ORIENTATION_BASED_STATION_KEEPING = 225;
    double maxRotationError = 1;
    long rotationTimer = 0L;

    int continuousIndex = 0;
    boolean intaking = false;


    @Override
    public void runOpMode() throws InterruptedException {
        ThirdChassis chassis = new ThirdChassis(this);
        chassis.indexerInit(Motif.GREEN_PURPLE_PURPLE, Artifacts.EMPTY);
        chassis.waitForStart(this);
        double targetAngle = 0;

        while (opModeIsActive()) {
            double leftStickX = gamepad1.left_stick_x;
            double leftStickY = -gamepad1.left_stick_y;
            double rightStickX = gamepad1.right_stick_x;

            chassis.update(telemetry);

            if (gamepad1.leftBumperWasPressed()) continuousIndex--;
            if (gamepad1.rightBumperWasPressed()) continuousIndex++;

            double rotationPower = 0;
            if (rightStickX != 0) {
                rotationTimer = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - rotationTimer > TIME_BEFORE_ORIENTATION_BASED_STATION_KEEPING) {
                double angleError = getNormalizedAngle(targetAngle - chassis.yawDeg);
                if (Math.abs(angleError) > maxRotationError) {
                    rotationPower = Math.max(-1.0, Math.min(-angleError / 45.0, 1.0));
                }
            } else {
                targetAngle = chassis.yawDeg;
            }
            chassis.moveFieldRelative(leftStickX, leftStickY, rotationPower);
            telemetry.update();
        }
    }

    private double getNormalizedAngle(double rawError) {
        return (rawError + 180) % 360 - 180;
    }
}
