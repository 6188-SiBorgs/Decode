package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.atlas.AtlasChassis;
import org.firstinspires.ftc.teamcode.atlas.ChassisConfig;

public class MecanumChassis extends AtlasChassis {
    public MecanumChassis(OpMode opMode) {
        super(opMode);
        ChassisConfig config = new ChassisConfig();
        config.frontLeftName = "frontLeft";
        config.frontRightName = "rearRight";
        config.backLeftName = "rearLeft";
        config.backRightName = "frontRight";
        config.backLeftIsReversed = true;
        config.backRightIsReversed = true;
        config.imuParameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
                ));
        init(config);
    }

    @Override
    public void tick() {

    }

    @Override
    public void initLoop(OpMode opMode) {

    }
}
