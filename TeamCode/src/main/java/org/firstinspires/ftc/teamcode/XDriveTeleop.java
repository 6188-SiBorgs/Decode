package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utils.Chassis;
import org.firstinspires.ftc.teamcode.utils.XDriveChassis;

@TeleOp(name = ".XDriveTeleop", group = "XDrive ")
public class XDriveTeleop extends LinearOpMode {
    public void runOpMode() {
        XDriveChassis chassis = new XDriveChassis(this);
        chassis.waitForStart(this);
        while (opModeIsActive()) {
            double px = gamepad1.left_stick_x;
            double py = -gamepad1.left_stick_y;
            double pa = gamepad1.right_stick_x;
            chassis.update(telemetry);
            chassis.moveFieldRelative(px, py, pa);
            telemetry.update();
        }
    }
}
