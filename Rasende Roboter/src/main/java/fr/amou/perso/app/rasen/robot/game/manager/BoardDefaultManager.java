package fr.amou.perso.app.rasen.robot.game.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.amou.perso.app.rasen.robot.enums.BoardPieceNumberEnum;
import fr.amou.perso.app.rasen.robot.enums.BoardPieceSideEnum;
import fr.amou.perso.app.rasen.robot.enums.BoxTypeEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.game.Board;
import fr.amou.perso.app.rasen.robot.game.BoardPiece;
import fr.amou.perso.app.rasen.robot.game.Box;
import fr.amou.perso.app.rasen.robot.game.Constant;
import fr.amou.perso.app.rasen.robot.game.data.GameModel;
import fr.amou.perso.app.rasen.robot.xsd.BoardPieceType;
import lombok.extern.log4j.Log4j2;

/**
 * Manager du plateau de jeu.
 *
 * @author amourany
 *
 */
@Service
@Log4j2
public class BoardDefaultManager implements BoardManager {

	/** Plateau de jeu. */
	private Board plateau;

	@Autowired
	private GameModel gameModel;

	/**
	 * Unmarshaller - Utilisé pour lire les xml contenant les informations des
	 * morceaux de plateau.
	 */
	@Autowired
	private Unmarshaller unmarshaller;

	@Override
	public void initialiserPlateau() {
		this.plateau = new Board();

		this.initBoardPieces();
		this.setBoard();

		this.gameModel.setBoard(this.plateau);

	}

	private void initBoardPieces() {
		List<BoardPiece> boardPieceList = new ArrayList<>();
		BoardPieceSideEnum[] boardPieceSideValues = BoardPieceSideEnum.values();
		Random randomGenerator = new Random();

		// Récupération de toutes les faces des morceaux de plateau à utiliser.
		for (BoardPieceNumberEnum boardPieceNumber : BoardPieceNumberEnum.values()) {

			Integer random = randomGenerator.nextInt(2);
			BoardPieceSideEnum boardPieceSide = boardPieceSideValues[random];

			BoardPiece boardPiece = new BoardPiece();
			boardPiece.setBoardPieceNumber(boardPieceNumber);
			boardPiece.setBoardPieceSide(boardPieceSide);

			Integer number = boardPieceNumber.ordinal() + 1;

			Integer initialLocation = boardPieceNumber.ordinal();
			Integer finalLocation = number;

			boardPiece.setInitialLocation(initialLocation);
			boardPiece.setFinalLocation(finalLocation);

			String xmlPath = Constant.MAP_PATH + "boardpiece" + number + boardPieceSide.name() + ".xml";
			File xml = new File(xmlPath);

			try {
				JAXBElement<BoardPieceType> jaxbElement = (JAXBElement<BoardPieceType>) this.unmarshaller
						.unmarshal(xml);
				BoardPieceType boardPieceType = jaxbElement.getValue();
				boardPiece.setBoardPiece(boardPieceType);
				boardPiece.initBoardPiece();
			} catch (RuntimeException | JAXBException e) {
				log.error(e.getMessage(), e);
			}

			boardPieceList.add(boardPiece);
		}

		// Mélange des morceaux de plateau.
		Collections.shuffle(boardPieceList);

		BoardPiece[] boardPieceArray = boardPieceList.toArray(new BoardPiece[Constant.NB_BOARD_PIECES]);

		this.plateau.setBoardPieces(boardPieceArray);

	}

	/**
	 * Sets up the board by putting the right boxes into the matrix
	 */
	public void setBoard() {

		Box[][] gameBoard = new Box[Constant.NB_BOXES][Constant.NB_BOXES];

		for (int i = 0; i < Constant.NB_BOXES; i++) {
			for (int j = 0; j < Constant.NB_BOXES; j++) {
				gameBoard[i][j] = new Box();
			}
		}

		List<Box> boxes;

		for (int i = 0; i < Constant.NB_BOARD_PIECES; i++) {
			boxes = this.plateau.getBoardPieces()[i].getBoxes();

			for (Box b : boxes) {
				gameBoard[b.getY()][b.getX()] = b;
			}
		}

		this.putCentralBoxes(gameBoard);
		this.putSurroundingWalls(gameBoard);
		this.oppositeWall(gameBoard);

		this.plateau.setGameBoard(gameBoard);
	}

	/**
	 * Put walls on the edges of the board
	 *
	 * @param gameBoard
	 */
	private void putSurroundingWalls(Box[][] gameBoard) {
		for (int i = 0; i < Constant.NB_BOXES; i++) {
			gameBoard[i][0].setWall(DirectionDeplacementEnum.LEFT);
			gameBoard[i][Constant.NB_BOXES - 1].setWall(DirectionDeplacementEnum.RIGHT);
			gameBoard[0][i].setWall(DirectionDeplacementEnum.UP);
			gameBoard[Constant.NB_BOXES - 1][i].setWall(DirectionDeplacementEnum.DOWN);
		}
	}

	/**
	 * Put central boxes of the board with a wall around this area
	 *
	 * @param gameBoard
	 */
	private void putCentralBoxes(Box[][] gameBoard) {
		gameBoard[7][7].setWall(DirectionDeplacementEnum.UP);
		gameBoard[7][7].setWall(DirectionDeplacementEnum.LEFT);

		gameBoard[7][8].setWall(DirectionDeplacementEnum.UP);
		gameBoard[7][8].setWall(DirectionDeplacementEnum.RIGHT);

		gameBoard[8][7].setWall(DirectionDeplacementEnum.DOWN);
		gameBoard[8][7].setWall(DirectionDeplacementEnum.LEFT);

		gameBoard[8][8].setWall(DirectionDeplacementEnum.DOWN);
		gameBoard[8][8].setWall(DirectionDeplacementEnum.RIGHT);

		gameBoard[7][7].setType(BoxTypeEnum.CENTRAL, null);
		gameBoard[7][8].setType(BoxTypeEnum.CENTRAL, null);
		gameBoard[8][7].setType(BoxTypeEnum.CENTRAL, null);
		gameBoard[8][8].setType(BoxTypeEnum.CENTRAL, null);
	}

	/**
	 * Build the opposite wall Example :If the north wall of a box exist then the
	 * south wall of the box above must be built
	 *
	 * @param gameBoard
	 */
	public void oppositeWall(Box[][] gameBoard) {
		for (int i = 0; i < Constant.NB_BOXES; i++) {
			for (int j = 0; j < Constant.NB_BOXES; j++) {
				if (gameBoard[i][j].isEast() && j != Constant.NB_BOXES - 1) {
					gameBoard[i][j + 1].setWest(true);
				}
				if (gameBoard[i][j].isWest() && j != 0) {
					gameBoard[i][j - 1].setEast(true);
				}
				if (gameBoard[i][j].isNorth() && i != 0) {
					gameBoard[i - 1][j].setSouth(true);
				}
				if (gameBoard[i][j].isSouth() && i != Constant.NB_BOXES - 1) {
					gameBoard[i + 1][j].setNorth(true);
				}
			}
		}
	}

}
