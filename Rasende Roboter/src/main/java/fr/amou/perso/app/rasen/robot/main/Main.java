package fr.amou.perso.app.rasen.robot.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import fr.amou.perso.app.rasen.robot.context.RasendeContext;
import fr.amou.perso.app.rasen.robot.game.manager.GameManager;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {

	/**
	 *
	 * @param args
	 */
	public static void main(String args[]) {
		ApplicationContext context = new AnnotationConfigApplicationContext(RasendeContext.class);
		GameManager gameManager = context.getBean(GameManager.class);
		try {
			gameManager.run();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

}
