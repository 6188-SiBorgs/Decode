package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DigitalLED {
    public enum Color {
        NONE,
        GREEN,
        RED,
        AMBER
    }
    private DigitalChannel redLED;
    private DigitalChannel greenLED;
    public Color color = Color.NONE;

    public DigitalLED(HardwareMap hardwareMap, String name) {
        redLED = hardwareMap.get(DigitalChannel.class, name + "Red");
        greenLED = hardwareMap.get(DigitalChannel.class, name + "Green");
        redLED.setMode(DigitalChannel.Mode.OUTPUT);
        greenLED.setMode(DigitalChannel.Mode.OUTPUT);
    }

    public void update() {
        switch (color) {
            case NONE:
                redLED.setState(false);
                greenLED.setState(false);
                break;
            case RED:
                redLED.setState(true);
                greenLED.setState(false);
                break;
            case AMBER:
                redLED.setState(true);
                greenLED.setState(true);
                break;
            case GREEN:
                redLED.setState(false);
                greenLED.setState(true);
                break;
        }
    }
}
