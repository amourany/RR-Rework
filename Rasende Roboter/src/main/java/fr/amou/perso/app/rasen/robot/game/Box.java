package fr.amou.perso.app.rasen.robot.game;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import fr.amou.perso.app.rasen.robot.enums.BoxTypeEnum;
import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.game.data.GameModel;
import lombok.Data;

/**
 * Class containing the data of a box (walls, color and type)
 */
@Data
public class Box {

	private boolean isWallTop; // Is there a wall on the north of the box ?
	private boolean isWallRight;
	private boolean isWallBottom;
	private boolean isWallLeft;

	private int x, y;

	/**
	 * @see ColorRobotEnum
	 */
	private ColorRobotEnum color;
	/**
	 * @see BoxTypeEnum
	 */
	private BoxTypeEnum type;

	/**
	 * Default Constructor
	 */
	public Box() {
		this.isWallTop = false;
		this.isWallRight = false;
		this.isWallBottom = false;
		this.isWallLeft = false;
		this.type = BoxTypeEnum.EMPTY;
		this.color = null;
	}

	public Box(final BoxTypeEnum boxT, final ColorRobotEnum boxC) {
		this();
		this.type = boxT;
		this.color = boxC;
	}

	public Box(final boolean north, final boolean east, final boolean south, final boolean west,
			final ColorRobotEnum color, final BoxTypeEnum type) {
		this.isWallTop = north;
		this.isWallRight = east;
		this.isWallBottom = south;
		this.isWallLeft = west;
		this.color = color;
		this.type = type;
	}

	/**
	 * Put the wall according to the direction
	 *
	 * @param direction : north, south, west or east
	 * @see DirectionDeplacementEnum
	 */
	public void setWall(final DirectionDeplacementEnum dir) {
		switch (dir) {
		case UP:
			this.isWallTop = true;
			break;
		case DOWN:
			this.isWallBottom = true;
			break;
		case LEFT:
			this.isWallLeft = true;
			break;
		case RIGHT:
			this.isWallRight = true;
			break;
		default:

			break;
		}
	}

	/**
	 * To know if a robot can cross the box or if it is stopped by a wall
	 *
	 * @param direction the direction that the robot is following
	 * @return <code>true</code> if the robot can continue its course
	 */
	public boolean canContinue(final DirectionDeplacementEnum dir) {
		Boolean res;
		res = true;

		switch (dir) {
		case UP:
			res ^= this.isWallTop;
			break;
		case DOWN:
			res ^= this.isWallBottom;
			break;
		case LEFT:
			res ^= this.isWallLeft;
			break;
		case RIGHT:
			res ^= this.isWallRight;
			break;
		default:
			break;
		}
		return res;
	}

	public void setType(final BoxTypeEnum type, final ColorRobotEnum color) {
		this.type = type;
		this.color = color;
	}

	private ImageIcon getImageIcon(GameModel game) {
		ImageIcon icon;

		if (this.type == BoxTypeEnum.EMPTY || this.type == BoxTypeEnum.MULTI) {
			icon = new ImageIcon(Constant.THEME_PATH + "box/" + this.type + ".png");
		} else {
			icon = new ImageIcon(Constant.THEME_PATH + "box/" + this.type + this.color + ".png");
		}

		return icon;
	}

	public JPanel getJPanel(GameModel game, int i, int j) {
		Robot rCurrent = null, rOrigin = null;
		Box bObjectif = game.getCurrentGoal();

		for (Robot r : game.getRobotPositionList()) {
			if (r.getX() == j && r.getY() == i) {
				rCurrent = r;
			}
			if (r.getOriginX() == j && r.getOriginY() == i) {
				rOrigin = r;
			}
		}

		Boolean isSelected = false;

		if (rCurrent != null) {
			isSelected = rCurrent.getColor() == game.getSelectedRobot();
		}

		final JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());
		ImageIcon icon;

		if (rCurrent != null) {
			if (isSelected) {
				icon = new ImageIcon(Constant.IMAGE_PATH + "selection.png");
				panel.add(new JLabel("", icon, JLabel.CENTER));
			}
			icon = new ImageIcon(Constant.THEME_PATH + "robots/robot" + rCurrent.getColor() + ".png");
			panel.add(new JLabel("", icon, JLabel.CENTER));
		}

		if (this.isWallTop) {
			icon = new ImageIcon(Constant.IMAGE_PATH + "wall_north.png");
			JLabel north = new JLabel("", icon, JLabel.CENTER);
			panel.add(north);
		}

		if (this.isWallBottom) {
			icon = new ImageIcon(Constant.IMAGE_PATH + "wall_south.png");
			JLabel south = new JLabel("", icon, JLabel.CENTER);
			panel.add(south);
		}
		if (this.isWallRight) {
			icon = new ImageIcon(Constant.IMAGE_PATH + "wall_east.png");
			JLabel east = new JLabel("", icon, JLabel.CENTER);
			panel.add(east);

		}
		if (this.isWallLeft) {
			icon = new ImageIcon(Constant.IMAGE_PATH + "/wall_west.png");
			JLabel west = new JLabel("", icon, JLabel.CENTER);
			panel.add(west);
		}

		if (this.type == BoxTypeEnum.CENTRAL) {
			panel.add(new JLabel("", bObjectif.getImageIcon(game), JLabel.CENTER));
		}

		if (rOrigin != null) {
			icon = new ImageIcon(Constant.THEME_PATH + "box/Start" + rOrigin.getColor() + ".png");
			panel.add(new JLabel("", icon, JLabel.CENTER));
		}

		panel.add(new JLabel("", this.getImageIcon(game), JLabel.CENTER));

		return panel;
	}
}
