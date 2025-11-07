package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.utils.XDriveChassis;

@TeleOp(name="Xendys Silly Teleop")
public class XendyTeleop extends LinearOpMode {
    int launcherSpeed = 2000;
    double maxRotationError = 1;
    boolean launching = false;
    boolean launchServoUp = false;

    boolean rmbdown = false;
    boolean lmbdown = false;
    boolean intaking = false;
    @Override
    public void runOpMode() {
        XDriveChassis chassis = new XDriveChassis(this);
        DcMotorEx launcherLeft = (DcMotorEx) hardwareMap.get(DcMotor.class, "launcherLeft");
        DcMotorEx launcherRight = (DcMotorEx) hardwareMap.get(DcMotor.class, "launcherRight");
        Servo launchServo = hardwareMap.get(Servo.class, "launchServo");
        launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        chassis.waitForStart(this);
        chassis.imu.resetYaw();
        double targetAngle = 0;

        while (opModeIsActive()) {
            double leftStickX = gamepad1.left_stick_x;
            double leftStickY = -gamepad1.left_stick_y;
            double rightStickX = gamepad1.right_stick_x;
            double rotationMovement = rightStickX;

            launcherSpeed = Range.clip(launcherSpeed, 20, 2800);

            if (gamepad1.right_bumper) {
                if (!rmbdown) launching = !launching;
                rmbdown = true;
            } else rmbdown = false;
            if (gamepad1.left_bumper) {
                if (!lmbdown) launchServoUp = !launchServoUp;
                lmbdown = true;
            } else lmbdown = false;

            if (!launching && gamepad1.a && !intaking) {
                intaking = true;
                launchServoUp = false;
                launcherLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                launcherRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                launcherLeft.setTargetPosition(30);
                launcherRight.setTargetPosition(30);
                launcherLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                launcherRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                launcherLeft.setVelocity(1000);
                launcherRight.setVelocity(1000);

            }
            if (intaking && !launcherLeft.isBusy() && !launcherRight.isBusy() && Math.abs(launcherLeft.getTargetPosition() - launcherLeft.getCurrentPosition()) < 5) {
                intaking = false;
                launcherLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                launcherRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                launcherLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                launcherRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                launcherLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                launcherRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                launcherLeft.setVelocity(0);
                launcherRight.setVelocity(0);
            }

            launchServo.setPosition(launchServoUp ? 0.6 : 0.39);

            if (!intaking) {
                launcherLeft.setVelocity(launching ? launcherSpeed * gamepad1.right_trigger : 0);
                launcherRight.setVelocity(launching ? launcherSpeed * gamepad1.right_trigger : 0);
            }

            telemetry.addLine("Launcher");
            telemetry.addData("Launching", launching);
            telemetry.addData("Launch servo up?", launchServoUp);
            telemetry.addData("Launch Speed", launcherSpeed);
            telemetry.addData("Intaking", intaking);
            telemetry.addData("Launch Velocity", (launcherLeft.getVelocity() + launcherRight.getVelocity()) * 0.5);
            telemetry.addLine();
            telemetry.addData("Robot Position", chassis.pose);

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

    }

    private static double getNormalizedAngle(double rawError) {
        return (rawError + 180) % 360 - 180;
    }
}
