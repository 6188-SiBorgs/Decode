package org.firstinspires.ftc.teamcode.starterbot;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Chassis;

@TeleOp(name="StarterBotTester")
public class StarterBotTest extends LinearOpMode {
    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {
        DcMotorEx right = hardwareMap.get(DcMotorEx.class, "right");
        DcMotorEx left = hardwareMap.get(DcMotorEx.class, "left");
        left.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        left.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        left.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        right.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        right.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        right.setDirection(DcMotorSimple.Direction.REVERSE);
        IMU imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                ))
        );
        imu.resetYaw();

        waitForStart();
        int lastPosition = 0;

        long startTime = System.currentTimeMillis();
        long lastMaxSpeedTime = System.currentTimeMillis();
        long stopTime = System.currentTimeMillis();
        long revolutionTime = 0;

        double lastVelocity = 0;
        boolean stopping = false;
        boolean testing = true;

        double lastDeltaTime = 0;
        right.setPower(1);
        left.setPower(1);
        while (opModeIsActive() && testing) {
            Orientation orientation = imu.getRobotOrientation(
                    AxesReference.INTRINSIC,
                    AxesOrder.ZYX,
                    AngleUnit.DEGREES
            );
            telemetry.addData("yaw", orientation.firstAngle);
            double dt = (System.currentTimeMillis() - lastDeltaTime);
            lastDeltaTime = System.currentTimeMillis();
            telemetry.addData("position", right.getCurrentPosition());
            int currentPosition = right.getCurrentPosition();
            double velocity = (currentPosition - lastPosition) / dt;
            lastPosition = currentPosition;

            if (!stopping && System.currentTimeMillis() - startTime > 2000) {
                stopping = true;
                right.setPower(0);
                left.setPower(0);
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
            telemetry.addLine(String.format("Revolution time: %dms", 0));
            telemetry.update();
        }
        while (opModeIsActive()) {}

    }
}
