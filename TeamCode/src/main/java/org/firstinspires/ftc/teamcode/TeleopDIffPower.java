package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.utils.XDriveChassis;
// Controls:
// Left Stick: Movement
// Right stick: Rotation
// Left Bumper: Shoot
// Right Trigger: Rotate shooter speed
// A: Intake

@Disabled
@TeleOp(name="Teleop (for broken robot)")
public class TeleopDIffPower extends LinearOpMode {
    private static final int LAUNCHER_SPEED = 1350;

    private DcMotorEx launcherLeft;
    private DcMotorEx launcherRight;

    double maxRotationError = 1;
    boolean launchServoUp = false;
    boolean leftBumperToggle = false;
    long intakeTimer = 0L;
    long launchTimer = 0L;
    long rotationTimer = 0L;

    @Override
    public void runOpMode() {
        XDriveChassis chassis = new XDriveChassis(this);

        launcherLeft = (DcMotorEx) hardwareMap.get(DcMotor.class, "launcherLeft");
        launcherRight = (DcMotorEx) hardwareMap.get(DcMotor.class, "launcherRight");
        Servo launchServo = hardwareMap.get(Servo.class, "launchServo");

        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        chassis.waitForStart(this);
        chassis.imu.resetYaw();
        double targetAngle = 0;

        while (opModeIsActive()) {
            double leftStickX = gamepad1.left_stick_x;
            double leftStickY = -gamepad1.left_stick_y;
            double rightStickX = gamepad1.right_stick_x;
            rightStickX = Math.copySign(Math.pow(rightStickX, 2), rightStickX);
            double rotationPower = rightStickX;

            if (gamepad1.left_bumper) {
                if (!leftBumperToggle) launchServoUp = !launchServoUp;
                leftBumperToggle = true;
                launchTimer = 0;
            } else leftBumperToggle = false;

            if (gamepad1.a && intakeTimer == 0) {
                intakeTimer = System.currentTimeMillis();
                launchServoUp = false;
                resetLaunchMotors();
                launcherLeft.setVelocity(1000);
                launcherRight.setVelocity(1000);
            }

            if (intakeTimer != 0 && System.currentTimeMillis() - intakeTimer > 500) {
                intakeTimer = 0;
                resetLaunchMotors();
                launcherLeft.setVelocity(0);
                launcherRight.setVelocity(0);
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
            chassis.moveFieldRelativeDegraded(leftStickX, leftStickY, rotationPower);
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
