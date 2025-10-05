package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.*;
import org.firstinspires.ftc.robotcore.external.navigation.*;
import org.firstinspires.ftc.teamcode.Chassis;

/**
 * Example OpMode. Demonstrates use of gyro, color sensor, encoders, and telemetry.
 *
 */
@TeleOp(name = ".XendyOpModeTesting", group = "MecanumBot")
public class Teleop extends LinearOpMode {

    public void runOpMode(){
        Chassis chassis = new Chassis(hardwareMap);
        double targetAngle = chassis.yawDeg;
        waitForStart();
        while (opModeIsActive()){
            double px = gamepad1.left_stick_x;
            double py = -gamepad1.left_stick_y;
            double pa = gamepad1.right_stick_x;
            double rot = pa;
            chassis.update(telemetry);
            if(pa != 0) {
                targetAngle = chassis.yawDeg;
            } else {
                double angle = (targetAngle - chassis.yawDeg + 180) % 360 - 180;
                if(Math.abs(angle) > 1) {
                    rot = Math.max(-1.0, Math.min(-angle / 45.0, 1.0));
                }
            }
            chassis.moveFieldRelative(px, py, rot);
            telemetry.update();
        }
    }
}
