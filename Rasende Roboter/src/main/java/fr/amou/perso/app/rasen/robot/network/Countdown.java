package fr.amou.perso.app.rasen.robot.network;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import fr.amou.perso.app.rasen.robot.controller.Controller;
import fr.amou.perso.app.rasen.robot.game.Constant;

/**
 * Countdown using the Timer class of Java
 */
public class Countdown {
    /**
     * @see Timer
     */
    private Timer timer;
    private int time;

    /**
     * @see Controller
     */
    private final Controller controller;

    /**
     * Constructor of the class
     *
     * @param c
     *            : Controller
     * @see Controller
     */
    public Countdown(final Controller cont) {
        this.controller = cont;
        this.time = Constant.TIMER;

        this.createCountdown();
    }

    /**
     * Creation and management of the countdown
     *
     * @see Timer
     */
    public void createCountdown() {

        final ActionListener action = new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                if (Countdown.this.time > 0) {
                    Countdown.this.time--;
                    Countdown.this.controller.refreshClientCountDown();
                    Countdown.this.controller.refreshColumn();
                } else {
                    Countdown.this.timer.stop();
                    Countdown.this.controller.refreshClientCountDown();
                    if (Countdown.this.controller.getServer() != null) {
                        Countdown.this.controller.getServer().startPlay();
                    }
                }
            }

        };
        this.timer = new Timer(1000, action);
        this.time = Constant.TIMER;
    }

    /**
     * Start the Countdown
     */
    public void startCountdown() {
        this.timer.start();
    }

    /**
     * Stop the Countdown
     */
    public void stopCountdown() {
        this.timer.stop();
    }

    /**
     * Reset the Countdown
     */
    public void resetCountdown() {
        this.time = Constant.TIMER;
        this.controller.refreshClientCountDown();
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(final int time) {
        this.time = time;
        this.controller.refreshClientCountDown();
    }
}
