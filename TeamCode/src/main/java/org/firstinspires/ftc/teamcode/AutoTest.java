package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasAutoOp;
import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasParameters;
import org.firstinspires.ftc.teamcode.utils.Chassis;
import org.firstinspires.ftc.teamcode.utils.XDriveChassis;

@TeleOp(name="AutoTestSilly")
public class AutoTest extends AtlasAutoOp {
    @Override
    public AtlasParameters create() {
        XDriveChassis chassis = new XDriveChassis(this);
        return new AtlasParameters(chassis, 0.02, 0.01, 0.05);
    }

    @Override
    public void perform() {
        double speed = 0.3  ;
        double tension = 0.05;
        moveTo(-0.6, 0.3).andRotateTo(-90)
            .thenSmoothMoveTo(0, 0.6, speed, tension)
            .thenSmoothMoveTo(0.6, 0.9, speed, tension).andRotateTo(180)
            .thenSmoothMoveTo(0, 1.2, speed, tension)
            .thenSmoothMoveTo(-0.6, 0.9, speed, tension).andRotateTo(90)
            .thenSmoothMoveTo(0, 0.6, speed, tension)
            .thenSmoothMoveTo(0.6, 0.3, speed, tension).andRotateTo(0)
            .thenSmoothMoveTo(0, 0, speed, tension)
            .perform();
    }
}
