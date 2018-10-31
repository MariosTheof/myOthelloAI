import java.util.*;

public class Board{

  public static enum Piece{
      BLACK, // ίσως τα αλλάξω σε '1' ή '-1' λόγω ui
      WHITE,
      EMPTY;
  }

  private Piece[][] board;

  private Piece currentPlayerPiece;

  //////////////////////////////////////////////////////////////////////////
  static class Point {
    public final int row;
    public final int col;

    public Point(int row, int col) {
      this.row = row;
      this.col = col;
    }
  }

  /*Όλες οι κατευθύνσεις αντιπροσωπευμένες από έναν πίνακα*/
  private static final Point[] possibleDirections = new Point[]{
    new Point(1, 0),
    new Point(1, 1),
    new Point(0, 1),
    new Point(-1, 1),
    new Point(-1, 0),
    new Point(-1, -1),
    new Point(0, -1),
    new Point(1, -1),
  };

  interface CellHandler {
    boolean handleCell(int row, int col, Piece piece);
  }

  /*iterateCells() accepts some direction and navigates through it
  till it hits an empty cell or border */
  void iterateCells(Point start, Point step, CellHandler handler) {

    for (int row = start.row + step.row, col = start.col + step.col;
         isValidPosition(row,col);
         row += step.row, col += step.col) {

           Piece piece = board[row][col]; // θέλει get ;?
           // empty cell
           if (piece == Piece.EMPTY)
              break;
              // handler can stop iteration
           if (!handler.handleCell(row, col, piece))
              break;
    }
  }

  static class CheckCellHandler implements CellHandler {
    private final Piece otherPiece;
    private boolean hasOtherPieces = false;
    private boolean endsWithMine = false;

    public CheckCellHandler(Piece otherPiece) {
        this.otherPiece = otherPiece;
    }

    @Override
    public boolean handleCell(int row, int column, Piece piece) {
        if (piece == otherPiece) {
            hasOtherPieces = true;
            return true;
        } else {
            endsWithMine = true;
            return false;
        }
    }

    public boolean isGoodMove() {
        return hasOtherPieces && endsWithMine;
    }

    }

  class FlipCellHandler implements CellHandler {
    private final Piece myPiece;
    private final Piece otherPiece;
    private final List<Point> currentFlipList = new ArrayList<Point>();

    public FlipCellHandler(Piece myPiece, Piece otherPiece) {
        this.myPiece = myPiece;
        this.otherPiece = otherPiece;
    }

    @Override
    public boolean handleCell(int row, int column, Piece piece) {
        if (piece == myPiece) {
            // flip all cells
            for (Point p : currentFlipList) {
                board[p.row][p.col] = myPiece;
            }
            return false;
        } else {
            currentFlipList.add(new Point(row, column));
            return true;
        }
    }
  }

  private boolean isValidPosition(int x, int y){
    if( x < 0 || x >= 8 || y < 0 || y >=  8 ){
      return false;
    }
    return true;
  }

  /*implement CheckCellHandler and iterate
  through all possible directions to see if we can flip in that direction*/
  private boolean checkLegalPlay(int row, int col) {
    Piece otherPiece = ( board[row][col] == Piece.BLACK ) ? Piece.WHITE : Piece.BLACK;
    Point start = new Point(row, col);
    for (Point step : possibleDirections) {
        // handler is stateful so create new for each direction
        CheckCellHandler checkCellHandler = new CheckCellHandler(otherPiece);
        iterateCells(start, step, checkCellHandler);
        if (checkCellHandler.isGoodMove())
            return true;
    }
    return false;
  }



  /////////////////////////////////////////////////////////////////////////

  /*Αρχικοποίηση του πινακα*/
  public Board(){
    this.board = new Piece[8][8];
    this.currentPlayerPiece = Piece.BLACK;
    // Αρχικές θέσεις

    this.board[3][3] = Piece.WHITE;
    this.board[3][4] = Piece.BLACK;
    this.board[4][3] = Piece.BLACK;
    this.board[4][4] = Piece.WHITE;
  }

  public boolean placePiece(int i, int j) {
    if (this.checkLegalPlay(i,j)) {
      this.board[i][j] = this.currentPlayerPiece;
      //updateBoard(i, j); // χρειάζονται τα updates ;;;;
      changeTurn();
      return true;
    } else {
      System.out.println("Illegal piece placement \n");
      return false;
    }
  }

  public String getCurrentPlayerPiece(){
    if (currentPlayerPiece == Piece.BLACK){
      return "Black";
    }else {
      return "White";
    }
  }

  private void changeTurn() {
    if (currentPlayerPiece == Piece.BLACK){
      currentPlayerPiece = Piece.WHITE;
    } else {
      currentPlayerPiece = Piece.BLACK;
    }
  }

//  public void winner

  /*Εκτυπώνει τον πίνακα στο cmd*/
  public void printBoard(){
    System.out.println("***********************************\n");
    System.out.println("|   || 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | \n");
    for (int row = 0; row <= 7; row++){
      System.out.print("| " + (row + 1) + " |");
      for (int col = 0; col <= 7; col++){
        if (board[row][col] == Piece.BLACK){
           System.out.print("| " + "B" + " "); // ίσως θα βάλω \u25E6 και έτσι
        } else if(board[row][col] == Piece.WHITE){
          System.out.print("| " + "W" + " ");
        } else {
          System.out.print("| " + "-" + " ");
        }
      }
      System.out.print("| \n");
    }
  }/* /printBoard() */



}
