package fr.amou.perso.app.rasen.robot.game;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Autowired;

import fr.amou.perso.app.rasen.robot.enums.BoardPieceNumberEnum;
import fr.amou.perso.app.rasen.robot.enums.BoardPieceSideEnum;
import fr.amou.perso.app.rasen.robot.enums.BoxTypeEnum;
import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.xsd.BoardPieceType;
import fr.amou.perso.app.rasen.robot.xsd.BoardPieceType.Boxes;
import fr.amou.perso.app.rasen.robot.xsd.BoardPieceType.Boxes.Box.Goal;
import fr.amou.perso.app.rasen.robot.xsd.BoardPieceType.Boxes.Box.Walls;
import fr.amou.perso.app.rasen.robot.xsd.ColorObjectifEnum;
import fr.amou.perso.app.rasen.robot.xsd.PositionMurEnum;
import fr.amou.perso.app.rasen.robot.xsd.TypeObjectifEnum;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * Class containing a quarter of the Board
 */
@Data
@Log4j2
public class BoardPiece {

	@Autowired
	private Unmarshaller unmarshaller;

	private int initialLocation;
	private int finalLocation;

	private List<Box> boxes = new ArrayList<>();

	/** Numéro de la piece du plateau. */
	private BoardPieceNumberEnum boardPieceNumber;

	/** Face de la piece. */
	private BoardPieceSideEnum boardPieceSide;

	private BoardPieceType boardPiece;

	public BoardPiece() {

	}

	public BoardPiece(final int initialLocation, final int finalLocation) {
		this.initialLocation = initialLocation;
		this.finalLocation = finalLocation;

	}

	public void initBoardPiece() {

		this.extractBoxInfo(this.boardPiece);
		this.rotateBoxes();
		this.adjustBoxes();

	}

	/**
	 * Extracts the data of the board piece from the xml file
	 *
	 * @param boardPiece
	 */
	private void extractBoxInfo(BoardPieceType boardPiece) {

		final Boxes xmlBoxes = boardPiece.getBoxes();
		final List<BoardPieceType.Boxes.Box> boxList = xmlBoxes.getBox();

		for (BoardPieceType.Boxes.Box box : boxList) {

			Integer iBox = box.getI();
			Integer jBox = box.getJ();

			Box realBox = new Box();
			realBox.setX(iBox);
			realBox.setY(jBox);

			Walls xmlWalls = box.getWalls();
			List<PositionMurEnum> wallList = xmlWalls.getWall();

			for (PositionMurEnum wall : wallList) {

				DirectionDeplacementEnum direction = DirectionDeplacementEnum.valueOf(wall.name());
				realBox.setWall(direction);

			}

			Goal xmlGoal = box.getGoal();

			if (xmlGoal != null) {
				ColorObjectifEnum xmlColor = xmlGoal.getColor();
				TypeObjectifEnum xmlType = xmlGoal.getType();

				BoxTypeEnum boxType = BoxTypeEnum.valueOf(xmlType.name());
				ColorRobotEnum boxColor = null;

				if (boxType != BoxTypeEnum.MULTI) {
					boxColor = ColorRobotEnum.valueOf(xmlColor.name());
				}

				realBox.setType(boxType, boxColor);

			}
			this.boxes.add(realBox);
		}

	}

	/**
	 * Calculates the number of rotations that are necessary and rotates the boxes
	 */
	private void rotateBoxes() {
		final int difference = this.initialLocation - this.finalLocation;

		if (Math.abs(difference) == 2) {
			this.rotateRight();
			this.rotateRight();
		}

		else if (difference == -1 || difference == 3) {
			this.rotateRight();
		} else if (difference == -3 || difference == 1) {
			this.rotateLeft();
		}

	}

	/**
	 * Rotates the boxes 90 degrees on the right
	 */
	public void rotateRight() {
		int oldX;
		int oldY;

		for (Box b : this.boxes) {
			oldX = b.getX();
			oldY = b.getY();

			b.setX(Constant.NB_BOXES_PER_PIECE - oldY - 1);
			b.setY(oldX);
			this.rotateWallsRight(b);
		}
	}

	/**
	 * Rotates the boxes 90 degrees on the left
	 */
	public void rotateLeft() {
		int oldX;
		int oldY;

		for (Box b : this.boxes) {
			oldX = b.getX();
			oldY = b.getY();

			b.setX(oldY);
			b.setY(Constant.NB_BOXES_PER_PIECE - oldX - 1);
			this.rotateWallsLeft(b);
		}
	}

	/**
	 * Rotates the walls 90 degrees on the right
	 *
	 * @param box the box of which the walls have to be rotated
	 */
	private void rotateWallsRight(final Box box) {

		Boolean oldSouth = box.getIsWallBottom();
		Boolean oldEast = box.getIsWallRight();
		Boolean oldNorth = box.getIsWallTop();
		Boolean oldWest = box.getIsWallLeft();

		box.setIsWallLeft(oldSouth);
		box.setIsWallTop(oldWest);
		box.setIsWallRight(oldNorth);
		box.setIsWallBottom(oldEast);
	}

	/**
	 * Rotates the walls 90 degrees on the left
	 *
	 * @param box the box of which the walls have to be rotated
	 */
	private void rotateWallsLeft(final Box box) {

		Boolean oldSouth = box.getIsWallBottom();
		Boolean oldEast = box.getIsWallRight();
		Boolean oldNorth = box.getIsWallTop();
		Boolean oldWest = box.getIsWallLeft();

		box.setIsWallLeft(oldNorth);
		box.setIsWallTop(oldEast);
		box.setIsWallRight(oldSouth);
		box.setIsWallBottom(oldWest);
	}

	/**
	 * Adjust the coordinates of the boxes in function of the final location of the
	 * board piece
	 */
	private void adjustBoxes() {

		if (this.finalLocation == 2) {
			this.adjustX();
		}

		else if (this.finalLocation == 3) {
			this.adjustX();
			this.adjustY();
		}

		else if (this.finalLocation == 4) {
			this.adjustY();
		}

	}

	/**
	 * Adjusts the Y coordinate
	 */
	private void adjustY() {
		for (Box b : this.boxes) {
			b.setY(b.getY() + Constant.NB_BOXES_PER_PIECE);
		}
	}

	/**
	 * Adjusts the X coordinate
	 */
	private void adjustX() {
		for (Box b : this.boxes) {
			b.setX(b.getX() + Constant.NB_BOXES_PER_PIECE);
		}
	}

}
