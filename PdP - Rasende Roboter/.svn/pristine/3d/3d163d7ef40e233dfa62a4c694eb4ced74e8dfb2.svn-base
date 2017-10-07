package test;

import game.Constant.Color;
import game.Constant.Direction;

import org.junit.Test;

import junit.framework.TestCase;
import controller.Controller;

public class ControllerTest extends TestCase {
	
	@Test
	public void testMoveRobotInDirection() {
		Controller c = new Controller();
		c.setSelectedRobot(Color.Green);
		
		int oldX = c.getgame().getSelectedRobot().x;
		
		c.moveRobotInDirection(Direction.Right);
		
		assertNotSame(oldX, c.getgame().getSelectedRobot().x);
	}
	
	
}