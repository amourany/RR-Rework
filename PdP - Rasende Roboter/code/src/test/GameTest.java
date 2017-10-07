package test;

import static org.junit.Assert.*;
import game.Game;
import game.Constant.Color;
import org.junit.Test;

import controller.Controller;

public class GameTest{

	@Test
	public void NewGame() {
		Controller c = new Controller();
		Game g = c.getgame();
		
		assertTrue(g.getRobots().size()==4);
		assertNull(g.getSelectedRobot());
		assertEquals(g.getCounterLap(),0);
		assertEquals(g.getCurrentRound(),1);		
	}
	
	@Test
	public void SelectARobot(){
		Controller c = new Controller();
		Game g = c.getgame();
	
		g.setSelectedRobot(Color.Blue);
		assertNotNull(g.getSelectedRobot());
		assertEquals(g.getSelectedRobot().getColor(),Color.Blue);
		
		g.setSelectedRobot(Color.Green);
		assertEquals(g.getSelectedRobot().getColor(),Color.Green);
		
		g.setSelectedRobot(Color.Yellow);
		assertEquals(g.getSelectedRobot().getColor(),Color.Yellow);
		
		g.setSelectedRobot(Color.Green);
		assertEquals(g.getSelectedRobot().getColor(),Color.Green);
	}
}