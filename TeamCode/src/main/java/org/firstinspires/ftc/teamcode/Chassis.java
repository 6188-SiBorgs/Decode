package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.atlas.AtlasChassis;
import org.firstinspires.ftc.teamcode.atlas.ChassisConfig;

public class Chassis extends AtlasChassis {
    public Chassis(HardwareMap hardwareMap) {
        boolean xdrive = true;
        ChassisConfig xDriveConfig = new ChassisConfig();
        ChassisConfig mecanumConfig = new ChassisConfig();
        if (xdrive) {
            xDriveConfig.frontLeftName = "frontLeft";
            xDriveConfig.frontRightName = "frontRight";
            xDriveConfig.backLeftName = "rearLeft";
            xDriveConfig.backRightName = "rearRight";
            xDriveConfig.backRightIsReversed = true;
            xDriveConfig.frontRightIsReversed = true;
            xDriveConfig.imuParameters = new IMU.Parameters(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.UP,
                            RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
                    ));
            robotYawOffset = 45;
            init(hardwareMap, xDriveConfig);
        } else {
            mecanumConfig.frontLeftName = "frontLeft";
            mecanumConfig.frontRightName = "frontRight";
            mecanumConfig.backLeftName = "rearLeft";
            mecanumConfig.backRightName = "rearRight";
            mecanumConfig.backRightIsReversed = true;
            mecanumConfig.backLeftIsReversed = true;
            xDriveConfig.imuParameters = new IMU.Parameters(
                    new RevHubOrientationOnRobot(
                            RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                            RevHubOrientationOnRobot.UsbFacingDirection.UP
                    ));
            init(hardwareMap, mecanumConfig);
        }
    }
}
