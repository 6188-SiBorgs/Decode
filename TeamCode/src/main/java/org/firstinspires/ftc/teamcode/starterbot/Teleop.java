package org.firstinspires.ftc.teamcode.starterbot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "StartBotTeleop")
public class Teleop extends LinearOpMode {
    public DcMotorEx right, left;

    @Override
    public void runOpMode() {
        right = hardwareMap.get(DcMotorEx.class, "right");
        left = hardwareMap.get(DcMotorEx.class, "left");

        waitForStart();

        while (opModeIsActive()) {
            right.setPower(gamepad1.right_stick_y);
            left.setPower(gamepad1.left_stick_y);
        }
    }
}
