package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasAutoOp;
import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasParameters;
import org.firstinspires.ftc.teamcode.utils.Chassis;

@TeleOp(name="LimelihghtAutoTest")
public class LimelightAutoTest extends AtlasAutoOp {
    @Override
    public AtlasParameters create() {
        Chassis chassis = new Chassis(this);
        return new AtlasParameters(chassis, 0.02, 0.01, 0.05);
    }

    @Override
    public void perform() {
        moveTo(0, 0)
                .thenMoveTo(0.6, 0)
                .thenMoveTo(0.6, 0.6)
                .thenMoveTo(0, 0.6)
                .thenMoveTo(0, 0).perform();
    }
}