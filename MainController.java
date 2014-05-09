
/**
 * @author adrazhi
 * COS 470 | Artificial Intelligence
 * Final Project - Chess Program using Artificial Intelligence
 * University of Maine | 05/09/2014
 *
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Piece representation: 
 * 	- Each piece is a BoardPiece object, which holds the type of piace and the square that is it located
 * 	- Each color's pieces are held in an array list, such as white_pieces, and black_piaces
 * 	- Each color has a PieceModel object, that manipulates that array_list and the game_piace objects. 
 *  - Click on the piece to select it, once selected, click on the square to move it 
 * 
 * AI: 
 *  - Separate object which controlls the PieceModel
 *   	- PieceModels - manipulated by mouse listeners (Swing)
 *   
 *   
 * @author adrazhi
 * 
 */
public class MainController extends JPanel {
	private static final long serialVersionUID = 1L;
	public static ArrayList<BoardPiece> white_pieces;
	public static ArrayList<BoardPiece> black_pieces;
	public static boolean move_whites;
	public static boolean refresh;
	public static PieceModel white_pieces_controller;
	public static PieceModel black_pieces_controller;
	public static AiController ai_controller;
	public static boolean highlighted_piece;
	public static int highlighted_x_square;
	public static int highlighted_y_square;
	public static char player_color;
	public static double move_time;
	public MainController() {
		/*
		 * Set up the white pieces
		 */
		white_pieces = new ArrayList<BoardPiece>();
		for(int i = 1; i < 9; i++) {
			white_pieces.add(new BoardPiece("pawn", "white", i, 2));
		}
		white_pieces.add(new BoardPiece("rook", "white", 1, 1));
		white_pieces.add(new BoardPiece("knight", "white", 2, 1));
		white_pieces.add(new BoardPiece("bishop", "white", 3, 1));
		white_pieces.add(new BoardPiece("queen", "white", 4, 1));
		white_pieces.add(new BoardPiece("king", "white", 5, 1));
		white_pieces.add(new BoardPiece("bishop", "white", 6, 1));
		white_pieces.add(new BoardPiece("knight", "white", 7, 1));
		white_pieces.add(new BoardPiece("rook", "white", 8, 1));
		
		/*
		 * Set up the black pieces
		 */
		black_pieces = new ArrayList<BoardPiece>();
		for(int i = 1; i < 9; i++) {
			black_pieces.add(new BoardPiece("pawn", "black", i, 7));
		}
		black_pieces.add(new BoardPiece("rook", "black", 1, 8));
		black_pieces.add(new BoardPiece("knight", "black", 2, 8));
		black_pieces.add(new BoardPiece("bishop", "black", 3, 8));
		black_pieces.add(new BoardPiece("queen", "black", 4, 8));
		black_pieces.add(new BoardPiece("king", "black", 5, 8));
		black_pieces.add(new BoardPiece("bishop", "black", 6, 8));
		black_pieces.add(new BoardPiece("knight", "black", 7, 8));
		black_pieces.add(new BoardPiece("rook", "black", 8, 8));

		//send arrayLists to the controllers (black and white)
		white_pieces_controller = new PieceModel(white_pieces, black_pieces);
		black_pieces_controller = new PieceModel(black_pieces, white_pieces);
		white_pieces_controller.oponent_controller = black_pieces_controller;
		black_pieces_controller.oponent_controller = white_pieces_controller;
		Scanner in = new Scanner(System.in);
		System.out.println("Which color do you want to play as? (type b for black or w for whites)");
		player_color = in.nextLine().charAt(0);
		//set up the player with white or black pieces depending on the choice
		if(player_color == 'b') {
			ai_controller = new AiController(white_pieces_controller, this);
		}
		if(player_color == 'w') {
			ai_controller = new AiController(black_pieces_controller, this);
		}
		in.close();
		System.out.println("after you make a move, click the screen again to tell the AI to make a move");
		
		//add mouse listener functionality
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent m) {
				//convert mouseCoordinates to square
				int x_square;
				int y_square;
				double mouse_x_value = (double) m.getX();
				double mouse_y_value = (double) m.getY();
				x_square = (int) Math.ceil(mouse_x_value/75);
				y_square = (int) Math.ceil(mouse_y_value/75)*-1;
				y_square += 9;
				highlighted_x_square = x_square;
				highlighted_y_square = y_square;
				if(move_whites) {
					if(player_color == 'w') {
						if(white_pieces_controller.selected_value == false) {
							white_pieces_controller.setSelected(x_square, y_square);
							if(white_pieces_controller.selected_value) {
								highlighted_piece = true;
							}
						}
						else {
							BoardPiece mouse_down = white_pieces_controller.check_piece_overlap(x_square, y_square);
							if(mouse_down != null) {
								white_pieces_controller.setSelected(x_square, y_square);
							}
							else if(white_pieces_controller.check_valid_piece(x_square, y_square)) {
								white_pieces_controller.moveSelected(x_square, y_square);
								white_pieces_controller.count_turn++;
								move_whites = false;
								highlighted_piece = false;
							}
						}
					}
					else {
						ai_controller.choose_best_move();
						white_pieces_controller.count_turn++;
						move_whites = false;
					}
				}

				else if(move_whites == false) {
					if(player_color == 'b') {
						if(black_pieces_controller.selected_value == false) {
							black_pieces_controller.setSelected(x_square, y_square);
							if(black_pieces_controller.selected_value) {
								highlighted_piece = true;
							}
						}
						else {
							BoardPiece mouse_down = black_pieces_controller.check_piece_overlap(x_square, y_square);
							if(mouse_down != null) {
								black_pieces_controller.setSelected(x_square, y_square);
							}
							else if(black_pieces_controller.check_valid_piece(x_square, y_square)) {
								black_pieces_controller.moveSelected(x_square, y_square);
								black_pieces_controller.count_turn++;
								move_whites = true;
								highlighted_piece = false;
							}


						}
					}
					else {
						ai_controller.choose_best_move();
						white_pieces_controller.count_turn++;
						move_whites = true;
					}
				}
				repaint();
			}
		});
	}

	public static void main(String[] args) {
		move_whites = true;
		refresh = false;
		highlighted_piece = false;
		highlighted_x_square = 0;
		highlighted_y_square = 0;

		//set up the board JFrame
		JFrame game_frame = new JFrame("Welcome to the Chess Program!");
		game_frame.add(new MainController());
		game_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game_frame.setSize(600, 650);
		game_frame.setLocationRelativeTo(null);
		game_frame.setVisible(true);
	}

	/*
	 * paint and redraw panel
	 */
	public void paint(Graphics g) {
		super.paintComponent(g);
		Graphics2D graphics_2d = (Graphics2D) g;
		//draw the chess board
		graphics_2d.setColor(new Color(0,0,0));
		graphics_2d.drawRect(0,0,600,600);
		graphics_2d.setColor(new Color(128,121,124));

		for(int i = 0; i < 500; i += 150) {
			for(int k = 75; k <= 525; k += 150) {
				graphics_2d.fillRect(k, i, 75, 75);
			}
		}
		for(int i = 75; i <= 525; i += 150) {
			for(int k = 0; k < 500; k += 150) {
				graphics_2d.fillRect(k, i, 75, 75);
			}
		}
		if(highlighted_piece) {
			graphics_2d.setColor(new Color(255,0,0));
			int rectX = (highlighted_x_square-1)*75;
			int rectY = (8-highlighted_y_square)*75;
			graphics_2d.fillRect(rectX, rectY, 75, 75);
		}
		
		//draw white pieces
		for(int i = 0; i < white_pieces.size(); i++) {
			BoardPiece board_piece = white_pieces.get(i);
			//set file name string
			StringBuffer name_buffer = new StringBuffer(board_piece.piece_type);
			name_buffer.append("_white.png");
			String name = name_buffer.toString();
			Image piece_image = new ImageIcon(this.getClass().getResource(name)).getImage();
			//set coordinates for the piece on the screen
			int xCord = (board_piece.xCoord-1)*75+18;
			int yCord = (8-board_piece.yCoord)*75+18;
			graphics_2d.drawImage(piece_image, xCord, yCord, null);
		}
		
		//draw black pieces
		for(int i = 0; i < black_pieces.size(); i++) {
			BoardPiece board_piece = black_pieces.get(i);
			//set file name string
			StringBuffer name_buffer = new StringBuffer(board_piece.piece_type);
			name_buffer.append("_black.png");
			String name = name_buffer.toString();
			Image image = new ImageIcon(this.getClass().getResource(name)).getImage();
			//set coordinates for the piece on the screen
			int xCord = (board_piece.xCoord-1)*75+18;
			int yCord = (8-board_piece.yCoord)*75+18;
			graphics_2d.drawImage(image, xCord, yCord, null);
		}
		graphics_2d.setColor(new Color(0,0,0));
		if(move_whites) {
			graphics_2d.drawString("White's move.", 20, 620);
		}
		else {
			graphics_2d.drawString("Black's move.", 20, 620);
		}
	}

}
