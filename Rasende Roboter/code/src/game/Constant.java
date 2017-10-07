package game;

import javax.swing.JMenuBar;

/**
 * Class containing all general constants (sizes) and Enum
 */
public final class Constant {
    public static final String FRAME_TITLE = "Rasende Roboter";

    public static final String IMAGE_PATH = "code/images/";
    public static final String THEME_PATH = IMAGE_PATH + "theme/";

    public static final String MAP_PATH = "code/maps/";

    public static final int CASE_SIZE = 40;
    public static final int NB_BOXES = 16;
    public static final int NB_BOXES_PER_PIECE = 8;
    public static final int BOARD_SIZE = CASE_SIZE * NB_BOXES;

    public static final int COLUMN_WIDTH = 250;

    public static final int FRAME_HEIGHT = BOARD_SIZE + JMenuBar.HEIGHT;
    public static final int FRAME_WIDTH = BOARD_SIZE + COLUMN_WIDTH;

    public static final int NB_BOARD_PIECES = 4;
    public static final int NB_BOARD_PIECES_SIDE = 2;

    public static final int NB_ROBOT = 4;

    public static final int SERVER_PORT = 3001;
    public static final int CLIENT_PORT = 3002;

    public static final int TIMER = 120; // In seconds

    /**
     * List of possible directions.
     */
    public enum Direction {
        Up, Down, Left, Right;
    }

    /**
     * The color of a box or a robot, required for the choice of the associated
     * image.
     */
    public enum Color {
        Blue, Green, Red, Yellow
    }

    /**
     * The type of a box, required for the choice of the associated image.
     */
    public enum BoxType {
        Empty, Central, More, Sun, Diamond, Triangle, Multi;
    }

    private Constant() {

    }
}
