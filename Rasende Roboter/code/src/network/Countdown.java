package network;

import game.Constant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import controller.Controller;




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
	 * @param c : Controller
	 * @see Controller
	 */
	public Countdown(final Controller cont){
		controller = cont;
		this.time = Constant.TIMER;
		
		createCountdown();
	}
	
	/**
	 * Creation and management of the countdown
	 * @see Timer
	 */
	public void createCountdown() {

		final ActionListener action = new ActionListener (){
		
		public void actionPerformed (final ActionEvent event){
			if(time>0)
			{
				time--;
				controller.refreshClientCountDown();
				controller.refreshColumn();
			}
			else
			{
				timer.stop();
				controller.refreshClientCountDown();
				if(controller.getServer()!=null)
					controller.getServer().startPlay();
			}
		}

		};
		timer = new Timer (1000, action); 
		time = Constant.TIMER;
	}
	
	/**
	 * Start the Countdown
	 */
	public void startCountdown()
	{
		timer.start();
	}
	
	/**
	 * Stop the Countdown
	 */
	public void stopCountdown()
	{
		timer.stop();
	}

	/**
	 * Reset the Countdown
	 */
	public void resetCountdown() {
		time=Constant.TIMER;
		controller.refreshClientCountDown();
	}
	
	public int getTime() {
		return time;
	}

	public void setTime(final int time) {
		this.time = time;
		controller.refreshClientCountDown();
	}
}
