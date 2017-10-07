package solver;

import game.Constant;
import game.Constant.Direction;
import game.Game;
import game.Robot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solver {
	
	private StructTree tree;
	private String root;
	private String leaf;
	private Game game;
	private int depth;
	private int cut;
	private boolean solved;
	
	public Solver() {
		tree = new StructTree();
	}
	
	public void initSolver(Game g) {
		root = "";
		leaf = "";
		game = g;
		tree.clear();
		depth = 0;
		cut = 0;
		solved = false;
	}
	
	public void solve(Game g){
		initSolver(g);
		createRoot();
		
		while(!solved && depth < 13){
			depth++;
			solved = buildPossibilities();
		}
		
		if(solved){
			System.out.println("Solution found, number of moves : " + (depth-1));
			tree.buildStack(leaf, root);
		}
		else {
			System.out.println("No solutions found, try to do some moves and try again");
		}
	}
	
	public void createRoot() {
		root = encodeKey(game.getRobots());
		tree.addPossibility(root, depth);
	}
	
	/**
	 * This function create all the possible moves
	 */
	private boolean buildPossibilities() {
		boolean moved;
		String toAdd;
		List<Robot> robots = new ArrayList<Robot>();
		List<Robot> copyRobot = new ArrayList<Robot>();
		Map<String, Integer> copyTree = new HashMap<String, Integer>();
		
		copyTree = tree.getKeyFromValue(depth-1);
		
		for(String s : copyTree.keySet()) {
				
			robots = decodeKey(s);
			
			for(Robot r : robots) {
				for(Direction d : Constant.Direction.values()) { // Then for each robot we move them in all directions
					copyRobot.clear();
					copyRobot.addAll(robots);
					Robot rCopy = new Robot(r);
					
					moved = game.getBoard().getNewPosition(rCopy, d, copyRobot);
					
					for(int i = 0; i < copyRobot.size(); i++) {
			            if(copyRobot.get(i).getColor() == rCopy.getColor()) {
			            	copyRobot.set(i, rCopy);
			            }
					}
					if(moved) { // And then we add the key made by the move of each robot to the hashmap
						if(!solved) {
							if(game.isWin(r)){ 
								toAdd = encodeKey(copyRobot);
								if(!tree.containsKey(toAdd)) {
									leaf = toAdd;
									tree.addParent(toAdd, s);
									solved = true;
								}
							}
							else {
								toAdd = encodeKey(copyRobot);
								if(!tree.containsKey(toAdd)){
									tree.addPossibility(toAdd, depth);
									tree.addParent(toAdd, s);
								}
								else
									cut++;
							}
						}
					}
				}
			}
		}
		return solved;
	}
	
	private String encodeKey(List<Robot> robots) {
		String s = "";
		for(Robot r : robots) {
			s += r.toString() + "&";
		}
		return s;
	}
	
	private List<Robot> decodeKey(String key) {
		String subKey[];
		String robotInfo[];
		Robot rob;
		List<Robot> robots = new ArrayList<Robot>();
		
		subKey = key.split("&");
		
		for(int i = 0; i < Constant.NB_ROBOT; i++){
			robotInfo = subKey[i].split(";"); // We split the keys in order to get the four robots
			rob = new Robot(Integer.parseInt(robotInfo[0]), Integer.parseInt(robotInfo[1]), Constant.Color.valueOf(robotInfo[2]));
			robots.add(rob);
		}
		
		return robots;
	}
}
