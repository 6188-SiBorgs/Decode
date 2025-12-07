package org.firstinspires.ftc.teamcode.utils;

public enum Motif {
    PURPLE_GREEN_PURPLE(1),
    PURPLE_PURPLE_GREEN(2),
    GREEN_PURPLE_PURPLE(0);

    private final int index;
    Motif(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
