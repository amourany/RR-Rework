package fr.amou.perso.app.rasen.robot.solver;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fr.amou.perso.app.rasen.robot.enums.ColorRobotEnum;
import fr.amou.perso.app.rasen.robot.enums.DirectionDeplacementEnum;
import fr.amou.perso.app.rasen.robot.game.Board;
import fr.amou.perso.app.rasen.robot.game.Robot;
import fr.amou.perso.app.rasen.robot.game.data.GameModel;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Solver {

	private StructTree tree = new StructTree();

	private GameModel game;

	private String positionSolution = StringUtils.EMPTY;
	private Integer profondeur = 0;
	private Boolean solved = false;

	public Solver(GameModel game) {
		this.game = game;
	}

	public String solve() {
		List<Robot> robotList = this.game.getRobotPositionList();

		// Création de la configuration initiale
		String positionInitiale = this.encodeKey(robotList);
		this.tree.addPossibility(positionInitiale, this.profondeur);

		Instant debutRecherche = Instant.now();

		while (!this.solved) {
			this.profondeur++;
			this.buildPossibilities();
		}

		Instant finRecherche = Instant.now();
		Long tempsRecherche = Duration.between(debutRecherche, finRecherche).toMillis();
		log.debug("Solution trouvée - profondeur : {} - durée : {} ms", this.profondeur, tempsRecherche);

		StringBuilder solutionSB = new StringBuilder();

		solutionSB.append("Solution found, number of moves : " + (this.profondeur) + "\n");
		String etapes = this.buildStack(this.positionSolution, positionInitiale);
		solutionSB.append(etapes);

		return solutionSB.toString();
	}

	/**
	 * This function create all the possible moves
	 */
	private void buildPossibilities() {

		Board board = this.game.getBoard();

		List<String> positionInitialeList = this.tree.getPositionInitialeProfondeur(this.profondeur - 1);

		for (String positionInitiale : positionInitialeList) {

			List<Robot> robots = this.decodeKey(positionInitiale);

			for (Robot robot : robots) {
				for (DirectionDeplacementEnum direction : DirectionDeplacementEnum.values()) {
					List<Robot> robotTravailList = new ArrayList<>(robots);
					Robot robotTravail = new Robot(robot);

					Boolean hasMoved = board.getNewPosition(robotTravail, direction, robotTravailList);

					robotTravailList = robotTravailList.stream()
							.map(r -> r.getColor() == robotTravail.getColor() ? robotTravail : r)
							.collect(Collectors.toList());

					if (hasMoved) {

						String nouvellePosition = this.encodeKey(robotTravailList);
						Boolean estConfigurationExistante = this.tree.containsKey(nouvellePosition);

						Boolean win = this.game.isWin(robotTravail);
						if (win) {
							if (!estConfigurationExistante) {
								this.positionSolution = nouvellePosition;
								this.tree.addParent(nouvellePosition, positionInitiale);
								this.solved = true;
							}
						} else if (!estConfigurationExistante) {
							this.tree.addPossibility(nouvellePosition, this.profondeur);
							this.tree.addParent(nouvellePosition, positionInitiale);
						}

					}
				}
			}
		}
	}

	private String buildStack(String feuille, String racine) {

		Deque<String> solutionStack = new ArrayDeque<>();

		StringBuilder etapesSB = new StringBuilder();

		solutionStack.push(feuille);

		String next = this.tree.getParent(feuille);
		solutionStack.push(next);

		while (!StringUtils.equals(next, racine)) {
			next = this.tree.getParent(next);
			solutionStack.push(next);
		}

		String positionInitiale = solutionStack.pop();

		for (String positionDepilee : solutionStack) {
			String direction = this.findDirection(positionInitiale, positionDepilee);
			etapesSB.append(direction + "\n");
			positionInitiale = positionDepilee;
		}

		return etapesSB.toString();
	}

	private String findDirection(String after, String before) {

		String deplacement = StringUtils.EMPTY;

		List<Robot> posRobotAvantList = this.decodeKey(before);
		List<Robot> posRobotApresList = this.decodeKey(after);

		for (Robot posRobotAvant : posRobotAvantList) {
			Integer indexRobot = posRobotAvantList.indexOf(posRobotAvant);
			Robot posRobotApres = posRobotApresList.get(indexRobot);

			if (!posRobotAvant.equals(posRobotApres)) {
				ColorRobotEnum couleur = posRobotAvant.getColor();
				Integer xAvant = posRobotAvant.x;
				Integer yAvant = posRobotAvant.y;

				Integer xApres = posRobotApres.x;
				Integer yApres = posRobotApres.y;

				DirectionDeplacementEnum d = null;
				if (xAvant > xApres) {
					d = DirectionDeplacementEnum.RIGHT;
				} else if (xAvant < xApres) {
					d = DirectionDeplacementEnum.LEFT;
				} else if (yAvant > yApres) {
					d = DirectionDeplacementEnum.DOWN;
				} else if (yAvant < yApres) {
					d = DirectionDeplacementEnum.UP;
				}

				deplacement = "Robot : " + couleur + " - Direction : " + d;

			}
		}

		return deplacement;
	}

	private String encodeKey(List<Robot> robots) {
		return robots.stream().map(Robot::toString).collect(Collectors.joining("&"));
	}

	private List<Robot> decodeKey(String key) {
		List<String> keySplit = Arrays.asList(key.split("&"));

		return keySplit.stream().map(Solver::creerRobotDepuisClef).collect(Collectors.toList());
	}

	private static Robot creerRobotDepuisClef(String key) {
		String[] robotCaract = key.split(";");

		Integer x = Integer.parseInt(robotCaract[0]);
		Integer y = Integer.parseInt(robotCaract[1]);
		ColorRobotEnum couleur = ColorRobotEnum.valueOf(robotCaract[2]);

		return new Robot(x, y, couleur);
	}
}
