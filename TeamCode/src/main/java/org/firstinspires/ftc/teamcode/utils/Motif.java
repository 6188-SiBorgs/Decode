package org.firstinspires.ftc.teamcode.utils;

public enum Motif {
    GREEN_PURPLE_PURPLE(0),
    PURPLE_GREEN_PURPLE(1),
    PURPLE_PURPLE_GREEN(2);

    private final int index;
    Motif(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static Motif getMotifFromIndex(int id) {
        switch (id) {
            case 22:
                return PURPLE_GREEN_PURPLE;
            case 23:
                return PURPLE_PURPLE_GREEN;
        }
        return GREEN_PURPLE_PURPLE;
    }
}
