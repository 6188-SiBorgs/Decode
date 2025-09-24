package org.firstinspires.ftc.teamcode.starterbot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "StartBotTeleop")
public class Teleop extends LinearOpMode {
    public DcMotorEx backLeft, backRight, frontLeft, frontRight;

    @Override
    public void runOpMode() {
        //init

        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        waitForStart();

        backLeft.setPower(gamepad1.left_stick_y);
        frontLeft.setPower(gamepad1.left_stick_y);

        frontRight.setPower(gamepad1.right_stick_y);
        frontRight.setPower(gamepad1.right_stick_y);
    }
}
