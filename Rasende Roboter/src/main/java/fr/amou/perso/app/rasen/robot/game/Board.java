package fr.amou.perso.app.rasen.robot.game;

import java.util.ArrayList;
import java.util.List;

import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import lombok.Data;

/**
 * Classe contenant les informations du plateau de jeu.
 *
 * @author amourany
 *
 */
@Data
public class Board {

	/** Les cases du plateau. */
	private Box[][] gameBoard = new Box[Constant.NB_BOXES][Constant.NB_BOXES];

	private List<Integer> numBoardPieces = new ArrayList<>();
	private char boardPiecesSide[] = new char[Constant.NB_BOARD_PIECES];

	private BoardPiece boardPieces[] = new BoardPiece[Constant.NB_BOARD_PIECES];

	/**
	 * Getter of GameBoard
	 *
	 * @return gameBoard
	 */
	public Box[][] getGameBoard() {
		return this.gameBoard;
	}

	public Box getBox(int x, int y) {
		return this.gameBoard[x][y];
	}

	public BoardPiece getBoardPiece(int i) {
		return this.boardPieces[i];
	}

	public void setBoardPiece(BoardPiece boardPiece) {
		this.boardPieces[boardPiece.getFinalLocation() - 1] = boardPiece;
	}

	/**
	 * Move a robot in a direction, and return true if robot has moved, false else
	 *
	 * @param robot     robot that have to move
	 * @param direction direction that the robot is following
	 * @param robots    all the robots of the game
	 * @see DirectionDeplacementEnum
	 * @see Robot
	 */
	public boolean getNewPosition(Robot robot, DirectionDeplacementEnum direction, List<Robot> robots) {
		boolean moved = false;
		while (this.gameBoard[robot.y][robot.x].canContinue(direction) && !robot.robotIsHere(robots, direction)) {
			switch (direction) {
			case RIGHT:
				robot.x++;
				break;
			case LEFT:
				robot.x--;
				break;
			case DOWN:
				robot.y++;
				break;
			case UP:
				robot.y--;
				break;
			default:
				break;
			}
			moved = true;
		}
		return moved;
	}
}
