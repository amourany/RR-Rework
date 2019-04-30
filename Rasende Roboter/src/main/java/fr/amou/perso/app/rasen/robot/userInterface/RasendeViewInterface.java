package fr.amou.perso.app.rasen.robot.userInterface;

public interface RasendeViewInterface {
	public void display();

	public void displayDataInfo();

	public void displayBoard();

	public void displayHelp();

	public void displayWin();

	public void setFocusOnBoard();

	public void afficherMessage(String message);

	public void dispose();

	void buildFrame();

}
