package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utils.Chassis;

/**
 * Example OpMode. Demonstrates use of gyro, color sensor, encoders, and telemetry.
 *
 */
@TeleOp(name = ".XendyOpModeTesting", group = "MecanumBot")
public class Teleop extends LinearOpMode {
    public void runOpMode(){
        Chassis chassis = new Chassis(this);
        chassis.waitForStart(this);
        while (opModeIsActive()){
            double px = gamepad1.left_stick_x;
            double py = -gamepad1.left_stick_y;
            double pa = gamepad1.right_stick_x;
            chassis.update(telemetry);
            chassis.moveFieldRelative(px, py, pa);
            telemetry.update();
        }
    }
}
