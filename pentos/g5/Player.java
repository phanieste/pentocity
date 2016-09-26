package pentos.g5;

import pentos.sim.Cell;
import pentos.sim.Building;
import pentos.sim.Land;
import pentos.sim.Move;
import pentos.sim.LandBuilder;

import java.util.*;

public class Player implements pentos.sim.Player {

	private int turn;	/* keeps track of the turn played */
	private Random gen;
	/* invalid cell count of the land grid prior before move execution */
	private int invalidCellCtPtM;
	private Set<Cell> road_cells = new HashSet<Cell>();
	private Land land_in_play;

	/* function is called once at the beginning before play is called */
	public void init() {
		gen = new Random();
	}

	public Move play(Building request, Land land) {
		/* set invalid cell count for the current land configuration */
		invalidCellCtPtM = invalidCellCount(land);
		land_in_play = land;

		/* find all valid building locations and orientations */
		ArrayList<Move> moves = new ArrayList<Move>();
		for (int i = 0; i < land.side; i++) {
			for (int j = 0; j < land.side; j++) {
				Cell p = new Cell(i, j);
				Building[] rotations = request.rotations();
				for (int ri = 0; ri < rotations.length; ri++) {
					Building b = rotations[ri];
					if (land.buildable(b, p))
						moves.add(new Move(true, request, p, ri,
							new HashSet<Cell>(), new HashSet<Cell>(),
								new HashSet<Cell>()));
				}
			}
		}

		/* for debugging */
		int nmbr_of_moves = moves.size();
		System.out.printf("Turn %d: %d possible moves.\n", turn, nmbr_of_moves);
		turn++;

		/* add necessary road placement to each move in valid set of moves */
		for (Move current : moves) {
			/*
			 * get coordinates of building placement (position plus local
			 * building cell coordinates)
			 */
			Set<Cell> shiftedCells = new HashSet<Cell>();
			for (Cell x : current.request.rotations()[current.rotation])
				shiftedCells.add(new Cell(x.i + current.location.i, x.j + current.location.j));

			/* build a road to connect this building to perimeter */
			Set<Cell> roadCells = findShortestRoadAlt(shiftedCells, land);
			if (roadCells != null) {
				current.road = roadCells;
			}
		}

		if (moves.isEmpty()) { /* reject if no valid placements */
			return new Move(false);
		} else {
			Move chosen = chooseMove(moves);
			road_cells.addAll(chosen.road);
			return chosen;
		}

		// /* choose a building placement at random */
		// if (moves.isEmpty()) /* reject if no valid placements */
		// return new Move(false);
		// else {
		// Move chosen = moves.get(gen.nextInt(moves.size()));
		// /*
		// * get coordinates of building placement (position plus local
		// * building cell coordinates)
		// */
		// Set<Cell> shiftedCells = new HashSet<Cell>();
		// for (Cell x : chosen.request.rotations()[chosen.rotation])
		// shiftedCells.add(new Cell(x.i + chosen.location.i, x.j +
		// chosen.location.j));

		// /* build a road to connect this building to perimeter */
		// Set<Cell> roadCells = findShortestRoad(shiftedCells, land);
		// if (roadCells != null) {
		// chosen.road = roadCells;
		// road_cells.addAll(roadCells);

		// /*
		// * for residences, build random ponds and fields connected to it
		// */
		// if (request.type == Building.Type.RESIDENCE) {
		// Set<Cell> markedForConstruction = new HashSet<Cell>();
		// markedForConstruction.addAll(roadCells);
		// chosen.water = randomWalk(shiftedCells, markedForConstruction, land,
		// 4);
		// markedForConstruction.addAll(chosen.water);
		// chosen.park = randomWalk(shiftedCells, markedForConstruction, land,
		// 4);
		// }
		// return chosen;
		// } else /* reject placement if building can't be connected by road */
		// return new Move(false);
		// }
	}

	private Move chooseMove(ArrayList<Move> moves) {
		Move chosenMove = evaluateMoves(moves);
		return chosenMove;
	}

	private Move evaluateMoves(ArrayList<Move> moves) {
		/*
		 * for each move, simulate what the Land grid would look like after that
		 * move had been executed; that move's score is equal to the # of free
		 * cells in the grid minus any empty cells that have become invalid
		 * building placements due to that placement
		 */
		Map<Integer, LinkedList<Move> > movesScores = new HashMap<Integer, LinkedList<Move> >();
		LandBuilder afterMove = null;
		int moveNmbr = 0;	/* for debugging */
		for (Move current : moves) {
			/* for debugging */
			// System.out.printf("Evaluating move #: %d\n", ++moveNmbr);

			afterMove = landAfterBuild(current, current.request);

			// /* for debugging */
			// System.out.printf(afterMove.toString());

			if (afterMove != null) {
				int score = freeCellsScore(afterMove);
				int postBldInvldCt = invalidCellCount(afterMove);
				int invalidCount = postBldInvldCt - invalidCellCtPtM;
				score -= invalidCount;

				LinkedList<Move> moveList = movesScores.get(score);
				if (moveList == null) {
					moveList = new LinkedList<Move>();
				}
				moveList.add(current);
				movesScores.put(score, moveList);
			}
		}
		ArrayList<Integer> scores = new ArrayList<Integer>();
		Set<Integer> scoresSet = movesScores.keySet();
		for (int key : scoresSet) {
			scores.add(key);
		}
		Collections.sort(scores);
		Collections.reverse(scores); // order scores from highest to lowest
		int moveChoice = scores.get(0); // first number; i.e. the highest score

		/* debugging print statements */
		ListIterator<Integer> iter = scores.listIterator();
		while (iter.hasNext()) {
			System.out.print(iter.next() + " ");
		}
		System.out.println("scores list end");

		LinkedList<Move> choiceMoves = movesScores.get(moveChoice);
		Move chosen = choiceMoves.peek(); // take the first move off the list

		return chosen;
	}

	/*
	 * returns a land after a move has been executed, i.e. a building has been
	 * built on the land. method copied almost word for word from the Simulator
	 * class
	 */
	private LandBuilder landAfterBuild(Move move, Building request) {
		LandBuilder land = new LandBuilder(land_in_play.side);
		land.copy(land_in_play);

		/* for debugging */
		// System.out.println("Before move -");
		// System.out.printf(land.toString());

		Building[] building_rotations = request.rotations();
		Building rotated_building = building_rotations[move.rotation];
		// play move. First build auxiliary structures.
		Iterator<Cell> water_cells = move.water.iterator();
		Iterator<Cell> park_cells = move.park.iterator();
		Iterator<Cell> rd_cells = move.road.iterator();
		String roadCells = "";
		while (water_cells.hasNext())
			land.buildWater(water_cells.next());
		while (park_cells.hasNext())
			land.buildPark(park_cells.next());
		while (rd_cells.hasNext()) {
			Cell x = rd_cells.next();
			roadCells = roadCells + " " + x.i + "," + x.j;
		}
		rd_cells = move.road.iterator();
		while (rd_cells.hasNext())
			land.buildRoad(rd_cells.next());
		if (!land.validateRoads())
			throw new RuntimeException("Roads not connected");
		String buildingCells = "";
		for (Cell p : rotated_building)
			buildingCells = buildingCells + " (" + (p.i + move.location.i) + "," + (p.j + move.location.j) + ")";
		int delta = land.build(rotated_building, move.location);

		/* for debugging */
		// System.out.println("After move -");
		// System.out.printf(land.toString());
		// LandBuilder landCheck = new LandBuilder(land_in_play.side);
		// landCheck.copy(land_in_play);
		// System.out.println("land_in_play");
		// System.out.printf(landCheck.toString());

		if (delta == -1) {
			// throw new RuntimeException("Invalid building placement");
			return null;
		}
		return land;
	}

	/* total number of unoccupied cells on the land grid */
	private int freeCellsScore(Land land) {
		int freeCellCount = 0;
		for (int i = 0; i < land.side; i++) {
			for (int j = 0; j < land.side; j++) {
				if (land.unoccupied(i, j)) {
					freeCellCount++;
				}
			}
		}
		return freeCellCount;
	}

	private int invalidCellCount(Land land) {
		int count = 0;
		Set<Cell> unoccupieds = new HashSet<Cell>();
		for (int i = 0; i < land.side; i++) {
			for (int j = 0; j < land.side; j++) {
				if (land.unoccupied(i, j)) {
					unoccupieds.add(new Cell(i, j));
				}
			}
		}

		/*
		 * check to see if 5 contiguous empty cells, i.e. can fit a residence
		 * there.
		 * needs editing to remove unnecessary building of 'residence space'.
		 */
		for (Cell emptyCell : unoccupieds) {
			Set<Cell> residenceSpace = new HashSet<Cell>();
			Cell tail = emptyCell;
			residenceSpace.add(tail);
			ArrayList<Cell> walk_cells = new ArrayList<Cell>();
			int i = 0;
			while (i < 4) {
				for (Cell p : tail.neighbors()) {
					if (p.isEmpty() && !residenceSpace.contains(p))
						walk_cells.add(p);
					i++;
				}
				if (i < 4 && walk_cells.isEmpty()) {
					count++;
					break;
				}
				tail = walk_cells.remove(0); // first cell on the list
				residenceSpace.add(tail);
			}
			walk_cells.clear();
		}
		return count;
	}

	/* build shortest sequence of road cells to connect to a set of cells b */
	private Set<Cell> findShortestRoad(Set<Cell> b, Land land) {
		Set<Cell> output = new HashSet<Cell>();
		boolean[][] checked = new boolean[land.side][land.side];
		Queue<Cell> queue = new LinkedList<Cell>();

		/* add border cells that don't have a road currently */
		Cell source = new Cell(Integer.MAX_VALUE, Integer.MAX_VALUE);
		/* dummy cell to serve as road connector to perimeter cells */

		for (int z = 0; z < land.side; z++) {
			/* if already on border don't build any roads */
			if (b.contains(new Cell(0, z)) || b.contains(new Cell(z, 0)) || b.contains(new Cell(land.side - 1, z))
							|| b.contains(new Cell(z, land.side - 1)))
				return output;
			if (land.unoccupied(0, z))
				queue.add(new Cell(0, z, source));
			if (land.unoccupied(z, 0))
				queue.add(new Cell(z, 0, source));
			if (land.unoccupied(z, land.side - 1))
				queue.add(new Cell(z, land.side - 1, source));
			if (land.unoccupied(land.side - 1, z))
				queue.add(new Cell(land.side - 1, z, source));
		}

		/* add cells adjacent to current road cells */
		for (Cell p : road_cells) {
			for (Cell q : p.neighbors()) {

				/*
				 * use tail field of cell to keep track of previous road cell
				 * during the search
				 */
				if (!road_cells.contains(q) && land.unoccupied(q) && !b.contains(q))
					queue.add(new Cell(q.i, q.j, p));
			}
		}
		while (!queue.isEmpty()) {
			Cell p = queue.remove();
			checked[p.i][p.j] = true;
			for (Cell x : p.neighbors()) {

				/* trace back through search tree to find path */
				if (b.contains(x)) {
					Cell tail = p;
					while (!b.contains(tail) && !road_cells.contains(tail) && !tail.equals(source)) {
						output.add(new Cell(tail.i, tail.j));
						tail = tail.previous;
					}
					if (!output.isEmpty())
						return output;
				} else if (!checked[x.i][x.j] && land.unoccupied(x.i, x.j)) {
					x.previous = p;
					queue.add(x);
				}
			}
		}
		if (output.isEmpty() && queue.isEmpty())
			return null;
		else
			return output;
	}

	/*
	 * walk n consecutive cells starting from a building. Used to build a random
	 * field or pond.
	 */
	private Set<Cell> randomWalk(Set<Cell> b, Set<Cell> marked, Land land, int n) {
		ArrayList<Cell> adjCells = new ArrayList<Cell>();
		Set<Cell> output = new HashSet<Cell>();
		for (Cell p : b) {
			for (Cell q : p.neighbors()) {
				if (land.isField(q) || land.isPond(q))
					return new HashSet<Cell>();
				if (!b.contains(q) && !marked.contains(q) && land.unoccupied(q))
					adjCells.add(q);
			}
		}
		if (adjCells.isEmpty())
			return new HashSet<Cell>();
		Cell tail = adjCells.get(gen.nextInt(adjCells.size()));
		for (int ii = 0; ii < n; ii++) {
			ArrayList<Cell> walk_cells = new ArrayList<Cell>();
			for (Cell p : tail.neighbors()) {
				if (!b.contains(p) && !marked.contains(p) && land.unoccupied(p) && !output.contains(p))
					walk_cells.add(p);
			}
			if (walk_cells.isEmpty()) {
				/* return output; if you want to build it anyway */
				return new HashSet<Cell>();
			}
			output.add(tail);
			tail = walk_cells.get(gen.nextInt(walk_cells.size()));
		}
		return output;
	}

	/* Everything below this point is from Stephanie's player commit */
	private Set<Cell> findShortestRoadAlt(Set<Cell> b, Land land) {
		// System.out.println("findShortestRoad");
		Set<Cell> output = new HashSet<Cell>();
		boolean[][] checked = new boolean[land.side][land.side];
		Queue<Cell> queue = new LinkedList<Cell>();

		for (Cell p : b) {
			if (isOnPerimeter(p,land))
				return output;
			for (Cell q : p.neighbors()) {
				if (road_cells.contains(q))
					return output;
				if (land.unoccupied(q.i,q.j)) {
					q.previous = p;
					queue.add(q);
				}
			}
		}

		while (!queue.isEmpty()) {
			Cell p = queue.remove();
			if (checked[p.i][p.j])
				continue;
			checked[p.i][p.j] = true;
			if (isOnPerimeter(p,land)) {
				Cell tail = p;
				output.add(new Cell(p.i,p.j));
				while (!b.contains(tail)) {
					output.add(new Cell(tail.i,tail.j));
					tail = tail.previous;
				}
				if (!output.isEmpty())
					return output;
			}
			else {
				for (Cell x : p.neighbors()) {
					if (road_cells.contains(x)) {
						Cell tail = p;
						output.add(new Cell(p.i,p.j));
						while (!b.contains(tail)) {
							output.add(new Cell(tail.i,tail.j));
							tail = tail.previous;
						}
						if (!output.isEmpty())
							return output;
					}
					else if (!checked[x.i][x.j] && land.unoccupied(x.i,x.j)) {
						x.previous = p;
						queue.add(x);
					}
				}
			}
		}
		if (output.isEmpty() && queue.isEmpty()) {
			return null;
		}
		else
			return output;
	}

	// check if cell is on perimeter
	private boolean isOnPerimeter(Cell c, Land land) {
		return (c.i == 0 || c.j == 0 || c.i == land.side-1 || c.j == land.side-1);
	}
}
