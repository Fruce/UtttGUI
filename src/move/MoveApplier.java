package move;

public class MoveApplier {
	
	public static void updateMove(char[][] state, int bigIndex, int smallIndex,char mark) {
		state[bigIndex][smallIndex] = mark;
	}

}
