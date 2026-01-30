package winConditions;

public class GlobalWin {
	public static boolean isGlobalWon(char[] globalState) {

	    // rows
	    for (int i = 0; i <= 6; i += 3) {
	        if (globalState[i] == globalState[i + 1] &&
	            globalState[i] == globalState[i + 2] &&
	            globalState[i] != ' ') {
	            return true;
	        }
	    }

	    // columns
	    for (int i = 0; i <= 2; i++) {
	        if (globalState[i] == globalState[i + 3] &&
	            globalState[i] == globalState[i + 6] &&
	            globalState[i] != ' ') {
	            return true;
	        }
	    }

	    // diagonals
	    if (
	        (globalState[0] == globalState[4] &&
	         globalState[0] == globalState[8] &&
	         globalState[0] != ' ')
	        ||
	        (globalState[2] == globalState[4] &&
	         globalState[2] == globalState[6] &&
	         globalState[2] != ' ')
	    ) {
	        return true;
	    }

	    return false;
	}
}
