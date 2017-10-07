package game;

import game.Constant.BoxType;
import game.Constant.Color;
import game.Constant.Direction;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;


/**
 * Class containing the data of a box (walls, color and type)
 */
public class Box {

	private boolean north; // Is there a wall on the north of the box ?
	private boolean east;
	private boolean south;
	private boolean west;
	
	private int x, y;
	
	/**
	 * @see Color
	 */
	private Color color;
	/**
	 * @see BoxType
	 */
	private BoxType type;

	/**
	 * Default Constructor
	 */
	public Box() {
		north=false;
		south=false;
		east=false;
		west=false;
		type = BoxType.Empty;
		color = null;
	}
	
	public Box(final BoxType boxT, final Color boxC){
		this();
		type = boxT;
		color = boxC;
	}

	public Box(final boolean north, final boolean east, final boolean south, final boolean west,
			final Color color, final BoxType type) {
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;
		this.color = color;
		this.type = type;
	}

	public boolean isNorth() {
		return north;
	}

	public void setNorth(final boolean north) {
		this.north = north;
	}

	public boolean isEast() {
		return east;
	}

	public void setEast(final boolean east) {
		this.east = east;
	}

	public boolean isSouth() {
		return south;
	}

	/**
	 * Put the wall according to the direction
	 * @param direction : north, south, west or east
	 * @see Direction
	 */
	public void setWall(final Direction dir){
		switch (dir) {
		case Up:
			north = true;
			break;
		case Down:
			south = true;
			break;
		case Left:
			west = true;
			break;
		case Right:
			east = true;
			break;
		default:

			break;
		}
	}


	/**
	 * To know if a robot can cross the box or if it is stopped by a wall
	 * @param direction the direction that the robot is following
	 * @return <code>true</code> if the robot can continue its course
	 */
	public boolean canContinue(final Direction dir){
		Boolean res;
		res = true;
		
		switch (dir) {
		case Up:
			res ^= north;
			break;
		case Down:
			res ^= south;
			break;
		case Left:
			res ^= west;
			break;
		case Right:
			res ^= east;
			break;
		default:
			break;
		}
		return res;
	}

	public void setSouth(final boolean south) {
		this.south = south;
	}

	public boolean isWest() {
		return west;
	}

	public void setWest(final boolean west) {
		this.west = west;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(final Color color) {
		this.color = color;
	}

	public BoxType getType() {
		return type;
	}

	public void setType(final BoxType type, final Color color) {
		this.type = type;
		this.color = color;
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

	private ImageIcon getImageIcon(Game game){
		ImageIcon icon;
		
		if(type == BoxType.Empty || type == BoxType.Multi) {
			icon = new ImageIcon(Constant.THEME_PATH + game.getTheme()+ "box/" + type + ".png");
		}
		else {
			icon = new ImageIcon(Constant.THEME_PATH + game.getTheme()+ "box/" + type + color + ".png");
		}
		
		return icon;
	}
	
	public JPanel getJPanel(Game game, int i, int j){
		Robot rCurrent = null, rOrigin = null;
		Box bObjectif = game.getCurrentGoal();
		
		for(Robot r : game.getRobots()){
			if(r.getX() == j && r.getY() == i){
				rCurrent = r;
			}
			if(r.getOriginX() == j && r.getOriginY() == i){
				rOrigin = r;
			}
		}		
		
		Boolean isSelected = (rCurrent == game.getSelectedRobot());
		
		final JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());
		ImageIcon icon;

		if(rCurrent != null) {
			if(isSelected){
				icon = new ImageIcon(Constant.IMAGE_PATH + "selection.png");
				panel.add(new JLabel("", icon, JLabel.CENTER));
			}
			icon = new ImageIcon(Constant.THEME_PATH + game.getTheme() + "robots/robot" + rCurrent.getColor() + ".png");
			panel.add(new JLabel("", icon, JLabel.CENTER));
		}
		
		if(isNorth()){
			icon = new ImageIcon(Constant.IMAGE_PATH + "wall_north.png");
			JLabel north = new JLabel("", icon, JLabel.CENTER);
			panel.add(north);
		}

		if(isSouth()){
			icon = new ImageIcon(Constant.IMAGE_PATH + "wall_south.png");
			JLabel south =  new JLabel("", icon, JLabel.CENTER);
			panel.add(south);
		}
		if(isEast()){
			icon = new ImageIcon(Constant.IMAGE_PATH + "wall_east.png");
			JLabel east =  new JLabel("", icon, JLabel.CENTER);
			panel.add(east);

		}
		if(isWest()){
			icon = new ImageIcon(Constant.IMAGE_PATH + "/wall_west.png");
			JLabel west = new JLabel("", icon, JLabel.CENTER);
			panel.add(west);
		}

		if(this.type == BoxType.Central){
			panel.add(new JLabel("", bObjectif.getImageIcon(game), JLabel.CENTER));
		}

		if(rOrigin != null){
			icon = new ImageIcon(Constant.THEME_PATH + game.getTheme() + "box/Start" + rOrigin.getColor() + ".png");
			panel.add(new JLabel("", icon, JLabel.CENTER));
		}

		panel.add(new JLabel("", getImageIcon(game), JLabel.CENTER));
		
		return panel;
	}
}