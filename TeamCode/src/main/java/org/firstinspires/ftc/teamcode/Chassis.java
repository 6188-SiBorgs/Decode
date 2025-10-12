package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.atlas.AtlasChassis;
import org.firstinspires.ftc.teamcode.atlas.ChassisConfig;

public class Chassis extends AtlasChassis {
    public Limelight3A limelight;
    public Chassis(HardwareMap hardwareMap) {
        boolean xdrive = false;
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
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
            fieldRelativeOffset = 45;
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

    @Override
    public void tick() {
        LLResult result = getLimelightData(yawDeg);
        if (result != null) {
            Position mt2 = result.getBotpose_MT2().getPosition();
            pose.updateTruePosition(mt2.x, mt2.y, mt2.acquisitionTime);
        }
    }

    public LLResult getLimelightData(double yaw) {
        double limelightOrientation = (yaw + limeLightYawOffset) % 360;
        if (limelightOrientation > 180) limelightOrientation -= 360;
        limelight.updateRobotOrientation(limelightOrientation);
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            return result;
        }
        return null;
    }
}
