package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.utils.MecanumChassis;
// Controls:
// Left Stick: Movement
// Right stick: Rotation
// Left Bumper: Shoot
// Right Trigger: Rotate shooter speed
// A: Intake
// Down Arrow: Spit out

/*
Dark mode
More gradients (ultra modern fancy super cool)
Fix the breakdown graph and round numbers to make it more appealing
Make it way fancier and professional
 */

@TeleOp(name="Teleop")
public class Teleop extends LinearOpMode {
    private static final double LAUNCHER_SPEED_MULTIPLIER = 0.8;
    private static final int LAUNCHER_SPEED_MAX = 1600;
    private static int LAUNCHER_SPEED = (int) (LAUNCHER_SPEED_MAX * LAUNCHER_SPEED_MULTIPLIER);
    public static final int INTAKE_TIME = 500;
    public static final int LAUNCH_TIME = 1000;
    public static final int TIME_BEFORE_ORIENTATION_BASED_STATION_KEEPING = 50;

    private DcMotorEx launcherLeft;
    private DcMotorEx launcherRight;

    double maxRotationError = 1;
    boolean launchServoUp = false;
    long intakeTimer = 0L;
    long launchTimer = 0L;
    long rotationTimer = 0L;

    @Override
    public void runOpMode() {
        MecanumChassis chassis = new MecanumChassis(this);

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

            if (gamepad1.dpad_right) {
                LAUNCHER_SPEED += 50;
            }

            if (gamepad1.dpad_left) {
                LAUNCHER_SPEED -= 50;
            }

            if (gamepad1.leftBumperWasPressed()) {
                launchServoUp = !launchServoUp;
                launchTimer = 0;
            }

            if ((gamepad1.left_trigger > 0.1 || gamepad1.dpad_down) && intakeTimer == 0) {
                intakeTimer = System.currentTimeMillis();
                launchServoUp = false;
                resetLaunchMotors();
                launcherRight.setVelocity(1000 * (gamepad1.dpad_down ? 1 : -1));
            }

            if (intakeTimer != 0 && System.currentTimeMillis() - intakeTimer > INTAKE_TIME) {
                intakeTimer = 0;
                resetLaunchMotors();
                launcherRight.setVelocity(0);
            }

            launchServo.setPosition(launchServoUp ? 0.75 : 0.55);
            telemetry.addData("Servo Position", launchServo.getPosition());

            double velocity = Math.min(Math.abs(launcherLeft.getVelocity()), Math.abs(launcherRight.getVelocity()));
            if (intakeTimer == 0) {
                launcherLeft.setVelocity(LAUNCHER_SPEED * gamepad1.right_trigger);
                launcherRight.setVelocity(-LAUNCHER_SPEED * gamepad1.right_trigger);
                if (velocity > LAUNCHER_SPEED) {

                    launchServoUp = true;
                    launchTimer = System.currentTimeMillis();
                }
            }

            if (launchTimer != 0 && System.currentTimeMillis() - launchTimer > LAUNCH_TIME) {
                launchTimer = 0;
                launchServoUp = false;
            }

            telemetry.addLine("Launching");
            telemetry.addData("Launch servo up?", launchServoUp);
            telemetry.addData("Target Launch Speed", LAUNCHER_SPEED);
            telemetry.addData("Current Launch Speed", velocity);
            telemetry.addLine("Intake");
            telemetry.addData("Intake Timer", intakeTimer);
            telemetry.addLine();
            telemetry.addData("Robot Position", chassis.pose);

            chassis.update(telemetry);

            if (rightStickX != 0) {
                targetAngle = chassis.yawDeg;
                rotationTimer = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - rotationTimer > TIME_BEFORE_ORIENTATION_BASED_STATION_KEEPING) {
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