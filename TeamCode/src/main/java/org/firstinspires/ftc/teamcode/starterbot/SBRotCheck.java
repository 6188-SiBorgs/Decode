package org.firstinspires.ftc.teamcode.starterbot;

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

@TeleOp(name="rotTest")
public class SBRotCheck extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
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
        waitForStart();
        boolean rotToNegetive = false;
        long startTime = System.currentTimeMillis();
        while (opModeIsActive()) {
            Orientation orientation = imu.getRobotOrientation(
                    AxesReference.INTRINSIC,
                    AxesOrder.ZYX,
                    AngleUnit.DEGREES
            );
            double yaw = orientation.firstAngle;
            telemetry.addData("yaw", yaw);
            telemetry.update();

            if (yaw > 90) {
                rotToNegetive = true;
            }
            if (yaw < 1 && rotToNegetive) break;

            left.setPower(-1);
            right.setPower(1);
        }
        left.setPower(0);
        right.setPower(0);
        telemetry.addData("time", System.currentTimeMillis() - startTime);
        telemetry.update();
        while (opModeIsActive()){}
    }
}
