package test;

import game.Box;
import game.Constant.BoxType;
import game.Constant.Color;
import game.Constant.Direction;

import org.junit.Test;

import junit.framework.TestCase;

public class BoxTest extends TestCase {
	
	@Test
	public void testCanContinue() {
		Box b = new Box();
		assertEquals(true, b.canContinue(Direction.Down));
		
		b = new Box(true, true, false, false, Color.Blue, BoxType.Sun);
		assertEquals(false, b.canContinue(Direction.Up));
		assertEquals(false, b.canContinue(Direction.Right));
		assertEquals(true, b.canContinue(Direction.Left));
		assertEquals(null, b.getColor(),Color.Blue );
		assertEquals(null, b.getType(),BoxType.Sun );	
	}
	
	@Test	
	public void testWalls() {
		Box b = new Box();
		
		b.setWall(Direction.Up);
		assertEquals(true, b.isNorth());
		
		b.setWall(Direction.Down);
		assertEquals(true, b.isSouth());
		
		b.setWall(Direction.Left);
		assertEquals(true, b.isWest());
		
		b.setWall(Direction.Right);
		assertEquals(true, b.isEast());		
	}
	
}