package fr.amou.perso.app.rasen.robot.event.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.amou.perso.app.rasen.robot.event.manager.ControllerService;
import fr.amou.perso.app.rasen.robot.game.Constant;

/**
 * Service de traitements des actions liés à la souris.
 *
 * @author amou
 *
 */
@Component
public class MouseDefaultAdapter extends MouseAdapter {

    @Autowired
    private ControllerService controller;

    @Override
    public void mouseClicked(final MouseEvent e) {
        int line = e.getY() / Constant.CASE_SIZE;
        int column = e.getX() / Constant.CASE_SIZE;

        this.controller.handleMouseAction(line, column);

    }
}
