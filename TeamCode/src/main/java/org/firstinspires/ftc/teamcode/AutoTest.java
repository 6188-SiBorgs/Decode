package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasAutoOp;
import org.firstinspires.ftc.teamcode.atlas.atlasauto.AtlasParameters;

@TeleOp(name="AutoTestSilly")
public class AutoTest extends AtlasAutoOp {
    @Override
    public AtlasParameters create() {
        Chassis chassis = new Chassis(hardwareMap);
        return new AtlasParameters(chassis, 1, 1);
    }

    @Override
    public void perform() {
        moveTo(1, 1)
                .thenMove(0, 1)
                .andRotateTo(180)

                .thenMove(0, 0)
                .andWaitForComplete()

                .thenMove(0, 1)

                .thenMove(0, 0)
                .perform();
    }
}
