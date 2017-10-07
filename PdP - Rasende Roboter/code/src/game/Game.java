package game;

import game.Constant.BoxType;
import game.Constant.Color;
import game.Constant.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import controller.Controller;


import solver.Solver;

/**
 * The model of our architecture
 */
public class Game {
	private Board mBoard;
	private List<Robot> mRobots;
	private Stack<List<Robot>> mPreviousPosition;
	private Stack<List<Robot>> mNextPosition;
	private Robot mSelectedRobot;
	private Stack<Box> mGoalCards;
	private Box currentGoal;
	private Solver mSolver;
	private boolean isOver;
	private String theme = "default/";

	public Game(Controller controller){
		mSolver = new Solver();	

		startNewGame();
	}

	public void startNewGame(){
		generateBoard();
		isOver = false;
		placeRobots();	
		mGoalCards = new Stack<Box>();

		//Cr�ation d'un tableau qui contient toutes les cartes possibles
		int i = 0;
		Box [] goalCardTab = new Box[17];
		for(Color c : Color.values()){
			for(BoxType bt : BoxType.values()){
				if(bt != BoxType.Central && bt != BoxType.Empty && bt != BoxType.Multi){
					goalCardTab[i] = new Box(bt, c);
					i++;
				}
			}
		}
		goalCardTab[i] = new Box(BoxType.Multi, null);

		//Ajout al�atoire des cartes dans la pile
		for (int j = 17; j > 0; j--){
			int k = (int)(Math.random()*100) % j;
			mGoalCards.push(goalCardTab[k]);
			if (k != (j-1)){
				Box tmp = goalCardTab[j-1];
				goalCardTab[j-1] = goalCardTab[k];
				goalCardTab[k] = tmp;
			}
		}
		startNewLap();
	}

	private void placeRobots() {
		mRobots = new ArrayList<Robot>();
		Robot rob;

		for(Color c : Constant.Color.values()){
			rob = new Robot(c);
			rob.placeOnBoard(mRobots);
			while(mBoard.getBox(rob.originY, rob.originX).getType() != Constant.BoxType.Empty)
				rob.placeOnBoard(mRobots);
			mRobots.add(rob);
		}
	}

	/**
	 * Generates a new Board
	 * 
	 */
	private void generateBoard() {
		mBoard = new Board();
		mBoard.initBoard();
		mBoard.setBoard();

	}

	public void startNewLap(){
		if(mGoalCards.empty()) {
			System.out.println("End of game !");
			isOver = true;
		}
		else{
			
			for(Robot r : mRobots) {
				r.newOrigin();
			}
			
			mPreviousPosition = new Stack<List<Robot>>();
			mNextPosition = new Stack<List<Robot>>();
			currentGoal = mGoalCards.pop();
		}
	}

	/**
	 * Getter of gameBoard
	 * @return gameBoard : Board
	 * @see Board
	 */
	public Board getBoard() {
		return mBoard;
	}

	/**
	 * Getter of the robots' list
	 * @return robots : the list of robots
	 * @see Robot
	 * @see List
	 */
	public List<Robot> getRobots() {
		return mRobots;
	}

	/**
	 * Return the robot with the specified Color
	 * @param c : Color, the color of the robot asked
	 * @return r : Robot, if the robot with this color exist, else null
	 * @see Color
	 * @see Robot
	 */
	public Robot getRobot(final Color col) {
		Robot result = null;

		for(Robot r : mRobots) {
			if(r.getColor() == col) {
				result = r;
			}
		}
		return result;
	}

	/**
	 * Setter of the robots' list
	 * @param robots
	 * @see Robot
	 * @see List
	 */
	public void setRobots(final List<Robot> robots) {
		mRobots = robots;
	}

	/**
	 * Getter of Current Round
	 * @return currentRound : int
	 */
	public int getCurrentRound(){
		return 17 - mGoalCards.size();
	}

	public void setRobotByColor(final Color color, final Robot rob) {
		for(int i = 0; i < mRobots.size(); i++) {
			if(mRobots.get(i).getColor() == color) {
				mRobots.set(i, rob);
			}
		}
	}
	
	/**
	 * Save the current position of the robots, save only the position
	 * if it is different from the last
	 */
	private void saveCurrentPosition(){		
		if(!mPreviousPosition.empty())
			if(mPreviousPosition.peek().equals(mRobots))
				return;

		List<Robot> robots = new ArrayList<>();
		for(Robot r : mRobots)
			robots.add(new Robot(r));

		mNextPosition.removeAllElements();		
		mPreviousPosition.push(robots);
	}
	
	/**
	 * Load the previous position
	 */
	public void loadPreviousPosition(){
		List<Robot> robots = new ArrayList<>();
		for(Robot r : mRobots)
			robots.add(new Robot(r));
		mNextPosition.push(robots);		
		mRobots = mPreviousPosition.pop();
	}

	/**
	 * Load the next position
	 */
	public void loadNextPosition(){		
		List<Robot> robots = new ArrayList<>();
		for(Robot r : mRobots)
			robots.add(new Robot(r));
		mPreviousPosition.push(robots);		
		mRobots = mNextPosition.pop();
	}

	/**
	 * Move a robot in a direction
	 * @param robot : the robot to move
	 * @param direction : Left, Right, Down, Up
	 * @see Direction
	 * @see Robot
	 */
	public void moveSelectedRobot(final Direction dir){
		saveCurrentPosition();

		mBoard.getNewPosition(mSelectedRobot, dir, mRobots);
		if (isWin(mSelectedRobot)) {
			startNewLap();
		}
	}

	public boolean isWin(final Robot rob) {
		boolean win = false;

		if(rob.getColor() == currentGoal.getColor()){
			if (mBoard.getGameBoard()[rob.y][rob.x].getType() == currentGoal.getType()
					&& mBoard.getGameBoard()[rob.y][rob.x].getColor() == currentGoal.getColor()) {
				win = true;
			}
		}
		else {
			if (mBoard.getGameBoard()[rob.y][rob.x].getType() == currentGoal.getType()
					&&	currentGoal.getType() == BoxType.Multi){
				win = true;
			}
		}
		return win;
	}

	public Robot getSelectedRobot() {
		return mSelectedRobot;
	}

	public void setSelectedRobot(final Robot selectedRobot) {
		mSelectedRobot = selectedRobot;
	}

	public void setSelectedRobot(final Color color){
		for(Robot r : mRobots) {
			if(r.getColor() == color) {
				mSelectedRobot = r;
			}
		}
	}

	public int getCounterLap(){
		return mPreviousPosition.size();
	}

	public void startSolver(){
		mSolver.solve(this);
	}

	public Stack<Box> getStack(){
		return mGoalCards;
	}

	public void initStack(){
		while (!mGoalCards.isEmpty()) {
			mGoalCards.pop();
		}
	}

	public Box getCurrentGoal() {
		return currentGoal;
	}

	public void setCurrentGoal(Box goal) {
		currentGoal = goal;
	}

	public boolean isOver() {
		return isOver;
	}

	public Stack<Box> getmGoalCards() {
		return mGoalCards;
	}

	public void setmGoalCards(Stack<Box> mGoalCards) {
		this.mGoalCards = mGoalCards;
	}

	public String getTheme(){
		return theme;
	}

	public void setTheme(String theme){
		this.theme = theme;
	}
	
	public Boolean hasPreviousPosition(){
		return !mPreviousPosition.empty();
	}
	
	public Boolean hasNextPosition(){
		return !mNextPosition.empty();
	}

	public Stack<List<Robot>> getmPreviousPosition() {
		return mPreviousPosition;
	}

	public void setmPreviousPosition(Stack<List<Robot>> mPreviousPosition) {
		this.mPreviousPosition = mPreviousPosition;
	}

	public Stack<List<Robot>> getmNextPosition() {
		return mNextPosition;
	}

	public void setmNextPosition(Stack<List<Robot>> mNextPosition) {
		this.mNextPosition = mNextPosition;
	}
}