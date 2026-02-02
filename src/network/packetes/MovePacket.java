package network.packetes;

import java.io.Serializable;

public class MovePacket implements Serializable {
    public int big;
    public int small;

    public MovePacket(int big, int small) {
        this.big = big;
        this.small = small;
    }
}
