
/**
 * @author adrazhi
 * COS 470 | Artificial Intelligence
 * Final Project - Chess Program using Artificial Intelligence
 * University of Maine | 05/09/2014
 *
 */

import java.lang.String;
public class BoardPiece {
	// representations for "king", "queen", "pawn", "bishop", "knight", "rook"
	String piece_type;
	
	//piece color: b or w
	String piece_color;
	
	//Board coordinates, bottom left is -1,1
	int xCoord;
	int yCoord;
	int piece_key;
	
	boolean moved_piece = false;
	int moved_turn;
	boolean enps = false;
	public BoardPiece(String t, String c, int xcor, int ycor) {
		piece_type = t;
		piece_color = c;
		xCoord = xcor;
		yCoord = ycor;
		piece_key = 10*xCoord + yCoord;
		
	}
	public void refresh() {
		piece_key = 10*xCoord + yCoord;
	}

}
