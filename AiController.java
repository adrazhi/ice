/**
 * 
 * @author adrazhi
 * COS 470 | Artificial Intelligence
 * Final Project - Chess Program using Artificial Intelligence
 * University of Maine | 05/09/2014
 *
 */



import java.lang.String;
import java.util.*;


/**
 * This class is responsible for controlling the AI behind the chess program
 * @author NETWORK-ROUTER
 *
 */
public class AiController {
	
	BoardTree boardGameTree;
	PieceModel controller;
	double move_time;
	Hashtable<String, Integer> board_table; 
	Hashtable<String, Integer> m_table;
	Hashtable<String, Integer[][]> position_matrix;
	ArrayList<BoardPiece> all_pieces;
	ArrayList<BoardPiece> oponents;
	String color;
	int side;
	int pawn_direction;
	int pawn_m, king_m, knight, queen_m, bishop_m, rook_m;
	int m_weight;
	int position_weight;
	int mb_weight;
	int st_weight;
	int end_count_pieces;
	Integer pawn_matrix[][];
	Integer knight_matrix[][];
	Integer king_matrix[][];
	Integer bishop_matrix[][];
	MainController mc;

	/**
	 * AiController constructor
	 * @param c
	 * @param mainC
	 */
	public AiController(PieceModel c, MainController mainC) {
		mc = mainC;
		controller = c;
		all_pieces = controller.board_pieces;
		oponents = controller.oponents;
		board_table = new Hashtable<String, Integer>();
		m_table = new Hashtable<String, Integer>();
		pawn_m = 3;
		king_m = 99;
		knight = 5;
		queen_m = 13;
		bishop_m = 5;
		rook_m = 8;
		m_weight = 20;
		position_weight = 5;
		st_weight = 5;
		mb_weight = 8;
		end_count_pieces = 15;
		//put the pieces into the table
		m_table.put("pawn", pawn_m);
		m_table.put("king", king_m);
		m_table.put("knight", knight);
		m_table.put("queen", queen_m);
		m_table.put("bishop", bishop_m);
		m_table.put("rook", rook_m);
		if (controller.pawn_y == 2) {
			side = 1;
			pawn_direction = 1;
			color = "white";
		} else {
			side = 8;
			pawn_direction = -1;
			color = "black";

		}
	
		// Create position matrixes for knight, bishop, king
		initKnightMatrix();
		initKingMatrix();
		startBishopMat();
		
		position_matrix = new Hashtable<String, Integer[][]>();
		position_matrix.put("knight", knight_matrix);
		position_matrix.put("king", king_matrix);
		position_matrix.put("bishop", bishop_matrix);
		boardGameTree = new BoardTree();
	}

	public void initKnightMatrix() {
		// matrix to represent the knight
		knight_matrix = new Integer[9][9];
		for (int i = 1; i < 9; i += 7) {
			knight_matrix[i][1] = -10;
			knight_matrix[i][2] = -8;
			knight_matrix[i][3] = -6;
			knight_matrix[i][4] = -4;
			knight_matrix[i][5] = -4;
			knight_matrix[i][6] = -6;
			knight_matrix[i][7] = -8;
			knight_matrix[i][8] = -10;
		}
		for (int i = 2; i < 9; i += 5) {
			knight_matrix[i][1] = -5;
			knight_matrix[i][2] = -3;
			knight_matrix[i][3] = 0;
			knight_matrix[i][4] = 0;
			knight_matrix[i][5] = 0;
			knight_matrix[i][6] = 0;
			knight_matrix[i][7] = -3;
			knight_matrix[i][8] = -5;
		}
		knight_matrix[side + pawn_direction][4] = 3; // good defense spots
		knight_matrix[side + pawn_direction][5] = 3;
		for (int i = 3; i < 7; i += 3) {
			knight_matrix[i][1] = -5;
			knight_matrix[i][2] = -1;
			knight_matrix[i][3] = 0;
			knight_matrix[i][4] = 5;
			knight_matrix[i][5] = 5;
			knight_matrix[i][6] = 0;
			knight_matrix[i][7] = -1;
			knight_matrix[i][8] = -5;
		}
		for (int i = 4; i < 6; i += 1) {
			knight_matrix[i][1] = -5;
			knight_matrix[i][2] = -1;
			knight_matrix[i][3] = 5;
			knight_matrix[i][4] = 7;
			knight_matrix[i][5] = 7;
			knight_matrix[i][6] = 5;
			knight_matrix[i][7] = -1;
			knight_matrix[i][8] = -5;
		}
	}

	public void initKingMatrix() {
		king_matrix = new Integer[9][9];
		for (int x = 1; x < 9; x++) {
			for (int y = 1; y < 9; y++) {
				king_matrix[x][y] = -2;
			}
		}

		king_matrix[1][side] = 10;
		king_matrix[2][side] = 12;
		king_matrix[3][side] = 8;
		king_matrix[4][side] = 0;
		king_matrix[5][side] = 0;
		king_matrix[6][side] = 10;
		king_matrix[1][side] = 15; 
		king_matrix[1][side] = 13;
	}

	public void startBishopMat() {
		bishop_matrix = new Integer[9][9];
		for (int x = 1; x < 9; x += 7) {
			for (int y = 1; y < 9; y++) {
				bishop_matrix[x][y] = -15;
			}
		}
		for (int x = 2; x < 9; x += 5) {
			bishop_matrix[x][1] = -5;
			bishop_matrix[x][2] = 3;
			bishop_matrix[x][3] = 0;
			bishop_matrix[x][4] = 0;
			bishop_matrix[x][5] = 0;
			bishop_matrix[x][6] = 0;
			bishop_matrix[x][7] = 3;
			bishop_matrix[x][8] = -5;
		}
		for (int x = 3; x < 7; x += 3) {
			bishop_matrix[x][1] = 0;
			bishop_matrix[x][2] = 3;
			bishop_matrix[x][3] = 5;
			bishop_matrix[x][4] = 5;
			bishop_matrix[x][5] = 5;
			bishop_matrix[x][6] = 5;
			bishop_matrix[x][7] = 3;
			bishop_matrix[x][8] = 0;
		}
		for (int x = 4; x < 6; x++) {
			bishop_matrix[x][1] = 2;
			bishop_matrix[x][2] = 4;
			bishop_matrix[x][3] = 6;
			bishop_matrix[x][4] = 10;
			bishop_matrix[x][5] = 10;
			bishop_matrix[x][6] = 6;
			bishop_matrix[x][7] = 4;
			bishop_matrix[x][8] = 2;
		}
	}

	/*
	 * Board evaluation function
	 * 
	 */
	public int evaluateGameBoard() {
		int score = 0;
		// scores for the positions
		int mt_score = 0;
		int ps_score = 0;
		int mb_score = 0;

		for (int i = 0; i < all_pieces.size(); i++) {
			BoardPiece piece = all_pieces.get(i);
			mt_score += m_table.get(piece.piece_type);
			if (all_pieces.size() + oponents.size() > end_count_pieces) { 
				if (position_matrix.get(piece.piece_type) != null) {
					ps_score += position_matrix.get(piece.piece_type)[piece.xCoord][piece.yCoord];
				}
			}

			ArrayList<SquarePosition> squares = controller.get_attacking_pos(piece);
			for (int k = 0; k < squares.size(); k++) {
				mb_score++;
				if (squares.get(k).yCoord > side + (3 * pawn_direction)) {
					mb_score++; 
				}
			}

		}
		
		// subtract scores from the oponent
		for (int i = 0; i < oponents.size(); i++) {
			BoardPiece piece = oponents.get(i);
			mt_score -= m_table.get(piece.piece_type);
		}

		mt_score *= m_weight;
		ps_score *= position_weight;
		mb_score *= mb_weight;
		score += mt_score;
		score += ps_score;
		score += mb_score;
		// pawn structure
		int pawn_structure = evaluatePawn();
		pawn_structure *= st_weight;
		score += pawn_structure;
		// System.out.println(evalScore);
		return score;
	}

	// evaluates the pawn structure
	public int evaluatePawn() {
		int score = 0;
		ArrayList<BoardPiece> pos_pawns = new ArrayList<BoardPiece>();
		ArrayList<BoardPiece> oponent_pawns = new ArrayList<BoardPiece>();
		ArrayList<Integer> posX = new ArrayList<Integer>();
		ArrayList<Integer> oponentX = new ArrayList<Integer>();
		ArrayList<Integer> posY = new ArrayList<Integer>();
		ArrayList<Integer> oponentY = new ArrayList<Integer>();
		for (int i = 0; i < all_pieces.size(); i++) {
			if (all_pieces.get(i).piece_type == "pawn") {
				BoardPiece pawn = all_pieces.get(i);
				pos_pawns.add(pawn);
				posX.add(pawn.xCoord);
				posY.add(pawn.yCoord);
			}
		}
		for (int i = 0; i < oponents.size(); i++) {
			if (oponents.get(i).piece_type == "pawn") {
				BoardPiece pawn = oponents.get(i);
				oponent_pawns.add(pawn);
				oponentX.add(pawn.xCoord);
				oponentY.add(pawn.yCoord);
			}
		}
		
		// check if there are any pawns in the center
		int center_squares = 3;
		for (int i = 0; i < posX.size(); i++) {
			if (posX.get(i) == 4 || posX.get(i) == 5) {
				if (posY.get(i) == 4 || posY.get(i) == 5) {
					score += center_squares;
				}
			}
		}
		
		// check to see if there's any enemies in the center of the board
		for (int i = 0; i < oponentX.size(); i++) {
			if (oponentX.get(i) == 4 || oponentX.get(i) == 5) {
				if (oponentY.get(i) == 4 || oponentY.get(i) == 5) {
					score -= center_squares; //subtract the scores from the oponent
				}
			}
		}
		
		// check to see if there are any isolated pawns
		int isolated_pawn_score = -1;
		for (int i = 0; i < posX.size(); i++) {
			int pawnX = posX.get(i);
			boolean leftEmpty = false;
			if (pawnX == 1) {
				leftEmpty = true;
			} else {
				if (posX.contains(pawnX - 1) == false) {
					leftEmpty = true;
				}
			}
			boolean right = false;
			if (pawnX == 8) {
				right = true;
			} else {
				if (posX.contains(pawnX + 1) == false) {
					right = true;
				}
			}
			if (right == true && leftEmpty == true) {
				score += isolated_pawn_score;
			}
		}
		
		// check to see if there are any isolated pawns for the enemy
		for (int i = 0; i < oponentX.size(); i++) {
			int pawnX = oponentX.get(i);
			boolean left = false;
			if (pawnX == 1) {
				left = true;
			} else {
				if (oponentX.contains(pawnX - 1) == false) {
					left = true;
				}
			}
			boolean right = false;
			if (pawnX == 8) {
				right = true;
			} else {
				if (oponentX.contains(pawnX + 1) == false) {
					right = true;
				}
			}
			if (right == true && left == true) {
				score -= isolated_pawn_score;
			}
		}
		
		// chekck to see for double isolated pawns
		int double_pawn_isolated = -1;
		for (int i = 0; i < posX.size(); i++) {
			int pawnX = posX.get(i);
			posX.remove((Integer) pawnX); // take current pawnX out so it
												// doesn't check itself
			if (posX.contains(pawnX)) {
				score += double_pawn_isolated;
			}
			posX.add(pawnX); // add it back in
		}
		
		// check to see for double enemy isolated pawns
		for (int i = 0; i < oponentX.size(); i++) {
			int pawnX = oponentX.get(i);
			oponentX.remove((Integer) pawnX);
			if (oponentX.contains(pawnX)) {
				score -= double_pawn_isolated; 
			}
			oponentX.add(pawnX); 
		}
		
		// look for friendly passed pawns
		int passed_pawns = 2;
		for (int i = 0; i < pos_pawns.size(); i++) {
			BoardPiece pos_pawn = pos_pawns.get(i);
			boolean passed = true;
			for (int k = 0; k < oponents.size(); k++) {
				BoardPiece enemy_pawn = oponents.get(k);
				boolean under = false;
				if (side == 1) {
					if (enemy_pawn.yCoord > pos_pawn.yCoord) {
						under = true;
					}
				} else {
					if (enemy_pawn.yCoord < pos_pawn.yCoord) {
						under = true;
					}
				}
				if (under) {
					if (enemy_pawn.xCoord == pos_pawn.xCoord
							|| enemy_pawn.xCoord == pos_pawn.xCoord + 1
							|| enemy_pawn.xCoord == pos_pawn.xCoord - 1) {
						passed = false;
					}
				}
			}
			if (passed) {
				score += passed_pawns;
			}
		}
		
		// Check to see if there's passed pawns on enemy
		for (int i = 0; i < oponent_pawns.size(); i++) {
			BoardPiece pos_pawn = oponent_pawns.get(i);
			boolean passed_pawn = true;
			for (int k = 0; k < pos_pawns.size(); k++) {
				BoardPiece enemy_pawn = pos_pawns.get(k);
				boolean under = false;
				if (side == 1) {
					if (enemy_pawn.yCoord < pos_pawn.yCoord) {
						under = true;
					}
				} else {
					if (enemy_pawn.yCoord > pos_pawn.yCoord) {
						under = true;
					}
				}
				if (under) {
					if (enemy_pawn.xCoord == pos_pawn.xCoord
							|| enemy_pawn.xCoord == pos_pawn.xCoord + 1
							|| enemy_pawn.xCoord == pos_pawn.xCoord - 1) {
						passed_pawn = false;
					}
				}
			}
			if (passed_pawn) {
				score -= passed_pawns; // subtract scores again
			}
		}
		return score;
	}

	// gets arraylist of all positive moves
	public ArrayList<PieceMover> generate_positive_moves() {
		ArrayList<PieceMover> positive_moves = new ArrayList<PieceMover>();
		for (int i = 0; i < all_pieces.size(); i++) {
			BoardPiece piece = all_pieces.get(i);
			if (piece.piece_type == "pawn") {
				controller.selected_piece = piece; 
				if (controller.check_valid_piece(piece.xCoord, piece.yCoord + pawn_direction)) {
					positive_moves.add(new PieceMover(piece, null, piece.xCoord, (piece.yCoord + pawn_direction)));
				}
				if (controller.check_valid_piece(piece.xCoord + 1, piece.yCoord + pawn_direction)) {
					positive_moves.add(new PieceMover(piece, controller.check_oponent_overlap(piece.xCoord + 1, piece.yCoord 
							+ pawn_direction), piece.xCoord + 1, (piece.yCoord + pawn_direction)));
				}
				if (controller.check_valid_piece(piece.xCoord - 1, piece.yCoord + pawn_direction)) {
					positive_moves.add(new PieceMover(piece, controller.check_oponent_overlap(piece.xCoord - 1, piece.yCoord + pawn_direction), 
							piece.xCoord - 1, (piece.yCoord + pawn_direction)));
				}
				if (piece.yCoord == side + pawn_direction) {
					if (controller.check_valid_piece(piece.xCoord, piece.yCoord + (pawn_direction * 2))) {
						if (find_oponent(piece.xCoord, piece.yCoord + (pawn_direction * 2)) == null
								&& find_positive(piece.xCoord, piece.yCoord + (pawn_direction * 2)) == null)
							positive_moves.add(new PieceMover(piece, null, piece.xCoord, (piece.yCoord + (pawn_direction * 2))));
					}
				}
				controller.selected_piece = null;
			} else {
				ArrayList<SquarePosition> attack = controller.get_attacking_pos(piece);
				int sx = piece.xCoord;
				int sy = piece.yCoord;
				for (int k = 0; k < attack.size(); k++) {
					SquarePosition sq_position = attack.get(k);
					BoardPiece taken = find_oponent(sq_position.xCoord, sq_position.yCoord);
					piece.xCoord = sq_position.xCoord;
					piece.yCoord = sq_position.yCoord;
					piece.refresh();
					if (taken != null) {
						oponents.remove(taken);
					}
					boolean check = controller.checkAll(); 
					piece.xCoord = sx;
					piece.yCoord = sy;
					piece.refresh();
					if (taken != null) {
						oponents.add(taken);
					}
					if (check == false) {
						positive_moves.add(new PieceMover(piece, taken, sq_position.xCoord,
								sq_position.yCoord));
					}
				}
			}
		}
		return positive_moves;
	}

	// gets arraylist of all es
	public ArrayList<PieceMover> generate_oponent_moves() {
		ArrayList<PieceMover> moves = new ArrayList<PieceMover>();
		for (int i = 0; i < oponents.size(); i++) {
			BoardPiece enemy = oponents.get(i);
			if (enemy.piece_type == "pawn") {
				int e_dir = pawn_direction * -1;
				int pawnY = side + (6 * pawn_direction);
				controller.oponent_controller.selected_piece = enemy; 
				if (controller.oponent_controller.check_valid_piece(enemy.xCoord,enemy.yCoord + e_dir)) {
					moves.add(new PieceMover(enemy, null, enemy.xCoord,(enemy.yCoord + e_dir)));
				}
				if (controller.oponent_controller.check_valid_piece(enemy.xCoord + 1, enemy.yCoord + e_dir)) {
							moves.add(new PieceMover(enemy, find_positive(enemy.xCoord + 1, (enemy.yCoord + e_dir)),
							enemy.xCoord + 1, (enemy.yCoord + e_dir)));
				}
				if (controller.oponent_controller.check_valid_piece(enemy.xCoord - 1, enemy.yCoord + e_dir)) {
					moves.add(new PieceMover(enemy, find_positive(enemy.xCoord - 1,(enemy.yCoord + e_dir)),
							enemy.xCoord - 1,(enemy.yCoord + e_dir)));
				}
				if (enemy.yCoord == pawnY) {
					if (controller.oponent_controller.check_valid_piece(enemy.xCoord, enemy.yCoord + (2 * e_dir))) {
						if (find_oponent(enemy.xCoord, enemy.yCoord + (e_dir * 2)) == null && find_positive(enemy.xCoord,
										enemy.yCoord + (e_dir * 2)) == null)
							moves.add(new PieceMover(enemy, null, enemy.xCoord, (enemy.yCoord + (e_dir * 2))));
					}
				}
				controller.selected_piece = null;
			} else {
				ArrayList<SquarePosition> attacking = controller.oponent_controller.get_attacking_pos(enemy);
				int sx = enemy.xCoord;
				int sy = enemy.yCoord;
				for (int k = 0; k < attacking.size(); k++) {
					SquarePosition square_pos = attacking.get(k);
					BoardPiece taken = find_positive(square_pos.xCoord, square_pos.yCoord);
					enemy.xCoord = square_pos.xCoord;
					enemy.yCoord = square_pos.yCoord;
					enemy.refresh();
					if (taken != null) {
						all_pieces.remove(taken);
					}
					boolean check = controller.oponent_controller.checkAll();
					enemy.xCoord = sx;
					enemy.yCoord = sy;
					enemy.refresh();
					if (taken != null) {
						all_pieces.add(taken);
					}
					if (check == false) {
						moves.add(new PieceMover(enemy, taken, square_pos.xCoord, square_pos.yCoord));
					}
				}
			}
		}

		return moves;
	}

	// Check to see if an oponent is located in specific x and y 
	// coordinates. Returns true if found
	public BoardPiece find_oponent(int x, int y) {
		int temp_key_value = (x * 10) + y;
		for (int i = 0; i < oponents.size(); i++) {
			if (oponents.get(i).piece_key == temp_key_value) {
				return oponents.get(i);
			}
		}
		return null;
	}

	// Check to see if own pieces exist in specific x and y coordinates.
	// Returns true if found.
	public BoardPiece find_positive(int x, int y) {
		int temp_key_value = (x * 10) + y;
		for (int i = 0; i < all_pieces.size(); i++) {
			if (all_pieces.get(i).piece_key == temp_key_value) {
				return all_pieces.get(i);
			}
		}
		return null;
	}

	//choose the best move under certain conditions
	public void choose_best_move() {
		boardGameTree.tree_root = new TreeNode(null, null, true);
		System.out.println("Computer is thinking...");
		//use iterative deepening to check for the best tree node
		TreeNode best_tree_node = boardGameTree.generate_iterative_deepening();
		System.out.println(best_tree_node.move.xinit + ", " + best_tree_node.move.yinit
				+ "... " + best_tree_node.move.finxcor + ", " + best_tree_node.move.finycor);
		controller.selected_piece = best_tree_node.move.p_mover;
		controller.moveSelected(best_tree_node.move.finxcor, best_tree_node.move.finycor);
		boardGameTree.tree_root = null;
	}

	public class BoardTree {
		int tree_iterations = 0;
		TreeNode tree_root;

		// returns node with best move, combination of iterative deepening
		// depth-first search and minimax 
		public TreeNode generate_iterative_deepening() {
			TreeNode best_choice = null;
			for (int k = 1; k <= 3; k++) {
				int tree_depth = 0;
				int max_tree_depth = k;
				TreeNode current_node = tree_root;
				current_node.node_alpha = -999999999; // reset alpha and beta values
				current_node.node_beta = 999999999; // reset alpha and beta values
				current_node.node_index = 0;
				boolean inner_tree_loop = true;
				while (inner_tree_loop) {
					// if results that checked at the bottom of the search
					if (tree_depth == max_tree_depth) {
						current_node.move.do_move();
						int score = evaluateGameBoard(); // get board evaluation
						current_node.node_score = score;
						// System.out.println(cScore);
						if (current_node.parent_node.max) {
							//set new alpha for maximum parent if the evaluation results
							//to be bigger
							if (current_node.parent_node.node_alpha < score) { 
								current_node.parent_node.node_alpha = score;
							}
						} else {
							//set new alpha for min parent if the evaluation results
							//to be smaller
							if (current_node.parent_node.node_beta > score) {
								current_node.parent_node.node_beta = score;
							}
						}
						
						// add to sorted list based on scores (makes alpha beta
						// search more efficient) - using insertionSort algorithm
						insertion_sort(current_node.parent_node.sorted_children_nodes, current_node, current_node.parent_node.max);
						current_node.move.undo_move();
						//decrement the depth of the tree
						tree_depth--;
						//set the current node to the parent
						current_node = current_node.parent_node;
					} else {
						if (current_node.node_index == 0 && current_node != tree_root) {
							current_node.move.do_move();
						}
						if (current_node.children_nodes == null) {
							current_node.generate_children();
						}
						
						//if done search the children nodes
						if (current_node.node_alpha > current_node.node_beta || current_node.node_index == current_node.children_nodes.size()) { 
							current_node.node_index = current_node.children_nodes.size();
							// if at a parent of a leaf node, let children be
							// the list of sorted children
							
							if (tree_depth == max_tree_depth - 1) {
								current_node.children_nodes = current_node.sorted_children_nodes;
							}
							if (current_node == tree_root) {
								// beta value
								int top_beta_value = -99999999;
								for (int i = 0; i < tree_root.children_nodes.size(); i++) {
									if (tree_root.children_nodes.get(i).node_beta > top_beta_value) {
										best_choice = tree_root.children_nodes.get(i);
										top_beta_value = tree_root.children_nodes.get(i).node_beta;
									}
								}
								inner_tree_loop = false;
							} else {
								TreeNode next_tree_node = current_node.parent_node;
								if (next_tree_node.max) {
									if (next_tree_node.node_alpha < current_node.node_beta) {
										next_tree_node.node_alpha = current_node.node_beta;
									}
								} else {
									if (next_tree_node.node_beta > current_node.node_alpha) {
										next_tree_node.node_beta = current_node.node_alpha;
									}
								}
								tree_depth--;
								current_node.move.undo_move();
								current_node = next_tree_node;
							}
							//check remaining nodes
						} else if (current_node.node_index < current_node.children_nodes.size()) { 
							TreeNode next_tree_node = current_node.children_nodes.get(current_node.node_index);
							current_node.node_index++;
							next_tree_node.node_alpha = -999999999;
							next_tree_node.node_beta = 999999999;
							next_tree_node.node_index = 0;
							tree_depth++;
							current_node = next_tree_node;
						}

					}
				}
			}
			return best_choice;
		}

		//generate a tree
		public TreeNode generate_tree(int max_tree_depth) {
			TreeNode current = tree_root;
			int depth = 0;
			while (true) {
				if (depth == max_tree_depth) {
					current.move.do_move();
					int evaluated_score = evaluateGameBoard();
					if (current.parent_node.max) {
						if (current.parent_node.node_alpha < evaluated_score) {
							current.parent_node.node_alpha = evaluated_score;
						}
					} else {
						if (current.parent_node.node_beta > evaluated_score) {
							current.parent_node.node_beta = evaluated_score;
						}
					}
					current.move.undo_move();
					depth--;
					current = current.parent_node;
				} else {
					if (current.node_index == 0) {
						current.generate_children();
						if (current != tree_root) {
							current.move.do_move();
						}
					}
					if (current.node_index == current.children_nodes.size() || current.node_beta < current.node_alpha) {
						if (current == tree_root) {
							System.out.println("root beta: " + tree_root.node_beta);
							// The best move will be the roots with the children that 
							// have the highest value
							int top_beta_value = -99999999;
							TreeNode best_tree_node = null;
							for (int i = 0; i < tree_root.children_nodes.size(); i++) {
								System.out.println(tree_root.children_nodes.get(i).node_beta);
								if (tree_root.children_nodes.get(i).node_beta > top_beta_value) {
									best_tree_node = tree_root.children_nodes.get(i);
									top_beta_value = tree_root.children_nodes.get(i).node_beta;
								}

							}
							return best_tree_node;
						} else {
							TreeNode next_tree_node = current.parent_node;
							if (next_tree_node.max) {
								if (next_tree_node.node_alpha < current.node_beta) {
									next_tree_node.node_alpha = current.node_beta;
								}
							} else {
								if (next_tree_node.node_beta > current.node_alpha) {
									next_tree_node.node_beta = current.node_alpha;
								}
							}
							depth--;
							current.move.undo_move();
							current.children_nodes.clear();
							current.children_nodes = null;
							current = next_tree_node;
						}
					} else {
						TreeNode next_tree_node = current.children_nodes.get(current.node_index);
						current.node_index++;
						depth++;
						current = next_tree_node;
					}
				}
			}
		}

		//insertion sort algorithm
		public void insertion_sort(ArrayList<TreeNode> node_list, TreeNode in_nodes, boolean high_nodes_first) {
			for (int i = 0; i < node_list.size(); i++) {
				if (high_nodes_first) {
					if (in_nodes.node_score > node_list.get(i).node_score) {
						node_list.add(i, in_nodes);
						return;
					}
				} else {
					if (in_nodes.node_score < node_list.get(i).node_score) {
						node_list.add(i, in_nodes);
						return;
					}
				}
			}
			node_list.add(in_nodes);
		}
	}

	//This class represents a node of the tree
	public class TreeNode {
		//move that resulted at this node
		PieceMover move; 
		//possible moves that can be made from that node
		ArrayList<PieceMover> piece_moves; 
		ArrayList<TreeNode> children_nodes;
		ArrayList<TreeNode> sorted_children_nodes;
		TreeNode parent_node;
		int node_score;
		int node_index;
		int node_alpha;
		int node_beta;
		//is this node a max or a min node (minimax - theorem)
		boolean max;

		// TreeNode constructor
		public TreeNode(PieceMover pm, TreeNode tn, boolean nm) {
			move = pm;
			max = nm;
			parent_node = tn;
			node_index = 0;
			node_alpha = -9999999;
			node_beta = 9999999;
			sorted_children_nodes = new ArrayList<TreeNode>();
		}

		public void generate_moves() {
			if (max) {
				piece_moves = generate_positive_moves();
			} else {
				piece_moves = generate_oponent_moves();
			}
		}

		public void generate_children() {
			generate_moves();
			if (piece_moves != null) {
				children_nodes = new ArrayList<TreeNode>();
				boolean new_max__node;
				if (max) {
					new_max__node = false;
				} else {
					new_max__node = true;
				}
				for (int i = 0; i < piece_moves.size(); i++) {
					children_nodes.add(new TreeNode(piece_moves.get(i), this, new_max__node));
				}
				piece_moves.clear();
				piece_moves = null;
			}
		}
	}

	// stores a movement on the board
	public class PieceMover {
		BoardPiece p_mover;
		BoardPiece p_taken;
		boolean castle = false;
		int finxcor, finycor;
		int xinit, yinit;

		public PieceMover(BoardPiece bp, BoardPiece bpt, int fx, int fy) {
			p_mover = bp;
			p_taken = bpt;
			finxcor = fx;
			finycor = fy;
			xinit = p_mover.xCoord;
			yinit = p_mover.yCoord;
		}

		public void do_move() {
			p_mover.xCoord = finxcor;
			p_mover.yCoord = finycor;
			p_mover.refresh();
			if (p_taken != null) {
				if (p_taken.piece_color == color) {
					all_pieces.remove(p_taken);
				} else {
					oponents.remove(p_taken);
				}
			}
		}

		public void undo_move() {
			p_mover.xCoord = xinit;
			p_mover.yCoord = yinit;
			p_mover.refresh();
			if (p_taken != null) {
				if (p_taken.piece_color == color) {
					all_pieces.add(p_taken);
				} else {
					oponents.add(p_taken);
				}
			}
		}
	}
}
