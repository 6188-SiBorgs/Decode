package org.firstinspires.ftc.teamcode.utils;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.atlas.AtlasChassis;
import org.firstinspires.ftc.teamcode.atlas.ChassisConfig;

import java.util.List;


public class Chassis extends AtlasChassis {
    public Limelight3A limelight;
    public Telemetry telemetry;
    public Chassis(OpMode opMode) {
        super(opMode);
        this.telemetry = opMode.telemetry;
        HardwareMap hardwareMap = opMode.hardwareMap;
        boolean xdrive = false;
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();
        ChassisConfig mecanumConfig = new ChassisConfig();

        mecanumConfig.frontLeftName = "frontLeft";
        mecanumConfig.frontRightName = "frontRight";
        mecanumConfig.backLeftName = "rearLeft";
        mecanumConfig.backRightName = "rearRight";
        mecanumConfig.backRightIsReversed = true;
        mecanumConfig.backLeftIsReversed = true;
        mecanumConfig.imuParameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                ));
        init(mecanumConfig);
    }

    public Motifs getMotif() {
        int id = getMotifId();
        System.out.println("Detected Motif: " + id);
        if (id == 21) return Motifs.GREEN_PURPLE_PURPLE;
        else if (id == 22) return Motifs.PURPLE_GREEN_PURPLE;
        else if (id == 23) return Motifs.PURPLE_PURPLE_GREEN;
        else throw new RuntimeException("Failed to get motif");
    }

    public int getMotifId() {
        limelight.pipelineSwitch(1);
        int id = -1;
        while (id == -1) {
            LLResult result = limelight.getLatestResult();
            if (result.getPipelineIndex() != 1 || !result.isValid()) continue;
            List<LLResultTypes.FiducialResult> tags = result.getFiducialResults();
            if (!tags.isEmpty()) {
                id = tags.get(0).getFiducialId();
            }
        }
        limelight.pipelineSwitch(0);
        return id;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void tick() {
            LLResult result = getLimelightData(yawDeg);
            if (result != null) {
                Position mt2 = result.getBotpose_MT2().getPosition();
                if (mt2.x != 0 && mt2.y != 0) {
                    pose.updateTruePosition(mt2.y, -mt2.x, mt2.acquisitionTime);
                    telemetry.addLine(String.format("Pose: %.2f, %.2f", mt2.y, mt2.x));
                } else {
                    telemetry.addLine(String.format("Invalid pose??: %.2f, %.2f", mt2.y, -mt2.x));
                }
            }
    }

    public LLResult getLimelightData(double yaw) {
        double limelightOrientation = (yaw + yawOffsetFromOrigin) % 360;
        if (limelightOrientation > 180) limelightOrientation -= 360;
        limelight.updateRobotOrientation(limelightOrientation);
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            return result;
        }
        return null;
    }

    boolean overrideLimelightOrientation = false;
    @Override
    public void initLoop(OpMode opMode) {
        LLResult result = getLimelightData(0);
        if (overrideLimelightOrientation) {
            telemetry.addLine("Using manual input starting orientation as the start");
        } else {
            telemetry.addLine("Attempting to automatically determine starting orientation using mt1");
            if (result != null) {
                Pose3D pose = result.getBotpose();
                double yaw = pose.getOrientation().getYaw(AngleUnit.DEGREES);
                telemetry.addData("Limelight orientation", yaw);
                if (yaw < 45 && yaw > -45) yawOffsetFromOrigin = 0;
                if (yaw > 45 && yaw < 135) yawOffsetFromOrigin = 90;
                if (yaw > 135 && yaw < 225) yawOffsetFromOrigin = 180;
            } else {
                telemetry.addLine("Cannot see any apriltags to determine orientation, please manually input a orientation");
            }
        }
        if (yawOffsetFromOrigin == 0) {
            telemetry.addLine("Current orientation: Starting on the BLUE GOAL side");
        } else if (yawOffsetFromOrigin == 90) {
            telemetry.addLine("Current orientation: Starting from the FAR SIDE");
        }
        else if (yawOffsetFromOrigin == 180) {
            telemetry.addLine("Current orientation: Starting from th RED GOAL side");
        } else {
            telemetry.addLine("Unknown orientation, yawoffset: " + yawOffsetFromOrigin);
        }
        if (result != null) {
            Position mt2 = result.getBotpose_MT2().getPosition();
            pose.updateTruePosition(mt2.y, -mt2.x, mt2.acquisitionTime);
            telemetry.addLine(String.format("Current determined position: %.2f, %.2f, at t=%d", mt2.y, -mt2.x, mt2.acquisitionTime));
        }
        telemetry.addLine("Manual Input:");
        telemetry.addLine("X: Blue goal side (red team alliance)");
        telemetry.addLine("A: Center (Far side from goals)");
        telemetry.addLine("B: Red goal side (blue team alliance)");
        telemetry.addLine("Y: Reset, attempt to use limelight orientation");
        if (opMode.gamepad1.x) {
            overrideLimelightOrientation = true;
            yawOffsetFromOrigin = 0;
        }
        if (opMode.gamepad1.a) {
            overrideLimelightOrientation = true;
            yawOffsetFromOrigin = 90;
        }
        if (opMode.gamepad1.b) {
            overrideLimelightOrientation = true;
            yawOffsetFromOrigin = 180;
        }
        if (opMode.gamepad1.y) {
            overrideLimelightOrientation = false;
        }
        telemetry.update();
    }
}
