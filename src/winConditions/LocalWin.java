package winConditions;

public class LocalWin {

	public static boolean isLocalWon(char[][] state, int b) {

	    // rows
	    for (int i = 0; i <= 6; i += 3) {
	        if (state[b][i] == state[b][i + 1] &&
	            state[b][i] == state[b][i + 2] &&
	            state[b][i] != ' ') {
	            return true;
	        }
	    }

	    // columns
	    for (int i = 0; i <= 2; i++) {
	        if (state[b][i] == state[b][i + 3] &&
	            state[b][i] == state[b][i + 6] &&
	            state[b][i] != ' ') {
	            return true;
	        }
	    }

	    // diagonals
	    return (state[b][0] == state[b][4] &&
	            state[b][0] == state[b][8] &&
	            state[b][0] != ' ')
	        ||
	           (state[b][2] == state[b][4] &&
	            state[b][2] == state[b][6] &&
	            state[b][2] != ' ');
	}

}
