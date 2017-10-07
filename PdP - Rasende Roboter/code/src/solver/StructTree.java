package solver;

import game.Constant;
import game.Constant.Direction;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class StructTree {
	public Map<String, Integer> possibilities;
	public Map<String, String> parent;
	public Stack<String> solutionStack;
	public String solution;
	
	public StructTree() {
		possibilities = new HashMap<String, Integer>();
		parent = new HashMap<String, String>();
		solutionStack = new Stack<String>();
		solution = "";
	}
	
	public void clear() {
		possibilities.clear();
		parent.clear();
		solutionStack.clear();
		solution = "";
	}
	
	public boolean containsKey(String key) {
		return possibilities.containsKey(key);
	}
	
	public void addParent(String key, String keyParent){
		parent.put(key, keyParent);
	}
	
	public void addPossibility(String key, int depth) {
			possibilities.put(key, depth);	
	}
	
	public Map<String, Integer> getKeyFromValue(int depth) {
		Map<String, Integer> subMap = new HashMap<String, Integer>();
		for(String key : possibilities.keySet()) {
			if(possibilities.get(key) == depth){
				subMap.put(key, depth);
			}
		}
		
		return subMap;
	}
	
	public void buildStack(String leaf, String root) {
		
		String next;

		next = parent.get(leaf);
		solutionStack.push(next);
		
		while(!next.equals(root)) {
			next = parent.get(next);
			solutionStack.push(next);
		}
		
		String before = solutionStack.pop();
		String after;
		
		final int max = solutionStack.size();
		for(int i = 0; i < max; i++) {
			after = solutionStack.pop();
			solution += findDirection(before, after) + "\n";
			before = after;
		}
		
		System.out.println(solution);		
	}
	
	private String findDirection(String after, String before) {
		String[] nodeBefore;
		String[] nodeAfter;
		String[] robotInfoBefore;
		String[] robotInfoAfter;
		String res = "Move the ";
		Direction d = null;
		
		nodeBefore = before.split("&");
		nodeAfter = after.split("&");
		
		for(int i = 0; i < Constant.NB_ROBOT; i++) {
			if(!nodeBefore[i].equals(nodeAfter[i])) {
				robotInfoBefore = nodeBefore[i].split(";");
				robotInfoAfter = nodeAfter[i].split(";");
				
				res += Constant.Color.valueOf(robotInfoBefore[2]) + " robot in the ";
				
				if(Integer.parseInt(robotInfoBefore[0]) > Integer.parseInt(robotInfoAfter[0]))
						d = Direction.Right;
				else if(Integer.parseInt(robotInfoBefore[0]) < Integer.parseInt(robotInfoAfter[0]))
						d = Direction.Left;
				else if(Integer.parseInt(robotInfoBefore[1]) > Integer.parseInt(robotInfoAfter[1]))
						d = Direction.Down;
				else if(Integer.parseInt(robotInfoBefore[1]) < Integer.parseInt(robotInfoAfter[1]))
						d = Direction.Up;
			}
		}
		res += d + " direction.";
		return res;
	}
	
	public String getSolution(){
		return solution;
	}
}