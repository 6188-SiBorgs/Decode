package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.sun.tools.javac.util.List;

import org.firstinspires.ftc.teamcode.utils.Artifacts;
import org.firstinspires.ftc.teamcode.utils.MecanumChassis;
import org.firstinspires.ftc.teamcode.utils.Motif;
import org.firstinspires.ftc.teamcode.utils.ThirdChassis;

@TeleOp(name="Teleop")
public class ThirdTeleop extends LinearOpMode {
    public static final int TIME_BEFORE_ORIENTATION_BASED_STATION_KEEPING = 225;
    double maxRotationError = 1;
    long rotationTimer = 0L;

    @Override
    public void runOpMode() {
        ThirdChassis chassis = new ThirdChassis(this);
        chassis.waitForStart(this);
        chassis.imu.resetYaw();
//        chassis.indexerInit(chassis.getSavedMotifId(), chassis.getSavedArtifacts());
        chassis.indexerInit(Motif.GREEN_PURPLE_PURPLE, Artifacts.EMPTY);
        double targetAngle = 0;

        while (opModeIsActive()) {
            double leftStickX = gamepad1.left_stick_x;
            double leftStickY = -gamepad1.left_stick_y;
            double rightStickX = gamepad1.right_stick_x;
            rightStickX = Math.copySign(Math.pow(rightStickX, 2), rightStickX);
            double rotationPower = rightStickX;

            if (gamepad1.aWasPressed())
                chassis.startIntaking();

            if (gamepad1.aWasReleased())
                chassis.stopIntaking();

            if (gamepad1.rightBumperWasPressed())
                chassis.launchMotif();

            if (gamepad1.leftBumperWasPressed())
                chassis.launchAnything();

            telemetry.addLine("Launch Data");
            telemetry.addData("Launch servo up?", chassis.launching);
            telemetry.addData("Target Launch Velocity", ThirdChassis.TARGET_LAUNCH_VELOCITY);
            telemetry.addData("Current Launch Velocity", chassis.getLaunchVelocity());
            telemetry.addLine();
            telemetry.addData("Robot Position", chassis.pose);
            telemetry.addLine();
            telemetry.addLine("Indexer Data");
            telemetry.addData("Current Position", chassis.getPosition());

            chassis.update(telemetry);

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
