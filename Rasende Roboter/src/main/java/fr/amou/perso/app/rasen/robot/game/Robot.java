package fr.amou.perso.app.rasen.robot.game;

import java.util.List;

import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import lombok.Data;

/**
 * Class containing the data of a robot (positions, color)
 */
@Data
public class Robot {
	public int x;
	public int y;
	public int originX;
	public int originY;
	/**
	 * @see ColorRobotEnum
	 */
	private ColorRobotEnum color;

	public Robot(final ColorRobotEnum col) {
		this.color = col;
	}

	public Robot(final int x, final int y, final ColorRobotEnum color) {
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

	public Robot(final int x, final int y, final ColorRobotEnum c, final int originX, final int originY) {
		this(x, y, c);
		this.originX = originX;
		this.originY = originY;
	}

	public boolean robotIsHere(final List<Robot> robots, final DirectionDeplacementEnum dir) {
		boolean res = false;

		for (Robot r : robots) {
			switch (dir) {
			case RIGHT:
				if (r.getX() == this.x + 1 && r.getY() == this.y) {
					res = true;
				}
				break;
			case LEFT:
				if (r.getX() == this.x - 1 && r.getY() == this.y) {
					res = true;
				}
				break;
			case DOWN:
				if (r.getY() == this.y + 1 && r.getX() == this.x) {
					res = true;
				}
				break;
			case UP:
				if (r.getY() == this.y - 1 && r.getX() == this.x) {
					res = true;
				}
				break;
			default:
				break;
			}
		}
		return res;
	}

	@Override
	public boolean equals(Object r) {
		boolean res = true;
		if (this.color != ((Robot) r).getColor() || this.x != ((Robot) r).x || this.y != ((Robot) r).y) {
			res = false;
		}
		return res;
	}

	@Override
	public String toString() {
		return this.x + ";" + this.y + ";" + this.color;
	}

	public void newOrigin() {
		this.originX = this.x;
		this.originY = this.y;
	}
}
