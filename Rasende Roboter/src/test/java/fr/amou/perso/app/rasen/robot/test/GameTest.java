package fr.amou.perso.app.rasen.robot.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.amou.perso.app.rasen.robot.controller.Controller;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Game;

public class GameTest {

    @Test
    public void NewGame() {
        Controller c = new Controller();
        Game g = c.getGame();

        assertTrue(g.getRobots().size() == 4);
        assertNull(g.getSelectedRobot());
        assertEquals(g.getCounterLap(), 0);
        assertEquals(g.getCurrentRound(), 1);
    }

    @Test
    public void SelectARobot() {
        Controller c = new Controller();
        Game g = c.getGame();

        g.setSelectedRobot(Color.Blue);
        assertNotNull(g.getSelectedRobot());
        assertEquals(g.getSelectedRobot().getColor(), Color.Blue);

        g.setSelectedRobot(Color.Green);
        assertEquals(g.getSelectedRobot().getColor(), Color.Green);

        g.setSelectedRobot(Color.Yellow);
        assertEquals(g.getSelectedRobot().getColor(), Color.Yellow);

        g.setSelectedRobot(Color.Green);
        assertEquals(g.getSelectedRobot().getColor(), Color.Green);
    }
}
