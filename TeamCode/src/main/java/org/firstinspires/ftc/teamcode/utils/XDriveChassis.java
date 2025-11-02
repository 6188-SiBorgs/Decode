package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.atlas.AtlasChassis;
import org.firstinspires.ftc.teamcode.atlas.ChassisConfig;

public class XDriveChassis extends AtlasChassis {
    public XDriveChassis(OpMode opMode) {
        super(opMode);
        ChassisConfig config = new ChassisConfig();
        config.frontLeftName = "frontLeft";
        config.frontRightName = "frontRight";
        config.backLeftName = "rearLeft";
        config.backRightName = "rearRight";
        config.imuParameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                ));
        config.backRightIsReversed = true;
        config.frontRightIsReversed = false;
        config.frontLeftIsReversed = true;
        init(config);
    }

    @Override
    public void tick() {

    }

    @Override
    public void initLoop(OpMode opMode) {

    }
}
