package test;

import java.io.File;
import java.util.List;

import org.junit.Test;

import game.BoardPiece;
import game.Box;
import game.Constant;

public class BoardTest {

    @Test
    public void testRotation() {
        BoardPiece bp1 = new BoardPiece(new File(Constant.MAP_PATH + "/boardpiece1A.xml"), 1, 1);
        BoardPiece bp2 = new BoardPiece(new File(Constant.MAP_PATH + "/boardpiece1A.xml"), 1, 2);
        bp1.initBoardPiece();
        bp2.initBoardPiece();
        List<Box> bp1Boxes = bp1.getBoxes();
        List<Box> bp2Boxes = bp2.getBoxes();
        for (Box b : bp1Boxes) {
            System.out.println(b.getX() + " " + b.getY());
        }
        System.out.println("\n");
        for (Box b : bp2Boxes) {
            System.out.println(b.getX() + " " + b.getY());
            // assertEquals(bp1.getBoxes(),bp2.getBoxes());
        }
    }

}
