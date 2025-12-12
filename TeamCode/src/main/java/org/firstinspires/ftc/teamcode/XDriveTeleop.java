package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.atlas.AtlasChassis;
import org.firstinspires.ftc.teamcode.utils.Chassis;
import org.firstinspires.ftc.teamcode.utils.MecanumChassis;
import org.firstinspires.ftc.teamcode.utils.XDriveChassis;

@Disabled
@TeleOp(name = ".XDriveTeleop", group = "XDrive")
public class XDriveTeleop extends OpMode {
    DcMotorEx leftFront, leftBack, rightFront, rightBack;

    @Override
    public void init() {

    }

    @Override
    public void loop() {
        leftFront = hardwareMap.get(DcMotorEx.class, "frontLeft");
        leftBack = hardwareMap.get(DcMotorEx.class,  "rearLeft");
        rightBack = hardwareMap.get(DcMotorEx.class, "rearRight");
        rightFront = hardwareMap.get(DcMotorEx.class,"frontRight");

        leftFront.setPower(gamepad1.a ? gamepad1.right_trigger : 0);
        leftBack.setPower(gamepad1.b ? gamepad1.right_trigger : 0);
        rightBack.setPower(gamepad1.x ? gamepad1.right_trigger : 0);
        rightFront.setPower(gamepad1.y ? gamepad1.right_trigger : 0);
    }
}
