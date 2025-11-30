package org.firstinspires.ftc.teamcode.utils;

public enum Motifs {
    PURPLE_GREEN_PURPLE(22),
    PURPLE_PURPLE_GREEN(23),
    GREEN_PURPLE_PURPLE(21);

    private final int code;

    Motifs(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
