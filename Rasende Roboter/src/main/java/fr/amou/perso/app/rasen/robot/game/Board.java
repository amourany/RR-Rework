package fr.amou.perso.app.rasen.robot.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.amou.perso.app.rasen.robot.game.Constant.BoxType;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;

/**
 * Class containing the Board
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
            // InputStream is = ClassLoader.getSystemResourceAsStream(Constant.MAP_PATH +
            // "boardpiece"
            // + this.numBoardPieces.get(i) + this.boardPiecesSide[i] + ".xml");
            // File f = FileUtils
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
            this.gameBoard[i][0].setWall(Constant.Direction.Left);
            this.gameBoard[i][Constant.NB_BOXES - 1].setWall(Constant.Direction.Right);
            this.gameBoard[0][i].setWall(Constant.Direction.Up);
            this.gameBoard[Constant.NB_BOXES - 1][i].setWall(Constant.Direction.Down);
        }
    }

    /**
     * Put central boxes of the board with a wall around this area
     */
    private void putCentralBoxes() {
        this.gameBoard[7][7].setWall(Direction.Up);
        this.gameBoard[7][7].setWall(Direction.Left);

        this.gameBoard[7][8].setWall(Direction.Up);
        this.gameBoard[7][8].setWall(Direction.Right);

        this.gameBoard[8][7].setWall(Direction.Down);
        this.gameBoard[8][7].setWall(Direction.Left);

        this.gameBoard[8][8].setWall(Direction.Down);
        this.gameBoard[8][8].setWall(Direction.Right);

        this.gameBoard[7][7].setType(BoxType.Central, null);
        this.gameBoard[7][8].setType(BoxType.Central, null);
        this.gameBoard[8][7].setType(BoxType.Central, null);
        this.gameBoard[8][8].setType(BoxType.Central, null);
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
     * @see Direction
     * @see Robot
     */
    public boolean getNewPosition(Robot robot, Direction direction, List<Robot> robots) {
        boolean moved = false;
        while (this.gameBoard[robot.y][robot.x].canContinue(direction) && !robot.robotIsHere(robots, direction)) {
            switch (direction) {
            case Right:
                robot.x++;
                break;
            case Left:
                robot.x--;
                break;
            case Down:
                robot.y++;
                break;
            case Up:
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

    /**
     * Setter of GameBoard
     *
     * @param gameBoard
     */
    public void setGameBoard(Box[][] gameBoard) {
        this.gameBoard = gameBoard;
    }

    public List<Integer> getNumBoardPieces() {
        return this.numBoardPieces;
    }

    public void setNumBoardPieces(List<Integer> numBoardPieces) {
        this.numBoardPieces = numBoardPieces;
    }

    public char[] getBoardPiecesSide() {
        return this.boardPiecesSide;
    }

    public void setBoardPiecesSide(char boardPiecesSide[]) {
        this.boardPiecesSide = boardPiecesSide;
    }

    public BoardPiece getBoardPiece(int i) {
        return this.boardPieces[i];
    }

    public BoardPiece[] getBoardPiece() {
        return this.boardPieces;
    }

    public void setBoardPiece(BoardPiece boardPiece) {
        this.boardPieces[boardPiece.getFinalLocation() - 1] = boardPiece;
    }
}
