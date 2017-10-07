package game;

import game.Constant.Color;
import game.Constant.Direction;

import java.util.List;


/**
 * Class containing the data of a robot (positions, color)
 */
public class Robot {
	public int x;
	public int y;
	public int originX;
	public int originY;
	/**
	 * @see Color
	 */
	private Color color;

	public Robot(final Color col) {
		color = col;
	}

	public Robot(final int x, final int y, final Color color) {
		this.x = x;
		this.y = y;
		this.originX = x;
		this.originY = y;
		this.color = color;
	}

	public Robot(final Robot rob) {
		this(rob.x, rob.y, rob.color);
		this.originX = rob.originX;
		this.originY = rob.originY;
		
	}

	public Robot(final int x, final int y, final Color c, final int originX, final int originY) {
		this(x, y, c);
		this.originX = originX;
		this.originY = originY;
	}

	/**
	 * Place a new Robot on the board
	 * @param robots : List<Robot>, set of the robots already on the board, use to know if the new robot is alone on the box
	 */
	public void placeOnBoard(final List<Robot> robots) {
		boolean isAlone; //Is the robot alone on the box ?

		do {
			isAlone = true;
			do 
				x = (int)(Math.random() * (15 + 1)); // robots positions are between box 0 and box 15 without boxes 7 and 8
			while(x == 7 || x == 8);

			do
				y = (int)(Math.random() * (15 + 1));
			while(x == 7 || x == 8);

			for(Robot r : robots) {
				if((x == r.x) && (y == r.y)) {
					isAlone = false;
				}
			}

		}while(!isAlone);

		originX = x;
		originY = y;
	}

	public int getX() {
		return x;
	}

	public void setX(final int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(final int y) {
		this.y = y;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public int getOriginX() {
		return originX;
	}

	public void setOriginX(final int originX) {
		this.originX = originX;
	}

	public int getOriginY() {
		return originY;
	}

	public void setOriginY(final int originY) {
		this.originY = originY;
	}

	public boolean robotIsHere(final List<Robot> robots, final Direction dir){
		boolean res = false;
		
		for(Robot r: robots){
			switch (dir) {
			case Right:
				if (r.getX() == x+1 && r.getY()== y){ res = true; }
				break;
			case Left:
				if (r.getX()== x-1 && r.getY()== y) { res = true; }
				break;
			case Down:
				if (r.getY()== y+1 && r.getX()== x) { res = true; }
				break;
			case Up:
				if (r.getY()==y-1 && r.getX()== x) { res = true; }
				break;
			default:
				break;
			}
		}
		return res;
	}
	
	public boolean equals(Object r) {
		boolean res = true;
		if(color != ((Robot)r).getColor() || x != ((Robot)r).x || y != ((Robot)r).y) { res = false; }
		return res;
	}
	
	public String toString(){
		return x + ";" + y + ";" + color;
	}

	public void newOrigin() {
		originX = x;
		originY = y;
	}
}
