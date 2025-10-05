package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Chassis;

@TeleOp(name="rotTest")
public class RotationTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Chassis chassis = new Chassis(hardwareMap);
        waitForStart();
        boolean negetive = false;
        long startTime = System.currentTimeMillis();
        while (opModeIsActive()) {
            chassis.update(telemetry);
            chassis.movePower(0, 0, -1);
            telemetry.addData("yaw", chassis.yawDeg);
            telemetry.update();

            if (chassis.yawDeg < -90) {
                negetive = true;
            }
            if (chassis.yawDeg > -45 && negetive) {
                break;
            }
        }
        chassis.movePower(0, 0, 0);
        telemetry.addData("time", System.currentTimeMillis() - startTime);
        telemetry.update();
        while (opModeIsActive());
    }
}
