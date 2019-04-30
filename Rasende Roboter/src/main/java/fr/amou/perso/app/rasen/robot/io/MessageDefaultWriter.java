package fr.amou.perso.app.rasen.robot.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.amou.perso.app.rasen.robot.event.manager.EventManager;

@Component
public class MessageDefaultWriter implements MessageWriter {

	@Autowired
	private EventManager controller;

	@Override
	public void ecrireMessage(String message) {
		this.controller.afficherMessage(message + "\n");
	}

}
