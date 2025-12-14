package org.firstinspires.ftc.teamcode.dihuagyvfiuahwdf;

import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasAutoOp;
import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasParameters;
import org.firstinspires.ftc.teamcode.utils.MecanumChassis;
import org.firstinspires.ftc.teamcode.utils.ThirdChassis;

public class AutoBlueFar extends AtlasAutoOp {
    @Override
    public AtlasParameters create() {
        ThirdChassis chassis = new ThirdChassis(this);
        return new AtlasParameters(chassis, 0.04, 0.01, 0.05);
    }

    @Override
    public void perform() {;
    }
}
