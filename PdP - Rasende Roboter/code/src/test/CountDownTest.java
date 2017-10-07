package test;

import junit.framework.TestCase;
import game.Constant;
import network.Countdown;

import org.junit.Test;

import controller.Controller;

public class CountDownTest extends TestCase{
	
	@Test
	public void testCountDown() {
		Controller c = new Controller();
		Countdown count = new Countdown(c);
		
		assertTrue(count.getTime()==Constant.TIMER);
		
		count.setTime(20);
		assertTrue(count.getTime()==20);
		
		count.startCountdown();
		try {
			Thread.sleep(2000);
			count.stopCountdown();
			assertTrue(count.getTime()<20);
		} catch (InterruptedException e) {
			
		}
		
		count.resetCountdown();
		assertTrue(count.getTime()==Constant.TIMER);
		
	}
}
