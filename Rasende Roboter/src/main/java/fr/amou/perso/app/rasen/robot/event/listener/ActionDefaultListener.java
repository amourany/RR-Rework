package fr.amou.perso.app.rasen.robot.event.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.amou.perso.app.rasen.robot.enums.ActionPossibleEnum;
import fr.amou.perso.app.rasen.robot.event.manager.EventManager;

@Component
public class ActionDefaultListener implements ActionListener {

    @Autowired
    private EventManager eventManager;

    @Override
    public void actionPerformed(ActionEvent e) {

        String actionString = e.getActionCommand();
        ActionPossibleEnum action = ActionPossibleEnum.valueOf(actionString);

        switch (action) {
        case ACTION_PREVIOUS:
            this.eventManager.loadPreviousPosition();
            break;
        case ACTION_NEXT:
            this.eventManager.loadNextPosition();
            break;
        case ACTION_QUIT:
            this.eventManager.askToQuit();
            break;
        case ACTION_NEW_GAME:
            this.eventManager.startNewGame();
            break;
        case ACTION_HELP:
            this.eventManager.displayHelp();
            break;
        case ACTION_SOLVE:
            this.eventManager.startSolver();
            break;
        case ACTION_THEME_DEFAULT:
            // this.game.setTheme("default/");
            // this.frame.displayBoard(this.game);
            break;
        default:
            System.out.println("Unknow Action");
            break;
        }

    }

}
