# g5 Pentocity Solution
Currently, our solution is a basic player that focuses only on placing residences and factories
and does not place any field or water. For road placement, we use a slightly modified version of
the findShortestRoad algorithm from the default Player. We are experimenting with two different
strategies with our basic Player, one called the "Spiral" strategy, which builds residences along
the outer edges of a spiral shape, and factories expanding outward from the center of the spiral.
Another strategy is the "Corner" strategy which builds residences and factories from diagonally
opposite corners toward the center. Both strategies separate residences and factories into their
own zones.

### Parks and Water
Our current strategy for parks and water involves extending the closest park or water if it is
within a two block radius of the building, or constructing a new 4-block of park or water
(alternating placement of park and water) that is adjacent to the building, with the bottom right
cell taking priority. We extend this new block of park or water (also termed "bonus group") in a
straight horizontal line (if possible) to maximize potential adjacency with future residences.

Using this strategy on top of our initial basic strategy increases our score by about 100.

## How to Run

    javac pentos/sim/Simulator.java
    javac pentos/g5/Player.java
    java pentos.sim.Simulator -g g5 --gui

On init, our player reads a config file called "player.cfg" from the working directory. If such
a file doesn't exist, it is created. You can replace one of the following line in that file:

    strategy=SPIRAL

or,

    strategy=CORNERS
