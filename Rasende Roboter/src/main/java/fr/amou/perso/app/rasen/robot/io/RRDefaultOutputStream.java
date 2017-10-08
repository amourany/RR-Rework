package fr.amou.perso.app.rasen.robot.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.amou.perso.app.rasen.robot.event.manager.EventManager;

@Component
public class RRDefaultOutputStream extends OutputStream {

    @Autowired
    private EventManager controller;

    public RRDefaultOutputStream() {
        super();

        final PrintStream out = new PrintStream(this);
        System.setOut(out);
    }

    @Override
    public void write(int arg0) throws IOException {
        this.controller.println(String.valueOf((char) arg0));
    }

}
