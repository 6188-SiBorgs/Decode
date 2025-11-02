package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utils.Chassis;
import org.firstinspires.ftc.teamcode.utils.Motifs;

@TeleOp(name="TestMotif")
public class TestMotif extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Chassis chassis = new Chassis(this);
        chassis.waitForStart(this);
        Motifs motif = chassis.getMotif();
        telemetry.addLine(motif.name());
        telemetry.update();
        sleep(9999999);
    }
}
