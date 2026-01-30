package move;

import game.GameController;
import game.MoveResult;

public class MoveValidity {
	
	//if value already exists
	private static MoveResult isCellEmpty(char[][] state, int b, int s) {
		
	    if (state[b][s] != ' ') {
	        //System.out.println("Cell is already occupied!");
	        return MoveResult.INVALID;
	    }
	    return MoveResult.VALID;
	}

	private static MoveResult isValidArea(
			GameController controller,
			char[] globalState, 
			int b, 
			int ls
			) {
		
		// first move.
		if (ls == -1)
			return MoveResult.VALID; 
		
		// intended board is free AND last board was deadboard.
		if (globalState[b] == ' ' && controller.isDeadBoard(ls)) {

			return MoveResult.VALID;
		}
		
		// intended board already won
		if (globalState[b] != ' ') {
			//System.out.println("The Global Cell is Already Won. Try Anywhere Else ~");
			return MoveResult.INVALID;
		}

		// must play in the directed board
		if (ls == b) {
			return MoveResult.VALID;
		}
		
		else {
			//System.out.println("not Valid Area! Enter at Global Cell " +(ls+1)); 
			return MoveResult.INVALID;
		}
	}

	
	    // master validation
	    public static MoveResult isValid(
	    			GameController controller,
	            char[] globalState,
	            char[][] state,
	            int b,
	            int s,
	            int lastBig,
	            int lastSmall
	    ) {
	
	        MoveResult areaCheck = isValidArea(controller, globalState, b, lastSmall);
	        if (areaCheck != MoveResult.VALID) {
	            return areaCheck;
	        }
	
	        MoveResult cellCheck = isCellEmpty(state, b, s);
	        if (cellCheck != MoveResult.VALID) {
	            return cellCheck;
	        }
	
	        return MoveResult.VALID;
	    }

}
