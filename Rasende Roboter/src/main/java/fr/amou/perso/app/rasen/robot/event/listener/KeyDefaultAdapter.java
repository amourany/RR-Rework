package fr.amou.perso.app.rasen.robot.event.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.event.manager.EventManager;

@Component
public class KeyDefaultAdapter extends KeyAdapter {

	@Autowired
	private EventManager eventManager;

	@Override
	public void keyPressed(final KeyEvent e) {

		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			this.eventManager.moveRobotInDirection(DirectionDeplacementEnum.UP);
			break;
		case KeyEvent.VK_DOWN:
			this.eventManager.moveRobotInDirection(DirectionDeplacementEnum.DOWN);
			break;
		case KeyEvent.VK_RIGHT:
			this.eventManager.moveRobotInDirection(DirectionDeplacementEnum.RIGHT);
			break;
		case KeyEvent.VK_LEFT:
			this.eventManager.moveRobotInDirection(DirectionDeplacementEnum.LEFT);
			break;
		case KeyEvent.VK_1:
		case KeyEvent.VK_R:
			this.eventManager.setSelectedRobot(ColorRobotEnum.RED);
			break;
		case KeyEvent.VK_2:
		case KeyEvent.VK_G:
			this.eventManager.setSelectedRobot(ColorRobotEnum.GREEN);
			break;
		case KeyEvent.VK_3:
		case KeyEvent.VK_B:
			this.eventManager.setSelectedRobot(ColorRobotEnum.BLUE);
			break;
		case KeyEvent.VK_4:
		case KeyEvent.VK_Y:
			this.eventManager.setSelectedRobot(ColorRobotEnum.YELLOW);
			break;
		default:
			break;
		}

	}
}
