package userInterface;

import controller.Controller;
import game.Game;

public interface RasendeViewInterface {
    public void display(final Game game, final Controller c);

    public void displayDataInfo(final Game game, final Controller c);

    public void displayPlayers(final String string);

    public void displayBoard(final Game game);

    public void displayHelp();

    public void displayWin();

    public void displayMoveLimit();

    public void displayLicense();

    public void displayConnectionLost();

    public String displayStartServer();

    public String[] displayJoinServer();

    public void setFocusOnBoard();

    public void println(final String string);

    public void setOnlinePerspective(Boolean online);

    public void setEnabledForfeit(Boolean enabled);

    public void setEnabledValidate(Boolean enabled);

    public int getSuggestion();

    public void dispose();

}
