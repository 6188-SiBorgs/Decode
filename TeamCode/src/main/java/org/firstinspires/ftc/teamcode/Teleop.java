package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utils.Chassis;
import org.firstinspires.ftc.teamcode.utils.XDriveChassis;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Example OpMode. Demonstrates use of gyro, color sensor, encoders, and telemetry.
 *
 */

@TeleOp(name = "Teleop")
public class Teleop extends OpMode {
    static final int maxRotationError = 1;

    XDriveChassis chassis;
    double targetAngle;

    @Override
    public void init() {
        chassis = new XDriveChassis(this);
        targetAngle = chassis.yawDeg;
    }

    @Override
    public void loop() {
        double leftStickX = gamepad1.left_stick_x;
        double leftStickY = -gamepad1.left_stick_y;
        double rightStickX = gamepad1.right_stick_x;
        double rotationMovement = rightStickX;

        chassis.update(telemetry);

        if (rightStickX != 0) {
            targetAngle = chassis.yawDeg;
        } else {
            double angleError = getNormalizedAngle(targetAngle - chassis.yawDeg);
            if (Math.abs(angleError) > maxRotationError) {
                rotationMovement = Math.max(-1.0, Math.min(-angleError / 45.0, 1.0));
            }
        }
        chassis.moveFieldRelative(leftStickX, leftStickY, rotationMovement);
        telemetry.update();
    }

    private static double getNormalizedAngle(double rawError) {
        return (rawError + 180) % 360 - 180;
    }
}
