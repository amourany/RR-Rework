package fr.amou.perso.app.rasen.robot.userInterface;

import fr.amou.perso.app.rasen.robot.controller.Controller;
import fr.amou.perso.app.rasen.robot.game.Game;

public interface RasendeViewInterface {
    public void display(final Game game, final Controller c);

    public void displayDataInfo(final Game game, final Controller c);

    public void displayPlayers(final String string);

    public void displayBoard(final Game game);

    public void displayHelp();

    public void displayWin();

    public void displayMoveLimit();

    public void displayConnectionLost();

    public String displayStartServer();

    public String[] displayJoinServer();

    public void setFocusOnBoard();

    public void println(final String string);

    public void setEnabledValidate(Boolean enabled);

    public int getSuggestion();

    public void dispose();

}
