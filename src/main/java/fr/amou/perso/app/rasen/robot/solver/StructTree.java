package fr.amou.perso.app.rasen.robot.solver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class StructTree {

	private Map<String, Integer> possibilities = new HashMap<>();
	private Map<String, String> parentMap = new HashMap<>();

	public Boolean containsKey(String key) {
		return this.possibilities.containsKey(key);
	}

	public void addParent(String key, String keyParent) {
		this.parentMap.put(key, keyParent);
	}

	public void addPossibility(String key, Integer depth) {
		this.possibilities.put(key, depth);
	}

	public List<String> getPositionInitialeProfondeur(Integer profondeur) {

		return this.possibilities.entrySet().stream().filter(entry -> entry.getValue().equals(profondeur))
				.map(Entry::getKey).collect(Collectors.toList());

	}

	public String getParent(String enfant) {
		return this.parentMap.get(enfant);
	}

}
