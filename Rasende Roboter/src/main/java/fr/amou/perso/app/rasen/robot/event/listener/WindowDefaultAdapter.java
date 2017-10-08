package fr.amou.perso.app.rasen.robot.event.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.amou.perso.app.rasen.robot.event.manager.EventManager;

@Component
public class WindowDefaultAdapter extends WindowAdapter {

    @Autowired
    private EventManager eventManager;

    @Override
    public void windowClosing(WindowEvent e) {
        this.eventManager.askToQuit();
    }
}
