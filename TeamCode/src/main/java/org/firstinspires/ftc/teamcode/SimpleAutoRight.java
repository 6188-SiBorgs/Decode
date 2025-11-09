package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasAutoOp;
import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasParameters;
import org.firstinspires.ftc.teamcode.utils.XDriveChassis;

@Autonomous(name="Simple Auto Right")
public class SimpleAutoRight extends AtlasAutoOp  {
    @Override
    public AtlasParameters create() {
        return new AtlasParameters(new XDriveChassis(this), 0.02, 0.01, 0.05);
    }

    @Override
    public void perform() {
        moveTo(0.5, 0)
                .perform();
    }
}
