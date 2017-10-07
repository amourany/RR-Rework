package fr.amou.perso.app.rasen.robot.game;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

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
    private final transient SAXBuilder sxb;

    private int initialLocation;
    private int finalLocation;

    private final transient List<Box> boxes;

    public BoardPiece(final File xmlFile, final int initialLocation, final int finalLocation) {
        this.initialLocation = initialLocation;
        this.finalLocation = finalLocation;

        this.xmlFile = xmlFile;
        this.sxb = new SAXBuilder();

        this.boxes = new ArrayList<>();
    }

    public void initBoardPiece() {
        Document xmlDoc = new Document();

        try {
            xmlDoc = this.sxb.build(this.xmlFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.extractBoxInfo(xmlDoc.getRootElement());
        this.rotateBoxes();
        this.adjustBoxes();
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
     * @param box
     *            the box of which the walls have to be rotated
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
     * @param box
     *            the box of which the walls have to be rotated
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

    /**
     * Extracts the data of the board piece from the xml file
     *
     * @param elem
     */
    private void extractBoxInfo(final Element elem) {

        final Element xmlBoxes = elem.getChild("boxes");
        final List<Element> xmlBox = xmlBoxes.getChildren("box");

        Element xmlWalls;
        List<Element> walls;

        Element xmlGoal;

        Element xmlColor;
        Element xmlType;

        Constant.BoxType boxType;
        Constant.Color boxColor;

        Box realBox;
        int iBox, jBox;

        for (Element box : xmlBox) {
            realBox = new Box();

            iBox = Integer.parseInt(box.getChild("i").getText());
            jBox = Integer.parseInt(box.getChild("j").getText());

            realBox.setX(iBox);
            realBox.setY(jBox);

            xmlWalls = box.getChild("walls");
            walls = xmlWalls.getChildren("wall");

            for (Element wall : walls) {

                if (wall.getText().equals("Top")) {
                    realBox.setWall(Constant.Direction.Up);
                } else if (wall.getText().equals("Right")) {
                    realBox.setWall(Constant.Direction.Right);
                } else if (wall.getText().equals("Bottom")) {
                    realBox.setWall(Constant.Direction.Down);
                } else if (wall.getText().equals("Left")) {
                    realBox.setWall(Constant.Direction.Left);
                }
            }

            xmlGoal = box.getChild("Goal");

            if (xmlGoal != null) {
                xmlColor = box.getChild("Goal").getChild("color");
                xmlType = box.getChild("Goal").getChild("type");

                boxType = Constant.BoxType.valueOf(xmlType.getText());
                if (boxType == Constant.BoxType.Multi) {
                    boxColor = null;
                } else {
                    boxColor = Constant.Color.valueOf(xmlColor.getText());
                }

                realBox.setType(boxType, boxColor);

            }
            this.boxes.add(realBox);
        }

    }

}
