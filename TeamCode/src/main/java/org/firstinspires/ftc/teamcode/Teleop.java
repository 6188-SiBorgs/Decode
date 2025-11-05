package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utils.XDriveChassis;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Teleop")
public class Teleop extends OpMode {
    static final int maxRotationError = 1;

    XDriveChassis chassis;
    DcMotorEx launcherMotor1, launcherMotor2;
    double targetAngle;

    int launcherSpeed = 2000;
    boolean launching = false;

    @Override
    public void init() {
        chassis = new XDriveChassis(this);

        launcherMotor1 = hardwareMap.get(DcMotorEx.class, "launcher1");
        launcherMotor2 = hardwareMap.get(DcMotorEx.class, "launcher2");

        launcherMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        targetAngle = chassis.yawDeg;
    }

    @Override
    public void loop() {
        double leftStickX = gamepad1.left_stick_x;
        double leftStickY = -gamepad1.left_stick_y;
        double rightStickX = gamepad1.right_stick_x;
        double rotationMovement = rightStickX;

        double multiplier = Math.pow(2 * gamepad1.left_trigger, 2)  + 1;

        if (gamepad1.dpad_up && launcherSpeed <= 2800) {
            launcherSpeed += (int) (50 * multiplier);
        }

        if (gamepad1.dpad_down && launcherSpeed >= 20) {
            launcherSpeed += (int) (50 * multiplier);
        }

        launcherSpeed = Range.clip(launcherSpeed, 20, 2800);

        if (gamepad1.right_bumper) {
            launching = !launching;
        }

        launcherMotor1.setVelocity(launching ? launcherSpeed * gamepad1.right_trigger : 0);
        launcherMotor2.setVelocity(launching ? launcherSpeed * gamepad1.right_trigger : 0);

        telemetry.addLine("Launcher");
        telemetry.addData("Launching", launching);
        telemetry.addData("Launch Speed", launcherSpeed);
        telemetry.addLine();

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
