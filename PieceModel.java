
/**
 * @author adrazhi
 * COS 470 | Artificial Intelligence
 * Final Project - Chess Program using Artificial Intelligence
 * University of Maine | 05/09/2014
 *
 */
import java.util.*;
import java.lang.Math;
/**
 * This class is responsible for controlling the movements
 * for the pieces by manipulating their arrayLists
 * 
 * @author adrazhi
 *
 */
public class PieceModel {
	ArrayList<BoardPiece> board_pieces;
	ArrayList<BoardPiece> oponents;
	boolean selected_value;
	BoardPiece selected_piece;
	String piece_color;
	int pawn_y;
	int pawn_direction;
	PieceModel oponent_controller;
	BoardPiece king;
	int count_turn = 0;
	public PieceModel(ArrayList<BoardPiece> board_p, ArrayList<BoardPiece> board_pe) {
		board_pieces = board_p;
		oponents = board_pe;
		selected_value = false;
		piece_color = board_pieces.get(0).piece_color;
		if(piece_color == "white") {
			pawn_y = 2;
			pawn_direction = 1;
		}
		else {
			pawn_y = 7;
			pawn_direction = -1;
		}
		for(int i = 0; i < board_pieces.size(); i++) {
			if(board_pieces.get(i).piece_type == "king") {
				king = board_pieces.get(i);
			}
		}
	}
	
	//sets the selected piece, given its coordinates, x and y
	public void setSelected(int xcor, int ycor) {
		int select_key_value = 10*xcor + ycor;
		for(int i = 0; i < board_pieces.size(); i++) {
			if(board_pieces.get(i).piece_key == select_key_value) {
				selected_value = true;
				selected_piece = board_pieces.get(i);
				return;
			}
		}
	}
	
	//moves the selected piece to specified square and captures oponent's piece
	public void moveSelected(int xcor, int ycor) {
		if(selected_piece == king) {
			if(xcor == king.xCoord+2) {
				king.xCoord = xcor;
				king.refresh();
				BoardPiece rook = check_piece_overlap(8, king.yCoord);
				rook.xCoord = 6;
				rook.refresh();
				king.moved_piece = true;
				rook.moved_piece = true;
				selected_piece = null;
				selected_value = false;
				return;
			}
			if(xcor == king.xCoord-3) {
				king.xCoord = xcor;
				king.refresh();
				BoardPiece rook = check_piece_overlap(1, king.yCoord);
				rook.xCoord = 3;
				rook.refresh();
				king.moved_piece = true;
				rook.moved_piece = true;
				selected_piece = null;
				selected_value = false;
				return;
			}
		}
		
		if(selected_piece.piece_type == "pawn" && Math.abs(selected_piece.yCoord-ycor) == 2) {
			selected_piece.moved_turn = count_turn;
		}
		
		//castling 
		selected_piece.moved_piece = true;
		BoardPiece captured_piece = check_oponent_overlap(xcor, ycor);
		if(captured_piece != null) {
			oponents.remove(captured_piece);
		}
		
		if(selected_piece.piece_type == "pawn" && captured_piece == null) {
			if(selected_piece.xCoord != xcor) {
				captured_piece = check_oponent_overlap(xcor, selected_piece.yCoord);
				if(captured_piece != null) {
					oponents.remove(captured_piece);
				}
			}
		}
		
		selected_piece.xCoord = xcor;
		selected_piece.yCoord = ycor;
		selected_piece.refresh();
		//this is when the pawn goes to a "promotion"
		//can become, for example a queen
		if(selected_piece.piece_type == "pawn") {
			if(ycor == pawn_y + (6*pawn_direction)) {
				selected_piece.piece_type = "queen";
			}
		}
		selected_piece = null;
		selected_value = false;
	}
	
	//Check if a move made by a piece is a valid move, returns true if it is a valid move
	public boolean check_valid_piece(int xcor, int ycor) { 
		if(selected_piece == king) {
			if(ycor == king.yCoord && xcor == king.xCoord + 2) {
				BoardPiece rook = check_piece_overlap(8, king.yCoord);
				if(rook == null) {
					return false;
				}
				if(king.moved_piece == false && rook.moved_piece == false) {
					for(int i = 6; i < 8; i++) {
						if(check_piece_overlap(i, king.yCoord) != null) {
							return false;
						}
						if(check_oponent_overlap(i, king.yCoord) != null) {
							return false;
						}
					}
					if(checkAll()) {
						return false;
					}
					king.xCoord++;
					king.refresh();
					if(checkAll()) {
						king.xCoord -= 1;
						king.refresh();
						return false;
					}
					king.xCoord++;
					king.refresh();
					if(checkAll()) {
						king.xCoord-=2;
						king.refresh();
						return false;
					}
					king.xCoord -= 2;
					king.refresh();
					//.out.println("castle!");
					return true;
				}
			}
			if(ycor == king.yCoord && xcor == king.xCoord-3) {
				//make sure neither have moved
				BoardPiece rook = check_piece_overlap(1, king.yCoord);
				if(rook == null) {
					return false;
				}
				if(king.moved_piece == false && rook.moved_piece == false) {
					//make sure not passing through any pieces
					for(int i = 4; i > 1; i--) {
						if(check_piece_overlap(i, king.yCoord) != null) {
							return false;
						}
						if(check_oponent_overlap(i, king.yCoord) != null) {
							return false;
						}
					}
					if(checkAll()) {
						return false;
					}
					king.xCoord--;
					king.refresh();
					if(checkAll()) {
						king.xCoord += 1;
						king.refresh();
						return false;
					}
					king.xCoord--;
					king.refresh();
					if(checkAll()) {
						king.xCoord += 2;
						king.refresh();
						return false;
					}
					king.xCoord--;
					king.refresh();
					if(checkAll()) {
						king.xCoord += 3;
						king.refresh();
						return false;
					}
					king.xCoord += 3;
					king.refresh();
					System.out.println("castle!");
					return true;
				}
			}
		}

		if(check_move_legal(selected_piece, xcor, ycor) == false) {
			return false;
		}
		int currx = selected_piece.xCoord;
		int curry = selected_piece.yCoord;
		selected_piece.xCoord = xcor;
		selected_piece.yCoord = ycor;
		selected_piece.refresh();
		BoardPiece captured_oponent = check_oponent_overlap(xcor, ycor);
		int captured_index = 0;
		if(captured_oponent != null) {
			captured_index = oponents.indexOf(captured_oponent);
			oponents.remove(captured_oponent);
		}
		if(checkAll()) {
			//reset movement changes and captures before returning false
			selected_piece.xCoord = currx;
			selected_piece.yCoord = curry;
			selected_piece.refresh();
			if(captured_oponent != null) {
				oponents.add(captured_index, captured_oponent);
			}
			return false;
		}
		//reset changes since this method is just meant to check if a move is valid 
		//without actually executing it
		selected_piece.xCoord = currx;
		selected_piece.yCoord = curry;
		selected_piece.refresh();
		if(captured_oponent != null) {
			oponents.add(captured_index, captured_oponent);
		}
		return true;
	}

	//checks if a square is occupied by a piece, returns piece if it is
	public BoardPiece check_piece_overlap(int xcor, int ycor) { //square coordinates that are being checked
		int temp_key_val = 10*xcor + ycor;
		for(int i = 0; i < board_pieces.size(); i++) {
			if(temp_key_val == board_pieces.get(i).piece_key) {
				return board_pieces.get(i);
			}
		}
		return null;
	}
	
	//checks if an enemy occupies a square, returns if it does
	public BoardPiece check_oponent_overlap(int xcor, int ycor) { 
		int temp_key_val = 10*xcor + ycor;
		for(int i = 0; i < oponents.size(); i++) {
			if(temp_key_val == oponents.get(i).piece_key) {
				return oponents.get(i);
			}
		}
		return null;
	}
	
	//checks that the movement about to happen is a valid chess move
	public boolean check_move_legal(BoardPiece bp, int xcor, int ycor) { 
		if(check_piece_overlap(xcor, ycor) != null) {
			return false;
		}
		int curr_x, curr_y;
		curr_x = bp.xCoord;
		curr_y = bp.yCoord;
		if(xcor == curr_x && ycor == curr_y) {
			return false;
		}
		if(bp.piece_type == "pawn") {
			if(curr_y == pawn_y) {
				if(ycor == curr_y+(pawn_direction*2)) {
					if(xcor == curr_x && check_oponent_overlap(xcor, ycor) == null) {
						return true;
					}
				}
			}
			if(ycor == curr_y + pawn_direction) {
				if(xcor == curr_x) {
					if(check_oponent_overlap(curr_x, curr_y+pawn_direction) == null) {
						return true;
					}
				}
				if(xcor == curr_x + 1) {
					if(check_oponent_overlap(curr_x+1, curr_y+pawn_direction) != null) {
						return true;
					}
					
					if(curr_y == pawn_y+(3 * pawn_direction)) {
						if(check_oponent_overlap(curr_x + 1, curr_y) != null) {
							if(piece_color == "white") { 
								if(check_oponent_overlap(curr_x + 1, curr_y).moved_turn == count_turn-1) {
									return true;
								}
									
							}
							if(piece_color == "black") {
								if(check_oponent_overlap(curr_x+1, curr_y).moved_turn == count_turn) {
									return true;
								}
							}
						}
					}
				}
				if(xcor == curr_x-1) {
					if(check_oponent_overlap(curr_x-1, curr_y+pawn_direction) != null) {
						return true;
					}

					if(curr_y == pawn_y+(3 * pawn_direction)) {
						if(check_oponent_overlap(curr_x - 1, curr_y) != null) {
							if(piece_color == "white") { 
								if(check_oponent_overlap(curr_x-1, curr_y).moved_turn == count_turn-1) {
									return true;
								}	
							}
							if(piece_color == "black") {
								if(check_oponent_overlap(curr_x-1, curr_y).moved_turn == count_turn) {
									return true;
								}
							}
						}
					}
				}
				
			}
		}
		if(bp.piece_type == "king") {
			if(xcor == curr_x+1 || xcor == curr_x-1 || xcor == curr_x) {
				if(ycor == curr_y+1 || ycor == curr_y-1 || ycor == curr_y) {
					if(check_piece_overlap(xcor, ycor) == null) {
						return true;
					}
				}
			}
		}
		if(bp.piece_type == "bishop") {
			int dx_value = Math.abs(curr_x-xcor);
			int dy_value = Math.abs(curr_y-ycor);
			//check if it crosses path
			if(dx_value == dy_value) {
				int x_sign = (int) Math.signum(xcor-curr_x);
				int y_sign = (int) Math.signum(ycor-curr_y);
				int x_squares[] = new int[dx_value-1];
				int y_squares[] = new int[dx_value-1];
				//create array of squares that it passes over
				for(int i = 1; i < dx_value; i++) {
					int tempX = curr_x+(x_sign*i);
					int tempY = curr_y+(y_sign*i);
					x_squares[i-1] = tempX;
					y_squares[i-1] = tempY;
				}
				//check every square
				for(int i = 0; i < x_squares.length; i++) {
					if(check_piece_overlap(x_squares[i], y_squares[i]) != null) {
						return false;
					}
					if(check_oponent_overlap(x_squares[i], y_squares[i]) != null) {
						return false;
					}
				}
				return true;
			}
		}
		
		if(bp.piece_type == "knight") {
			int dx_value = Math.abs(curr_x-xcor);
			int dy_value = Math.abs(curr_y-ycor);
			if((dx_value == 1 && dy_value == 2) || (dx_value == 2 && dy_value == 1)) {
				return true;
			}
		}
		
		if(bp.piece_type == "rook") {
			int dx_value = Math.abs(curr_x-xcor);
			int dy_value = Math.abs(curr_y-ycor);
			if(dx_value == 0 || dy_value == 0) {
				if(dx_value == 0) {
					int y_sign = (int) Math.signum(ycor-curr_y);
					int y_squares[] = new int[dy_value-1];
					for(int i = 1; i < dy_value; i++) {
						int temp_y_value = curr_y+(y_sign*i);
						y_squares[i-1] = temp_y_value;
					}
					for(int i = 0; i < y_squares.length; i++) {
						if(check_piece_overlap(curr_x, y_squares[i]) != null) {
							return false;
						}
						if(check_oponent_overlap(curr_x, y_squares[i]) != null) {
							return false;
						}
					}
				}
				if(dy_value == 0) {
					int x_sign = (int) Math.signum(xcor-curr_x);
					int x_squares[] = new int[dx_value-1];
					for(int i = 1; i < dx_value; i++) {
						int temp_x_value = curr_x+(x_sign*i);
						x_squares[i-1] = temp_x_value;
					}
					for(int i = 0; i < x_squares.length; i++) {
						if(check_piece_overlap(x_squares[i], curr_y)!= null) {
							return false;
						}
						if(check_oponent_overlap(x_squares[i], curr_y) != null) {
							return false;
						}
					}
				}
				return true;
			}
		}
		
		if(bp.piece_type == "queen") {
			int dx_value = Math.abs(curr_x-xcor);
			int dy_value = Math.abs(curr_y-ycor);
			//check if it crosses path
			if(dx_value == dy_value) {
				int x_sign = (int) Math.signum(xcor-curr_x);
				int y_sign = (int) Math.signum(ycor-curr_y);
				int x_squares[] = new int[dx_value-1];
				int y_squares[] = new int[dx_value-1];
				//create array of squares that it passes over
				for(int i = 1; i < dx_value; i++) {
					int temp_x_value = curr_x+(x_sign*i);
					int temp_y_value = curr_y+(y_sign*i);
					x_squares[i-1] = temp_x_value;
					y_squares[i-1] = temp_y_value;
				}
				//check every square
				for(int i = 0; i < x_squares.length; i++) {

					if(check_piece_overlap(x_squares[i], y_squares[i]) != null) {
						return false;
					}
					if(check_oponent_overlap(x_squares[i], y_squares[i]) != null) {
						return false;
					}
				}
				return true;
			}
			dx_value = Math.abs(curr_x-xcor);
			dy_value = Math.abs(curr_y-ycor);
			if(dx_value == 0 || dy_value == 0) {
				if(dx_value == 0) {
					int y_sign = (int) Math.signum(ycor-curr_y);
					int y_squares[] = new int[dy_value-1];
					for(int i = 1; i < dy_value; i++) {
						int temp_y_value = curr_y+(y_sign*i);
						y_squares[i-1] = temp_y_value;
					}
					for(int i = 0; i < y_squares.length; i++) {
						if(check_piece_overlap(curr_x, y_squares[i]) != null) {
							return false;
						}
						if(check_oponent_overlap(curr_x, y_squares[i]) != null) {
							return false;
						}
					}
				}
				if(dy_value == 0) {
					int x_sign = (int) Math.signum(xcor-curr_x);
					int x_squares[] = new int[dx_value-1];
					for(int i = 1; i < dx_value; i++) {
						int temp_x_value = curr_x+(x_sign*i);
						x_squares[i-1] = temp_x_value;
					}
					for(int i = 0; i < x_squares.length; i++) {
						if(check_piece_overlap(x_squares[i], curr_y) != null) {
							return false;
						}
						if(check_oponent_overlap(x_squares[i], curr_y) != null) {
							return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Returns an arraylist of the swuares that can be attacked by a piece
	 * Only works with own pieces, to check oponent's pieces
	 */
	public ArrayList<SquarePosition> get_attacking_pos(BoardPiece board_p) {
		ArrayList<SquarePosition> attacking_pos = new ArrayList<SquarePosition>();
		int xcor = board_p.xCoord;
		int ycor = board_p.yCoord;
		if(board_p.piece_type == "pawn") {
			int piece_direction;
			if(board_p.piece_color == "white") {
				piece_direction = 1;
			}
			else {
				piece_direction = -1;
			}
			for(int i = -1; i < 2; i+=2) {
				if(xcor+i > 0 && xcor+i < 9 && ycor + piece_direction > 0 && ycor + piece_direction < 9) {
					if(check_piece_overlap(xcor + i, ycor + piece_direction) == null) {
						attacking_pos.add(new SquarePosition(xcor + i, ycor+piece_direction));
					}
				}
			}
			return attacking_pos;
		}
		if(board_p.piece_type == "king") {
			for(int i = -1; i < 2; i++) {
				for(int k = -1; k < 2; k++) {
					int new_xcor = xcor + i;
					int new_ycor = ycor + k;
					if(new_xcor > 0 && new_xcor < 9 && new_ycor > 0 && new_ycor < 9) {
						if(check_piece_overlap(new_xcor, new_ycor) == null) {
							attacking_pos.add(new SquarePosition(new_xcor, new_ycor));
						}
					}
				}
			}
			return attacking_pos;
		}
		if(board_p.piece_type == "bishop") {
			//nested for loop checks combination of directions
			for(int x_direct = -1; x_direct < 2; x_direct += 2) { 
				for(int y_direct = -1; y_direct < 2; y_direct += 2) {
					int move_dist = 1;
					while(true) {
						int new_xvalue = xcor + (move_dist*x_direct);
						int new_yvalue = ycor + (move_dist*y_direct);
						if(new_xvalue > 8 || new_xvalue < 1 || new_yvalue > 8 || new_yvalue < 1) {
							break;
						}
						if(check_piece_overlap(new_xvalue, new_yvalue) != null) {
							break;
						}
						if(check_oponent_overlap(new_xvalue, new_yvalue) != null) {
							attacking_pos.add(new SquarePosition(new_xvalue, new_yvalue));
							break;
						}
						attacking_pos.add(new SquarePosition(new_xvalue, new_yvalue));
						move_dist++;
					}
				}
			}
			return attacking_pos;
		}
		
		if(board_p.piece_type == "knight") {
			//combinations for the piece (knight) directions
			for(int x_direct = -1; x_direct < 2; x_direct += 2) { 
				for(int y_direct = -1; y_direct < 2; y_direct += 2) {
					//y versus x combinations
					for(int x_move = 1; x_move < 3; x_move++) {
						int y_move;
						if(x_move == 1) {
							y_move = 2;
						}
						else {
							y_move = 1;
						}
						int new_x_value = xcor + (x_direct*x_move);
						int new_y_value = ycor + (y_direct*y_move);
						if(new_x_value > 8 || new_x_value < 1 || new_y_value > 8 || new_y_value < 1) {

						}
						else if(check_piece_overlap(new_x_value, new_y_value) != null) {

						}
						else if(check_oponent_overlap(new_x_value, new_y_value) != null) {
							attacking_pos.add(new SquarePosition(new_x_value, new_y_value));
							break;
						}
						else {
							attacking_pos.add(new SquarePosition(new_x_value, new_y_value));
						}
					}
				}
			}
			return attacking_pos;
		}
		
		if(board_p.piece_type == "rook") {
			//combinations for the rook direction
			for(int x_direct = -1; x_direct < 2; x_direct++) { 
				for(int y_direct = -1; y_direct < 2; y_direct++) {
					//one of them has to be 0
					if(y_direct == 0 || x_direct == 0) {
						int moved_dist = 1;
						while(true) {
							int new_xvalue = xcor + (x_direct*moved_dist);
							int new_yvalue = ycor + (y_direct*moved_dist);
							if(new_xvalue > 8 || new_xvalue < 1 || new_yvalue > 8 || new_yvalue < 1) {
								break;
							}
							if(check_piece_overlap(new_xvalue, new_yvalue) != null) {
								break;
							}
							if(check_oponent_overlap(new_xvalue, new_yvalue) != null) {
								attacking_pos.add(new SquarePosition(new_xvalue, new_yvalue));
								break;
							}
							attacking_pos.add(new SquarePosition(new_xvalue, new_yvalue));
							moved_dist++;
						}
					}
				}
			}
			return attacking_pos;
		}
		
		if(board_p.piece_type == "queen") {
			//combinations for queen directions
			for(int x_direct = -1; x_direct < 2; x_direct++) { 
				for(int y_direct = -1; y_direct < 2; y_direct++) {
					int moved_dist = 1;
					while(true) {
						int new_xvalue = xcor + (x_direct*moved_dist);
						int new_yvalue = ycor + (y_direct*moved_dist);
						if(new_xvalue > 8 || new_xvalue < 1 || new_yvalue > 8 || new_yvalue < 1) {
							break;
						}
						if(check_piece_overlap(new_xvalue, new_yvalue) != null) {
							break;
						}
						if(check_oponent_overlap(new_xvalue, new_yvalue) != null) {
							attacking_pos.add(new SquarePosition(new_xvalue, new_yvalue));
							break;
						}
						attacking_pos.add(new SquarePosition(new_xvalue, new_yvalue));
						moved_dist++;
					}

				}
			}
			return attacking_pos;
		}
		return null;
	}
	
	//returns true if king is in check
	public boolean checkAll() {
		for(int i = 0; i < oponents.size(); i++) {
			ArrayList<SquarePosition> attacked = oponent_controller.get_attacking_pos(oponents.get(i));
			for(int k = 0; k < attacked.size(); k++) {
				if(attacked.get(k).xCoord == king.xCoord && attacked.get(k).yCoord == king.yCoord) {
					return true;
				}
			}
		}
		return false;
	}
}
