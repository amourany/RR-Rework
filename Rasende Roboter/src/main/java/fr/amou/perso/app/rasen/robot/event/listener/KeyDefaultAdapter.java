package fr.amou.perso.app.rasen.robot.event.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.amou.perso.app.rasen.robot.event.manager.ControllerService;
import fr.amou.perso.app.rasen.robot.game.Constant.Color;
import fr.amou.perso.app.rasen.robot.game.Constant.Direction;

@Component
public class KeyDefaultAdapter extends KeyAdapter {

    @Autowired
    private ControllerService controller;

    @Override
    public void keyPressed(final KeyEvent e) {

        switch (e.getKeyCode()) {
        case KeyEvent.VK_UP:
            this.controller.moveRobotInDirection(Direction.Up);
            break;
        case KeyEvent.VK_DOWN:
            this.controller.moveRobotInDirection(Direction.Down);
            break;
        case KeyEvent.VK_RIGHT:
            this.controller.moveRobotInDirection(Direction.Right);
            break;
        case KeyEvent.VK_LEFT:
            this.controller.moveRobotInDirection(Direction.Left);
            break;
        case KeyEvent.VK_1:
        case KeyEvent.VK_R:
            this.controller.setSelectedRobot(Color.Red);
            break;
        case KeyEvent.VK_2:
        case KeyEvent.VK_G:
            this.controller.setSelectedRobot(Color.Green);
            break;
        case KeyEvent.VK_3:
        case KeyEvent.VK_B:
            this.controller.setSelectedRobot(Color.Blue);
            break;
        case KeyEvent.VK_4:
        case KeyEvent.VK_Y:
            this.controller.setSelectedRobot(Color.Yellow);
            break;
        default:
            break;
        }

        this.controller.refreshBoard();
    }
}
