package game;

public class GameState {
	public char[][] state = new char[9][9];
	public char[] globalState = new char[9];
	
	public void initialize() {
		
		for (int i = 0; i < 9; i++) {
		    for (int j = 0; j < 9; j++) {
		        state[i][j] = ' ';
		    		}
		    globalState[i] = ' '; 
			}
	
		}
	
	
}
