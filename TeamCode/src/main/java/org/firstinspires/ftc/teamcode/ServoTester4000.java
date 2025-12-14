package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="ServoTester4000")
public class ServoTester4000 extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Servo servo = hardwareMap.get(Servo.class, "launchServo");
        waitForStart();
        
        double servoPosition = 0.0;
        while (opModeIsActive()) {
            if (gamepad1.aWasPressed()) servoPosition += 0.05;
            if (gamepad1.yWasPressed()) servoPosition -= 0.05;

            if (gamepad1.leftBumperWasPressed()) servoPosition = 0;
            if (gamepad1.rightBumperWasPressed()) servoPosition = 1;
            if (gamepad1.dpadLeftWasPressed()) servoPosition += 0.1;
            if (gamepad1.dpadRightWasPressed()) servoPosition -= 0.1;

            servoPosition = Range.clip(servoPosition, 0.0, 1.0);

            servo.setPosition(servoPosition);

            telemetry.addData("servoPosition", servoPosition);
            telemetry.update();
        }

    }
}
