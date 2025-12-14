package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.utils.Artifacts;
import org.firstinspires.ftc.teamcode.utils.DigitalLED;
import org.firstinspires.ftc.teamcode.utils.Motif;
import org.firstinspires.ftc.teamcode.utils.ThirdChassis;

@TeleOp(name=".Teleop")
public class ThirdTeleopTheSecond extends LinearOpMode {
    public static final int TIME_BEFORE_ORIENTATION_BASED_STATION_KEEPING = 225;
    public static final int LAUNCH_TIMER_TIME = 600; // extend and unextend
    double maxRotationError = 1;
    long rotationTimer = 0L;

    int continuousIndex = 0;
    boolean intaking = false;
    // Index =
    private static final double TICKS_PER_POSITION = 28 * 18.8803 / 6;

    public long launchStart = 0;


    @Override
    public void runOpMode() throws InterruptedException {
        ThirdChassis chassis = new ThirdChassis(this);
        chassis.indexerInit(Motif.GREEN_PURPLE_PURPLE, Artifacts.EMPTY);
        chassis.waitForStart(this);
        double targetAngle = 0;

        chassis.indexerMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        chassis.indexerMotor.setTargetPosition(0);
        chassis.indexerMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while (opModeIsActive()) {
            double leftStickX = gamepad1.left_stick_x;
            double leftStickY = -gamepad1.left_stick_y;
            double rightStickX = gamepad1.right_stick_x;

            chassis.update(telemetry);

            if (launchStart == 0) {
                if (gamepad1.leftBumperWasPressed()) continuousIndex--;
                if (gamepad1.rightBumperWasPressed()) continuousIndex++;
            }

            if (gamepad1.a && launchStart == 0) {
                intaking = true;
                chassis.intakeMotor.setVelocity(-1300);
            } else {
                intaking = false;
                chassis.intakeMotor.setVelocity(0);
            }

            int indexerMotorPosition = continuousIndex * 2 + (intaking ? 1 : 0);
            int encoderPosition = (int) (TICKS_PER_POSITION * indexerMotorPosition);

            if (gamepad1.right_trigger > 0.1 && !intaking) {
                chassis.leftLaunchMotor.setVelocity(ThirdChassis.TARGET_LAUNCH_VELOCITY * -gamepad1.right_trigger);
                chassis.rightLaunchMotor.setVelocity(ThirdChassis.TARGET_LAUNCH_VELOCITY * -gamepad1.right_trigger);
                chassis.changeLEDColor(DigitalLED.Color.GREEN);
                if (chassis.getLaunchVelocity() > ThirdChassis.TARGET_LAUNCH_VELOCITY) {
                    launchStart = System.currentTimeMillis();
                    chassis.launchServo.setPosition(ThirdChassis.LAUNCH_SERVO_LOWER);
                    chassis.changeLEDColor(DigitalLED.Color.RED);
                }
            } else {
                chassis.leftLaunchMotor.setVelocity(0);
                chassis.rightLaunchMotor.setVelocity(0);
            }

            if (launchStart != 0) {
                long dt = System.currentTimeMillis() - launchStart;
                if (dt < LAUNCH_TIMER_TIME) {
                    // Thing should be down
                    chassis.changeLEDColor(DigitalLED.Color.RED);
                    chassis.launchServo.setPosition(ThirdChassis.LAUNCH_SERVO_LOWER);
                } else if (dt < LAUNCH_TIMER_TIME * 2) {
                    // Thing should be up
                    chassis.changeLEDColor(DigitalLED.Color.AMBER);
                    chassis.launchServo.setPosition(ThirdChassis.LAUNCH_SERVO_UPPER);
                } else {
                    chassis.changeLEDColor(DigitalLED.Color.NONE);
                    launchStart = 0;
                }
            } else {
                chassis.launchServo.setPosition(ThirdChassis.LAUNCH_SERVO_UPPER);
            }

            // offset 2 for intake pos
            chassis.indexerMotor.setTargetPosition(encoderPosition);
            chassis.indexerMotor.setPower(0.75);

            telemetry.addData("Launch Speed", chassis.getLaunchVelocity());


            double rotationPower = 0;
            if (rightStickX != 0) {
                rotationTimer = System.currentTimeMillis();
                rotationPower = rightStickX;
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
