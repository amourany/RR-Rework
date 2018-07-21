package fr.amou.perso.app.rasen.robot.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import fr.amou.perso.app.rasen.robot.enums.BoxTypeEnum;
import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.xsd.Boardpiece;
import fr.amou.perso.app.rasen.robot.xsd.Boardpiece.Boxes;
import fr.amou.perso.app.rasen.robot.xsd.Boardpiece.Boxes.Box.Goal;
import fr.amou.perso.app.rasen.robot.xsd.Boardpiece.Boxes.Box.Walls;
import fr.amou.perso.app.rasen.robot.xsd.ColorObjectifEnum;
import fr.amou.perso.app.rasen.robot.xsd.PositionMurEnum;
import fr.amou.perso.app.rasen.robot.xsd.TypeObjectifEnum;
import lombok.Data;

/**
 * Class containing a quarter of the Board
 */
@Data
public class BoardPiece {
	/**
	 * @see Box
	 */

	private File xmlFile;

	private int initialLocation;
	private int finalLocation;

	private final transient List<Box> boxes;

	public BoardPiece(final File xmlFile, final int initialLocation, final int finalLocation) {
		this.initialLocation = initialLocation;
		this.finalLocation = finalLocation;

		this.xmlFile = xmlFile;

		this.boxes = new ArrayList<>();
	}

	public void initBoardPiece() {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance("fr.amou.perso.app.rasen.robot.xsd");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			Boardpiece boardPiece = (Boardpiece) unmarshaller.unmarshal(this.xmlFile);

			this.extractBoxInfo(boardPiece);
			this.rotateBoxes();
			this.adjustBoxes();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Extracts the data of the board piece from the xml file
	 *
	 * @param boardPiece
	 */
	private void extractBoxInfo(Boardpiece boardPiece) {

		final Boxes xmlBoxes = boardPiece.getBoxes();
		final List<Boardpiece.Boxes.Box> boxList = xmlBoxes.getBox();

		for (Boardpiece.Boxes.Box box : boxList) {

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

		final boolean oldSouth = box.isSouth();
		final boolean oldEast = box.isEast();
		final boolean oldNorth = box.isNorth();
		final boolean oldWest = box.isWest();

		box.setWest(oldSouth);
		box.setNorth(oldWest);
		box.setEast(oldNorth);
		box.setSouth(oldEast);
	}

	/**
	 * Rotates the walls 90 degrees on the left
	 *
	 * @param box the box of which the walls have to be rotated
	 */
	private void rotateWallsLeft(final Box box) {

		final boolean oldSouth = box.isSouth();
		final boolean oldEast = box.isEast();
		final boolean oldNorth = box.isNorth();
		final boolean oldWest = box.isWest();

		box.setWest(oldNorth);
		box.setNorth(oldEast);
		box.setEast(oldSouth);
		box.setSouth(oldWest);
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
