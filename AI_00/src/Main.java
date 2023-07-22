import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Scanner;

public class Main implements ActionListener {

    // column and row length
    int rows = 6, columns = 7;

    JFrame f = new JFrame("Game Board");
    JButton reset = new JButton();
    JButton label = new JButton();
    JButton[][] bt = new JButton[rows][columns];
    int x=10, y=10;
	
	//turns
    boolean PLAYER =false , AI =true;
    boolean turn = AI;

    // if a cell in board is empty, it's 0
    int empty = 0;
    // if a cell in board contains player piece, it's -1, else it's 1.
    int playerPiece = -1, AIPiece = 1;

    // number of pieces in a row that makes a player win
    int fourPiece = 4;

    // name of player
    String playerName;

    // gameOver
    boolean gameOver = false;

    int[][] board = new int[rows][columns];

    static Scanner scan = new Scanner(System.in);

    // Game
    public static void main(String[] args) {
        Main object = new Main();
        object.Board(object.columns,object.rows,"Mahdi");


        int[][] board = new int[object.rows][object.columns];
        object.printBoard(board);
        object.gameOver = false;
        int col;
        while (object.gameOver == false) {
            if (object.turn == object.PLAYER) {
                System.out.println("Your turn (O)");
                System.out.println("Enter a not filled column number (0 to 6) or 9=reset: ");
                col = scan.nextInt();
                if (object.isEmptyColumn(board, col)) {
                    int row = object.rowToDrop(board, col);
                    object.dropPiece(board, row, col, object.playerPiece);
                    object.printBoard(board);

                    if (object.isFourInARow(board, object.playerPiece)) {
                        System.out.println("!!!You Won!!!");
                        object.gameOver = true;
                        break;
                    }
                    object.turn = object.AI;
                }
            }
            // Edited
            if (object.turn == object.AI) {
                System.out.println("AI Turn (X)");
                col = object.negamax(board, 5, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1)[0];
//				col = minmax(board, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, true)[0];
                if (object.isEmptyColumn(board, col)) {
                    int row = object.rowToDrop(board, col);
                    object.dropPiece(board, row, col, object.AIPiece);
                    object.printBoard(board);
                    if (object.isFourInARow(board, object.AIPiece)) {
                        System.out.println("XXX AI WON XXX");
                        object.gameOver = true;
                        break;
                    }
                    object.turn = object.PLAYER;
                }
            }
        }
    }
    
    int[][] create_board() {
        return board;
    }

    void printBoard(int[][] board) {
        System.out.println("| 0 | 1 | 2 | 3 | 4 | 5 | 6 |");
        System.out.println();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(board[i][j]==playerPiece)
                    System.out.print("| O ");
                else if(board[i][j]==AIPiece)
                    System.out.print("| X ");
                else
                    System.out.print("|   ");
            }
            System.out.print("|");
            System.out.println();
        }
    }

    // if first row of a column is empty, it's an Empty column
    boolean isEmptyColumn(int[][] board, int col) {
        boolean ret = false;
        if(col<columns && col>=0) {
            ret = (board[0][col]==0);
        } else {
            System.out.println("Number is out of bounds");
            ret = false;
        }
        return ret;
    }

    // we go down to a the last empty row
    int rowToDrop(int[][] board, int col) {
        for (int i = rows - 1; i >= 0; i--) {
            if (board[i][col] == 0) {
                return i;
            }
        }
        return 0;
    }

    // dropping piece
    void dropPiece(int[][] board, int row, int col, int piece) {
        board[row][col] = piece;
    }

    // checks if there are 4 in a row
    boolean isFourInARow(int[][] board, int piece) {
        // Horizental
        for (int c = 0; c < columns - 3; c++) {
            for (int r = 0; r < rows; r++) {
                if (board[r][c] == piece && board[r][c + 1] == piece && board[r][c + 2] == piece
                        && board[r][c + 3] == piece)
                    return true;
            }
        }

        // Vertical
        for (int c = 0; c < columns; c++) {
            for (int r = 0; r < rows - 3; r++) {
                if (board[r][c] == piece && board[r + 1][c] == piece && board[r + 2][c] == piece
                        && board[r + 3][c] == piece)
                    return true;
            }
        }

        // diagonal from bottom left to top right
        for (int c = 0; c < columns - 3; c++) {
            for (int r = 0; r < rows - 3; r++) {
                if (board[r][c] == piece && board[r + 1][c + 1] == piece && board[r + 2][c + 2] == piece
                        && board[r + 3][c + 3] == piece)
                    return true;
            }
        }

        // diagonal from bottom right to top left
        for (int c = 0; c < columns - 3; c++) {
            for (int r = 3; r < rows; r++) {
                if (board[r][c] == piece && board[r - 1][c + 1] == piece && board[r - 2][c + 2] == piece
                        && board[r - 3][c + 3] == piece)
                    return true;
            }
        }
        return false;
    }

    // used in the next method to count each kind of pieces in a row of size 4
    int countContentOfFourPiece(int[] fourPiece, int piece) {
        int count = 0;
        for (int i = 0; i < fourPiece.length; i++) {
            if (fourPiece[i] == piece)
                count++;
        }
        return count;
    }

    // used in heuristic
    int usedInHeu(int[] fourPiece, int piece) {
        int score = 0;
        int opponentPiece = playerPiece;

        if (piece == playerPiece)
            opponentPiece = AIPiece;

        if (countContentOfFourPiece(fourPiece, piece) == 4)
            score += 100;
        else if (countContentOfFourPiece(fourPiece, piece) == 3 && countContentOfFourPiece(fourPiece, empty) == 1)
            score += 20;
        else if (countContentOfFourPiece(fourPiece, piece) == 2 && countContentOfFourPiece(fourPiece, empty) == 2)
            score += 5;

        if (countContentOfFourPiece(fourPiece, opponentPiece) == 4 && countContentOfFourPiece(fourPiece, empty) == 0)
            score -= 200;
        else if (countContentOfFourPiece(fourPiece, opponentPiece) == 3 && countContentOfFourPiece(fourPiece, empty) == 1)
            score -= 15;
        else if (countContentOfFourPiece(fourPiece, opponentPiece) == 2 && countContentOfFourPiece(fourPiece, empty) == 2)
            score -= 1;

        return score;
    }

    //heuristic
    int heuristic(int[][] board, int piece) {
        int score = 0;
        int[] eachRow = new int[columns];
        int[] eachColumn = new int[rows];
        int[] eachFour = new int[fourPiece];

        //Horizontal
        for (int r = 0; r < rows; r++) {
            for (int i = 0; i < columns; i++) {
                eachRow[i] = board[r][i];
            }
            for (int c = 0; c < columns - 3; c++) {
                for (int j = c; j < c + fourPiece; j++) {
                    eachFour[j-c] = eachRow[j];
                }
                score += usedInHeu(eachFour, piece);
            }
        }

        //Vertical
        for (int c = 0; c < columns; c++) {
            for (int i = 0; i < rows; i++) {
                eachColumn[i] = board[i][c];
            }
            for (int r = 0; r < rows - 3; r++) {
                for (int j = r; j < r + fourPiece; j++) {
                    eachFour[j-r] = eachColumn[j];
                }
                score += usedInHeu(eachFour, piece);
            }
        }

        //diagonal 1
        for (int r = 0; r < rows - 3; r++) {
            for (int c = 0; c < columns - 3; c++) {
                for (int i = 0; i < fourPiece; i++) {
                    eachFour[i] = board[r + i][c + i];
                }
                score += usedInHeu(eachFour, piece);
            }
        }

        //diagonal 2
        for (int r = 0; r < rows - 3; r++) {
            for (int c = 0; c < columns - 3; c++) {
                for (int i = 0; i < fourPiece; i++) {
                    eachFour[i] = board[r + 3 - i][c + i];
                }
                score += usedInHeu(eachFour, piece);
            }
        }

        return score;
    }


    // gives the valid columns for putting piece in
    ArrayList<Integer> validColumns(int[][] board) {
        ArrayList<Integer> valid_locations = new ArrayList<Integer>();
        for (int c = 0; c < columns; c++) {
            if (isEmptyColumn(board, c))
                valid_locations.add(c);
        }
        return valid_locations;
    }

    // checking if it is terminal node
    boolean isTerminalNode(int[][] board) {
        return isFourInARow(board, playerPiece) || isFourInARow(board, AIPiece)
                || validColumns(board).size() == 0;
    }

    // gives a random value in arrayList. used in next method.
    int randomOfArrayList(ArrayList<Integer> arrayList) {
        int rand =  0 +(int)(Math.random() * arrayList.size());
        return arrayList.get(rand);
    }

    // minmax algorithm with alpha beta pruning
    Integer[] minmax(int[][] board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        ArrayList<Integer> valid_locations = validColumns(board);
        boolean is_terminal = isTerminalNode(board);
        Integer[] returnArray = new Integer[2];
        int[][] childBoard = new int[rows][columns];
        if (depth == 0 || is_terminal) {
            if (is_terminal) {
                if (isFourInARow(board, AIPiece)) {
                    returnArray[0] = null;
                    returnArray[1] = 1000000000;
                    return returnArray;
                } else if (isFourInARow(board, playerPiece)) {
                    returnArray[0] = null;
                    returnArray[1] = -1000000000;
                    return returnArray;
                } else { // Game is over, no more valid moves
                    returnArray[0] = null;
                    returnArray[1] = 0;
                    return returnArray;
                }
            } else { // Depth is zero
                returnArray[0] = null;
                returnArray[1] = heuristic(board, AIPiece);
                return returnArray;
            }
        }
        if(maximizingPlayer) {
            int value = -Integer.MAX_VALUE;
            int column = randomOfArrayList(valid_locations);
            for (int col = 0; col < valid_locations.size(); col++) {
                int row = rowToDrop(board, valid_locations.get(col));
                childBoard = new int[rows][columns];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        childBoard[i][j] = board[i][j];
                    }
                }
                dropPiece(childBoard, row, col, AIPiece);
                int new_score = minmax(childBoard, depth - 1, alpha, beta, false)[1];
                if (new_score > value) {
                    value = new_score;
                    column = col;
                }
                alpha = Math.max(alpha, value);
                if (alpha >= beta)
                    break;
            }
            returnArray[0] = column;
            returnArray[1] = value;
            return returnArray;
        } else {
            int value = Integer.MAX_VALUE;
            int column = randomOfArrayList(valid_locations);
            for (int col = 0; col < valid_locations.size(); col++) {
                int row = rowToDrop(board, valid_locations.get(col));
                childBoard = new int[rows][columns];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        childBoard[i][j] = board[i][j];
                    }
                }
                dropPiece(childBoard, row, col, playerPiece);
                int new_score = minmax(childBoard, depth - 1, alpha, beta, true)[1];
                if (new_score < value) {
                    value = new_score;
                    column = col;
                }
                beta = Math.min(beta, value);
                if (alpha >= beta)
                    break;
            }
            returnArray[0] = column;
            returnArray[1] = value;
            return returnArray;
        }
    }

    //negamax with alpha beta pruning
    Integer[] negamax(int[][] board, int depth, int alpha, int beta, int color) {
        ArrayList<Integer> valid_locations = validColumns(board);
        boolean is_terminal = isTerminalNode(board);
        Integer[] returnArray = new Integer[2];
        int[][] board_copy = new int[rows][columns];
        int new_score;
        int value;
        if (depth == 0 || is_terminal) {
            returnArray[0] = null;
            returnArray[1] = color * heuristic(board, 1);
            return returnArray;
        }

        value = -Integer.MAX_VALUE;
        for (int col = 0; col < valid_locations.size(); col++) {
            int row = rowToDrop(board, valid_locations.get(col));
            board_copy = new int[rows][columns];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    board_copy[i][j] = board[i][j];
                }
            }
            dropPiece(board_copy, row, col, color);
            new_score = -negamax(board_copy, depth - 1, -beta, -alpha, -color)[1];
            if (new_score > value) {
                value = new_score;
                returnArray[0] = col;
            }
            alpha = Math.max(alpha, value);
            if (alpha >= beta)
                break;
        }
        returnArray[1] = value;
        return returnArray;
    }
    
	//sound of dropping piece.
	void playSound() {
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(this.getClass().getResource("/image/Piecedrop.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

    // image blue for Player and red for AI
    Image red = new ImageIcon(this.getClass().getResource("/image/red.jpg")).getImage();
    Image blue = new ImageIcon(this.getClass().getResource("/image/blue.jpg")).getImage();

    void draw_board(int[][] board) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (board[i][j] == playerPiece)
                    bt[i][j].setIcon(new ImageIcon(blue.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
                else if (board[i][j] == AIPiece)
                    bt[i][j].setIcon(new ImageIcon(red.getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
            }
        }
    }


    void Board(int column, int row, String player) {

        reset.setBounds(10,320,80,20);
        reset.setBackground(Color.pink);
        reset.setText("RESET");
        reset.addActionListener(this);
        f.add(reset);

        label.setBounds(275,320,85,20);
        label.setBackground(Color.white);
        f.add(label);


        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                bt[i][j] = new JButton();
                bt[i][j].setBackground(Color.white);
                bt[i][j].setBounds(x, y, 50, 50);
                //bt[i][j].addActionListener(this);
                f.add(bt[i][j]);
                //GamePanel.add(bt[i][j]);
                x +=50;
            }
            y+=50;
            x=10;
        }


        f.setSize(390, 390);//400 width and 500 height
        // f.pack();
        f.setLayout(null);//using no layout managers
        f.setVisible(true);//making the frame visible

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(dim.width/2-f.getSize().width/2, dim.height/2-f.getSize().height/2);


        int[][] board = create_board();
        printBoard(board);
        gameOver = false;

        // Edited
        while(gameOver ==false){
            if (turn == AI) {
                label.setText("AI");

                int col = negamax(board, 5, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1)[0];
//				col = minmax(board, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, true)[0];

                if (isEmptyColumn(board, col)) {
                    row = rowToDrop(board, col);
                    dropPiece(board, row, col, AIPiece);
                    playSound();
                    printBoard(board);
                    draw_board(board);
                    if (isFourInARow(board, AIPiece)) {
                        System.out.println("XXX AI WON XXX");
                        JOptionPane.showMessageDialog(null, "AI Won !");
                        //gameOver = true;
                        // break;
                    }

                    label.setText("PLAYER");

                    turn=PLAYER;
                    //System.out.println("player");
                }
            }
            else {
                for (int i = 0; i < row; i++) {
                    for (int j = 0; j < column; j++) {
                        bt[i][j].addActionListener(this);
                    }
                }
            }
        }

    }

    @Override
    public void actionPerformed (ActionEvent actionEvent){

        if(actionEvent.getSource().equals(reset)){
            gameOver =false;
            //  game_over=true;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    //bt[i][j] = new JButton();
                    // bt[i][j].setBackground(Color.red);
                    bt[i][j].setIcon(null);
                    bt[i][j].revalidate();
                    board[i][j]= empty;
                    // bt[i][j].setBounds(x, y, 50, 50);
                    //bt[i][j].addActionListener(this);
                    // f.add(bt[i][j]);
                    //GamePanel.add(bt[i][j])
                }
            }

            reset.removeActionListener(this);
            turn = AI;
            reset.addActionListener(this);
        }

        if (turn == PLAYER && gameOver == false) {
            label.setText("PLAYER");

            int /*x = 0,*/ y = 0;
            lb:
            for (int m = 0; m < rows; m++) {
                for (int n = 0; n < columns; n++) {
                    if (actionEvent.getSource().equals(bt[m][n])) {
                        //x = m;
                        y = n;
                        break lb;
                    }
                }
            }
            if (isEmptyColumn(board, y)) {
                int row = rowToDrop(board, y);
                dropPiece(board, row, y, playerPiece);
                playSound();
                printBoard(board);
                draw_board(board);
                if (isFourInARow(board, playerPiece)) {

                    JOptionPane.showMessageDialog(null, "player Won !");
                    //gameOver = true;
                }
                turn = AI;
                //System.out.println(turn);
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        bt[i][j].removeActionListener(this);
                    }
                }
            }
        }
    }
}
