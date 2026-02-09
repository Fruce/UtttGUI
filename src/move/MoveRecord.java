package move;

public final class MoveRecord {
    public final int big;
    public final int small;
    public final char previousMark;
    public final int previousPlayer;
    public final int prevLastBig;
    public final int prevLastSmall;
    public final char prevGlobalState;
    public final boolean prevGameOver;

    public MoveRecord(
        int big,
        int small,
        char previousMark,
        int previousPlayer,
        int prevLastBig,
        int prevLastSmall,
        char prevGlobalState,
        boolean prevGameOver
    ) {
        this.big = big;
        this.small = small;
        this.previousMark = previousMark;
        this.previousPlayer = previousPlayer;
        this.prevLastBig = prevLastBig;
        this.prevLastSmall = prevLastSmall;
        this.prevGlobalState = prevGlobalState;
        this.prevGameOver = prevGameOver;
    }
}

