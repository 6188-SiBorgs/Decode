package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Motor Test")
public class MotorTest extends OpMode {
    private DcMotorEx testMotor;

    @Override
    public void init() {
        testMotor = hardwareMap.get(DcMotorEx.class, "testMotor");
    }

    @Override
    public void loop() {
        testMotor.setPower(gamepad1.left_stick_x);
    }
}
