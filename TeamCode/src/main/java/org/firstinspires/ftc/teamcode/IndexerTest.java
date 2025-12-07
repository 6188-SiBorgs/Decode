package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import java.util.ArrayList;

@TeleOp(name = "Indexer Test")
public class IndexerTest extends OpMode {
    private static final double TICKS_PER_INDEX = 28 * 18.8803 / 6;
    private int position = 0;
    private boolean intaking = false;

    private int pattern = 0;
    private int purpleIndex = 0;
    private ArrayList<Integer> artifacts = new ArrayList<>();

    private DcMotorEx indexerMotor;

    @Override
    public void init() {
        purpleIndex = pattern;
        artifacts.add(-1);
        artifacts.add(-1);
        artifacts.add(-1);
        indexerMotor = hardwareMap.get(DcMotorEx.class, "indexer");
        indexerMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        indexerMotor.setTargetPosition(0);
        indexerMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        indexerMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    @Override
    public void loop() {
        intaking = gamepad1.a;
        if (gamepad1.x) artifacts.set(getIndex(), 0);
        if (gamepad1.y) artifacts.set(getIndex(), 1);

        if (gamepad1.dpadRightWasPressed()) position++;
        if (gamepad1.dpadLeftWasPressed()) position--;
        if (gamepad1.rightBumperWasPressed()) {
            boolean cycle = true;
            int indexerIndex = artifacts.indexOf(getNextBall());
            if (indexerIndex == -1) {
                indexerIndex = artifacts.indexOf(1 - getNextBall());
                cycle = false;
            }
            if (indexerIndex != -1) {
                position = indexerIndex;
                if (cycle) cycleBall();
                artifacts.set(getIndex(), -1);
            }
        }

        int motorIndex = position * 2 + (intaking ? 1 : 0);
        int encoderPosition = (int) (TICKS_PER_INDEX * motorIndex);
        indexerMotor.setTargetPosition(encoderPosition);
        indexerMotor.setPower(0.25);

        telemetry.addData("Index", getIndex());
        telemetry.addData("Target Position", encoderPosition);
        telemetry.addData("Actual Position", indexerMotor.getCurrentPosition());
        telemetry.addData("Current Artifacts", artifacts);
        telemetry.addData("Next Ball", getNextBall());
        telemetry.update();
    }

    private int getNextBall() {
        return purpleIndex == 0 ? 1 : 0;
    }

    private int getIndex() {
        return Math.floorMod(position, 3);
    }

    private void cycleBall() {
        purpleIndex--;
        if (purpleIndex < 0) {
            purpleIndex = 2;
        }
    }
}
