package move;

public class MoveSwitcher {
	
	public static char switchMove(char mark) {
		if (mark == 'X') 
			return 'O';
		
		else
			return 'X';
	}

}
