package game;

import game.Constant.BoxType;
import game.Constant.Direction;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 *  Class containing the Board
 */
public class Board {
	/**
	 * @see Box
	 */
	private Box[][] gameBoard;

	private List<Integer> numBoardPieces;
	private char boardPiecesSide[];

	/**
	 * @see BoardPiece
	 */
	private BoardPiece boardPieces[];

	public Board() {
		
		numBoardPieces = new ArrayList<Integer>();
		boardPiecesSide = new char[Constant.NB_BOARD_PIECES];
		boardPieces = new BoardPiece[Constant.NB_BOARD_PIECES];
		gameBoard = new Box[Constant.NB_BOXES][Constant.NB_BOXES];
	}
	
	public void initBoard() {

		randomizeNumBoardPieces();
		randomizeBoardPiecesSide();

		initBoardPieces();
		setBoard();

	}

	/**
	 * Initializes the board pieces with their xml file, initialLocation and finalLocation
	 */
	private void initBoardPieces() {
		String mapPath;
		int initialLocation;
		int finalLocation;
		BoardPiece bp;
		
		for (int i = 0; i<Constant.NB_BOARD_PIECES; i++)
		{		
			mapPath = Constant.MAP_PATH + "boardpiece" + numBoardPieces.get(i) + boardPiecesSide[i] +".xml";
			initialLocation = numBoardPieces.get(i);
			finalLocation = i+1;
			
			bp = new BoardPiece(new File(mapPath), initialLocation , finalLocation);
			bp.initBoardPiece();
			boardPieces[i] = bp;
		}
	}

	/**
	 * Chooses a random board piece
	 */
	private void randomizeNumBoardPieces() {
		for(int i=0; i<Constant.NB_BOARD_PIECES; i++){
			numBoardPieces.add(i+1);
		}

		Collections.shuffle(numBoardPieces);
	}

	/**
	 * Chooses a random side for a board piece
	 */
	private void randomizeBoardPiecesSide() {
		for(int i=0; i<Constant.NB_BOARD_PIECES; i++)
		{
			boardPiecesSide[i] = ((int) (Math.random()*100)%Constant.NB_BOARD_PIECES_SIDE+1)==1 ? 'A' : 'B';
		}
	}

	/**
	 * Sets up the board by putting the right boxes into the matrix
	 */
	public void setBoard()
	{
		for (int i=0 ; i<Constant.NB_BOXES ; i++)
		{
			for (int j=0 ; j<Constant.NB_BOXES ; j++){
				gameBoard[i][j]=new Box();
			}
		}
		
		List<Box> boxes;
		
		for(int i=0; i<Constant.NB_BOARD_PIECES; i++){
			boxes = boardPieces[i].getBoxes();
			
			for(Box b : boxes){
				gameBoard[b.getY()][b.getX()] = b;
			}
		}

		putCentralBoxes();
		putSurroundingWalls();
		oppositeWall();
	}

	/**
	 * Put walls on the edges of the board
	 */
	private void putSurroundingWalls() {
		for (int i=0; i<Constant.NB_BOXES;i++)
		{
			gameBoard[i][0].setWall(Constant.Direction.Left);
			gameBoard[i][Constant.NB_BOXES-1].setWall(Constant.Direction.Right);
			gameBoard[0][i].setWall(Constant.Direction.Up);
			gameBoard[Constant.NB_BOXES-1][i].setWall(Constant.Direction.Down);
		}
	}

	/**
	 * Put central boxes of the board with a wall around this area
	 */
	private void putCentralBoxes() {
		gameBoard[7][7].setWall(Direction.Up);
		gameBoard[7][7].setWall(Direction.Left);

		gameBoard[7][8].setWall(Direction.Up);
		gameBoard[7][8].setWall(Direction.Right);

		gameBoard[8][7].setWall(Direction.Down);
		gameBoard[8][7].setWall(Direction.Left);

		gameBoard[8][8].setWall(Direction.Down);
		gameBoard[8][8].setWall(Direction.Right);

		gameBoard[7][7].setType(BoxType.Central, null);
		gameBoard[7][8].setType(BoxType.Central, null);
		gameBoard[8][7].setType(BoxType.Central, null);
		gameBoard[8][8].setType(BoxType.Central, null);
	}

	/**
	 * Build the opposite wall
	 * Example :If the north wall of a box exist then the south wall of the box above must be built
	 */
	public void oppositeWall(){
		for(int i=0 ; i<Constant.NB_BOXES ; i++){
			for(int j=0 ; j<Constant.NB_BOXES ; j++){
				if(gameBoard[i][j].isEast() && j != Constant.NB_BOXES-1){
					gameBoard[i][j+1].setWest(true);
				}
				if(gameBoard[i][j].isWest() && j != 0){
					gameBoard[i][j-1].setEast(true);
				}
				if(gameBoard[i][j].isNorth() && i != 0){
					gameBoard[i-1][j].setSouth(true);
				}
				if(gameBoard[i][j].isSouth() && i != Constant.NB_BOXES-1){
					gameBoard[i+1][j].setNorth(true);
				}
			}
		}
	}
	
	/**
	 * Move a robot in a direction, and return true if robot has moved, false else
	 * @param robot robot that have to move
	 * @param direction direction that the robot is following
	 * @param robots all the robots of the game
	 * @see Direction
	 * @see Robot
	 */
	public boolean getNewPosition(Robot robot, Direction direction, List<Robot> robots) {
		boolean moved = false;
		while (gameBoard[robot.y][robot.x].canContinue(direction) 
				&& !robot.robotIsHere(robots, direction)) {
			switch (direction) {
			case Right :
				robot.x++;
				break;
			case Left :
				robot.x--;
				break;
			case Down :
				robot.y++;
				break;
			case Up :
				robot.y--;
				break;
			default:
				break;
			}
			moved = true;
		}	
		return moved;
	}

	/**
	 * Getter of GameBoard
	 * @return gameBoard
	 */
	public Box[][] getGameBoard() {
		return gameBoard;
	}
	
	public Box getBox(int x, int y) {
		return gameBoard[x][y];
	}

	/**
	 * Setter of GameBoard
	 * @param gameBoard
	 */
	public void setGameBoard(Box[][] gameBoard) {
		this.gameBoard = gameBoard;
	}


	public List<Integer> getNumBoardPieces() {
		return numBoardPieces;
	}


	public void setNumBoardPieces(List<Integer> numBoardPieces) {
		this.numBoardPieces = numBoardPieces;
	}


	public char[] getBoardPiecesSide() {
		return boardPiecesSide;
	}


	public void setBoardPiecesSide(char boardPiecesSide[]) {
		this.boardPiecesSide = boardPiecesSide;
	}

	public BoardPiece getBoardPiece(int i) {
		return boardPieces[i];
	}
	
	public BoardPiece[] getBoardPiece() {
		return boardPieces;
	}

	public void setBoardPiece(BoardPiece boardPiece) {
		this.boardPieces[boardPiece.getFinalLocation()-1] = boardPiece;
	}
}
