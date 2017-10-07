package fr.amou.perso.app.rasen.robot.test;

import org.junit.Test;

import fr.amou.perso.app.rasen.robot.controller.Controller;
import fr.amou.perso.app.rasen.robot.game.Constant;
import fr.amou.perso.app.rasen.robot.network.Countdown;
import junit.framework.TestCase;

public class CountDownTest extends TestCase {

    @Test
    public void testCountDown() {
        Controller c = new Controller();
        Countdown count = new Countdown(c);

        assertTrue(count.getTime() == Constant.TIMER);

        count.setTime(20);
        assertTrue(count.getTime() == 20);

        count.startCountdown();
        try {
            Thread.sleep(2000);
            count.stopCountdown();
            assertTrue(count.getTime() < 20);
        } catch (InterruptedException e) {

        }

        count.resetCountdown();
        assertTrue(count.getTime() == Constant.TIMER);

    }
}
