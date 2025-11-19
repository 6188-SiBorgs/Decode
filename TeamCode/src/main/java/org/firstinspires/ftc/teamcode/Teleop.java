package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.utils.XDriveChassis;
// Controls:
// Left Stick: Movement
// Right stick: Rotation
// Left Bumper: Shoot
// Right Trigger: Rotate shooter speed
// A: Intake

@TeleOp(name="Teleop")
public class Teleop extends LinearOpMode {
    private static final int LAUNCHER_SPEED = 1350;

    private DcMotorEx launcherLeft;
    private DcMotorEx launcherRight;

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

            launchServo.setPosition(launchServoUp ? 0.6 : 0.43);

            if (intakeTimer == 0) {
                launcherLeft.setVelocity(LAUNCHER_SPEED * gamepad1.right_trigger);
                launcherRight.setVelocity(LAUNCHER_SPEED * gamepad1.right_trigger);
                double velocity = (launcherLeft.getVelocity() + launcherRight.getVelocity()) * 0.5;
                if (velocity > 1350) {
                    launchServoUp = true;
                    launchTimer = System.currentTimeMillis();
                }
            }

            if (launchTimer != 0 && System.currentTimeMillis() - launchTimer > 1000) {
                launchTimer = 0;
                launchServoUp = false;
            }

            telemetry.addData("Launch servo up?", launchServoUp);
            telemetry.addData("Launch Speed", LAUNCHER_SPEED);
            telemetry.addData("Intake Timer", intakeTimer);
            telemetry.addData("Launch Velocity", (launcherLeft.getVelocity() + launcherRight.getVelocity()) * 0.5);
            telemetry.addLine();
            telemetry.addData("Robot Position", chassis.pose);

            chassis.update(telemetry);

            if (rightStickX != 0) {
                targetAngle = chassis.yawDeg;
                rotationTimer = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - rotationTimer > 50) {
                double angleError = getNormalizedAngle(targetAngle - chassis.yawDeg);
                if (Math.abs(angleError) > maxRotationError) {
                    rotationPower = Math.max(-1.0, Math.min(-angleError / 45.0, 1.0));
                }
            }
            chassis.moveFieldRelative(leftStickX, leftStickY, rotationPower);
            telemetry.update();
        }
    }

    private void resetLaunchMotors() {
        launcherLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcherRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    private double getNormalizedAngle(double rawError) {
        return (rawError + 180) % 360 - 180;
    }
}
