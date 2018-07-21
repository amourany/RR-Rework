package fr.amou.perso.app.rasen.robot.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.amou.perso.app.rasen.robot.enums.BoxTypeEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import lombok.Data;

/**
 * Class containing the Board
 */
@Data
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

        this.numBoardPieces = new ArrayList<>();
        this.boardPiecesSide = new char[Constant.NB_BOARD_PIECES];
        this.boardPieces = new BoardPiece[Constant.NB_BOARD_PIECES];
        this.gameBoard = new Box[Constant.NB_BOXES][Constant.NB_BOXES];
    }

    public void initBoard() {

        this.randomizeNumBoardPieces();
        this.randomizeBoardPiecesSide();

        this.initBoardPieces();
        this.setBoard();

    }

    /**
     * Initializes the board pieces with their xml file, initialLocation and
     * finalLocation
     */
    private void initBoardPieces() {
        String mapPath;
        int initialLocation;
        int finalLocation;
        BoardPiece bp;

        for (int i = 0; i < Constant.NB_BOARD_PIECES; i++) {
            mapPath = Constant.MAP_PATH + "boardpiece" + this.numBoardPieces.get(i) + this.boardPiecesSide[i] + ".xml";
            initialLocation = this.numBoardPieces.get(i);
            finalLocation = i + 1;

            bp = new BoardPiece(new File(mapPath), initialLocation, finalLocation);
            bp.initBoardPiece();
            this.boardPieces[i] = bp;
        }
    }

    /**
     * Chooses a random board piece
     */
    private void randomizeNumBoardPieces() {
        for (int i = 0; i < Constant.NB_BOARD_PIECES; i++) {
            this.numBoardPieces.add(i + 1);
        }

        Collections.shuffle(this.numBoardPieces);
    }

    /**
     * Chooses a random side for a board piece
     */
    private void randomizeBoardPiecesSide() {
        for (int i = 0; i < Constant.NB_BOARD_PIECES; i++) {
            this.boardPiecesSide[i] = ((int) (Math.random() * 100) % Constant.NB_BOARD_PIECES_SIDE + 1) == 1 ? 'A'
                    : 'B';
        }
    }

    /**
     * Sets up the board by putting the right boxes into the matrix
     */
    public void setBoard() {
        for (int i = 0; i < Constant.NB_BOXES; i++) {
            for (int j = 0; j < Constant.NB_BOXES; j++) {
                this.gameBoard[i][j] = new Box();
            }
        }

        List<Box> boxes;

        for (int i = 0; i < Constant.NB_BOARD_PIECES; i++) {
            boxes = this.boardPieces[i].getBoxes();

            for (Box b : boxes) {
                this.gameBoard[b.getY()][b.getX()] = b;
            }
        }

        this.putCentralBoxes();
        this.putSurroundingWalls();
        this.oppositeWall();
    }

    /**
     * Put walls on the edges of the board
     */
    private void putSurroundingWalls() {
        for (int i = 0; i < Constant.NB_BOXES; i++) {
            this.gameBoard[i][0].setWall(DirectionDeplacementEnum.LEFT);
            this.gameBoard[i][Constant.NB_BOXES - 1].setWall(DirectionDeplacementEnum.RIGHT);
            this.gameBoard[0][i].setWall(DirectionDeplacementEnum.UP);
            this.gameBoard[Constant.NB_BOXES - 1][i].setWall(DirectionDeplacementEnum.DOWN);
        }
    }

    /**
     * Put central boxes of the board with a wall around this area
     */
    private void putCentralBoxes() {
        this.gameBoard[7][7].setWall(DirectionDeplacementEnum.UP);
        this.gameBoard[7][7].setWall(DirectionDeplacementEnum.LEFT);

        this.gameBoard[7][8].setWall(DirectionDeplacementEnum.UP);
        this.gameBoard[7][8].setWall(DirectionDeplacementEnum.RIGHT);

        this.gameBoard[8][7].setWall(DirectionDeplacementEnum.DOWN);
        this.gameBoard[8][7].setWall(DirectionDeplacementEnum.LEFT);

        this.gameBoard[8][8].setWall(DirectionDeplacementEnum.DOWN);
        this.gameBoard[8][8].setWall(DirectionDeplacementEnum.RIGHT);

        this.gameBoard[7][7].setType(BoxTypeEnum.CENTRAL, null);
        this.gameBoard[7][8].setType(BoxTypeEnum.CENTRAL, null);
        this.gameBoard[8][7].setType(BoxTypeEnum.CENTRAL, null);
        this.gameBoard[8][8].setType(BoxTypeEnum.CENTRAL, null);
    }

    /**
     * Build the opposite wall Example :If the north wall of a box exist then the
     * south wall of the box above must be built
     */
    public void oppositeWall() {
        for (int i = 0; i < Constant.NB_BOXES; i++) {
            for (int j = 0; j < Constant.NB_BOXES; j++) {
                if (this.gameBoard[i][j].isEast() && j != Constant.NB_BOXES - 1) {
                    this.gameBoard[i][j + 1].setWest(true);
                }
                if (this.gameBoard[i][j].isWest() && j != 0) {
                    this.gameBoard[i][j - 1].setEast(true);
                }
                if (this.gameBoard[i][j].isNorth() && i != 0) {
                    this.gameBoard[i - 1][j].setSouth(true);
                }
                if (this.gameBoard[i][j].isSouth() && i != Constant.NB_BOXES - 1) {
                    this.gameBoard[i + 1][j].setNorth(true);
                }
            }
        }
    }

    /**
     * Move a robot in a direction, and return true if robot has moved, false else
     *
     * @param robot
     *            robot that have to move
     * @param direction
     *            direction that the robot is following
     * @param robots
     *            all the robots of the game
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
}
