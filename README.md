# pentocity
Solving the Pentocity problem for COMS 4444.

## Collaborating on GitHub
Each of us can push our own code to our own branches (e.g. Stephanie would push to a 'stephanie' branch) and then create pull
requests to merge into master.

## Todo
* [x] If rejection due to road can't be connected, do something else
* [x] Complete the remaining edge, one loop before and one loop after the three loops
* [x] Discuss more hueristics for this problem
* [ ] Explore water / fields
* [ ] Abstract Buildings to Plots, these plots can be predefined
* [ ] Explore how Buildings can be fitted together well

## Commands
    javac pentos/sim/Simulator.java
    javac pentos/g5/Player.java
    java pentos.sim.Simulator -g g5 --gui

## Utilities
* BuildingUtil has all the code relevant to Buildings in it
* LandUtil has all the code relevant to Land in it
* Pentominos is a simple sequencer to generate [all the pentomino shapes](pentos/docs/pentominos.txt)
    javac pentos/g5/util/Pentominos.java
    java pentos.g5.util.Pentominos

