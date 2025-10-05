package org.firstinspires.ftc.teamcode.atlas;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.teamcode.Chassis;

import static java.lang.Math.*;

import java.lang.reflect.Parameter;

public class AtlasChassis {
    public DcMotorEx backLeft, backRight, frontLeft, frontRight;
    public IMU imu;
    public AtlasPose pose;

    public double yawRads = 0.0;
    public double yawDeg = 0.0;
    public double limeLightYawOffset = 0.0;
    public double robotYawOffset = 0.0;

    public int backLeftTicks = 0;
    public int backRightTicks = 0;
    public int frontLeftTicks = 0;
    public int frontRightTicks = 0;

    public double metersPerTick = 0.00048;
    public double fieldRelativeOffset = 0.0;

    private long lastUpdateTime = System.currentTimeMillis();

    public static final double RAD_TO_DEG = 180.0 / Math.PI;
    public static final double DEG_TO_RAD = Math.PI / 180.0;

    private DcMotor.ZeroPowerBehavior zeroPowerBehavior;
    public AtlasChassis() {

    }

    public void init(HardwareMap hardwareMap, ChassisConfig config) {
        zeroPowerBehavior = config.zeroPowerBehavior;
        backLeft = getDcMotorEx(hardwareMap, config.backLeftName, config.backLeftIsReversed);
        backRight = getDcMotorEx(hardwareMap, config.backRightName, config.backRightIsReversed);
        frontLeft = getDcMotorEx(hardwareMap, config.frontLeftName, config.frontLeftIsReversed);
        frontRight = getDcMotorEx(hardwareMap, config.frontRightName, config.frontRightIsReversed);
        // Make sure imu exists in hardware map
        imu = hardwareMap.get(IMU.class, "imu");

        pose = new AtlasPose(metersPerTick);

        imu.initialize(config.imuParameters);
        imu.resetYaw();
    }

    private DcMotorEx getDcMotorEx(HardwareMap hardwareMap, String name, boolean reversed) {
        DcMotorEx motor = (DcMotorEx) hardwareMap.get(DcMotor.class, name);
        if (reversed) motor.setDirection(DcMotorSimple.Direction.REVERSE);
        else motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(zeroPowerBehavior);
        return motor;
    }
    private DcMotorEx getDcMotorEx(HardwareMap hardwareMap, String name) {
        return getDcMotorEx(hardwareMap, name, false);
    }

    public void moveFieldRelative(double x, double y, double rx) {
        double yaw = yawRads + fieldRelativeOffset;
        double rotatedX = x * Math.cos(-yaw) - y * Math.sin(-yaw);
        double rotatedY = x * Math.sin(-yaw) + y * Math.cos(-yaw);
        movePower(rotatedX, rotatedY, rx);
    }

    public void movePower(double x, double y, double r) {
        double denominator = max(abs(y) + abs(x) + abs(r), 1.0);
        frontLeft.setPower((y + x + r) / denominator);
        backLeft.setPower((y - x + r) / denominator);
        frontRight.setPower((y - x - r) / denominator);
        backRight.setPower((y + x - r) / denominator);
    }

    public void runToPosition(int x, int y) {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setTargetPosition(y + x);
        frontLeft.setTargetPosition(y + x);
        backLeft.setTargetPosition(y - x);
        frontRight.setTargetPosition(y - x);
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        movePower(x, y, 0);
    }

    public double update() {
        return update(null);
    }
    public double update(Telemetry telemetry) {
        long currentTime = System.currentTimeMillis();
        double deltaTimeMS = currentTime - lastUpdateTime;
        double deltaTime = deltaTimeMS * 0.001;
        lastUpdateTime = currentTime;

        Orientation orientation = imu.getRobotOrientation(
                AxesReference.INTRINSIC,
                AxesOrder.ZYX,
                AngleUnit.RADIANS
        );

        yawRads = orientation.firstAngle + robotYawOffset * DEG_TO_RAD;
        yawDeg = orientation.firstAngle * RAD_TO_DEG + robotYawOffset;

        int[] positions = new int[]{
                backLeft.getCurrentPosition(),
                backRight.getCurrentPosition(),
                frontLeft.getCurrentPosition(),
                frontRight.getCurrentPosition()
        };

        int deltaBackLeft = positions[0] - backLeftTicks;
        int deltaBackRight = positions[1] - backRightTicks;
        int deltaFrontLeft = positions[2] - frontLeftTicks;
        int deltaFrontRight = positions[3] - frontRightTicks;

        backLeftTicks = positions[0];
        backRightTicks = positions[1];
        frontLeftTicks = positions[2];
        frontRightTicks = positions[3];

        pose.updateEncoders(deltaFrontLeft, deltaFrontRight, deltaBackLeft, deltaBackRight, yawRads);

        if (telemetry != null) {
            telemetry.addLine("Chassis debug data:");
            telemetry.addLine("position (" + pose.x + ", " + pose.y + ")");
            telemetry.addLine("yaw " + yawDeg);
        }
        return deltaTime;
    }
}
