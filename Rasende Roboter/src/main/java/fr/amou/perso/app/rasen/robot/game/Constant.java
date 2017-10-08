package fr.amou.perso.app.rasen.robot.game;

import javax.swing.JMenuBar;

/**
 * Class containing all general constants (sizes) and Enum
 */
public final class Constant {
    public static final String FRAME_TITLE = "Rasende Roboter";

    public static final String IMAGE_PATH = "src/main/resources/images/";
    public static final String THEME_PATH = IMAGE_PATH + "theme/";

    public static final String MAP_PATH = "src/main/resources/maps/";

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

    private Constant() {

    }
}
