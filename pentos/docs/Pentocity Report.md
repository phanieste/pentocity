# COMS W4444 Pentocity Report
## **Group 5**: Stephanie Huang, Yogesh Garg, Avidan Hessing

## The Problem
You have bought a large tract of land, and are developing it for residential and industrial building. Traditional methods of land development would require you to subdivide the land into plots in advance, before seeing what kinds of plots people might want. In this project we'll try to see if we can do better by dynamically developing the tract of land in response to demand.

The land is modeled as a 50x50 grid of cells. A cell can contain one of several types of development, and several contiguous cells can be used to place larger structures. The main structural elements are:

Residences. These contain 5 contiguous cells, and are one of the 18 possible pentomino shapes (reflective symmetry doesn't apply here).
Factories. These contain up to 25 cells, and are always rectangular, with between 1 and 5 cells on each side.
Roads. Road cells must be adjacent to the perimeter of the development, or must be connected via a sequence of orthogonally adjacent road cells to the perimeter.
Water. A contiguous group of four or more water cells is considered a pond.
Park. A contiguous group of four or more park cells is considered a field.
At the start of the simulation, the development is a blank slate. A sequence of requests to build residences or factories (i.e., buildings) of particular shapes arrives one at a time -- you don't see ahead to future requests. Most of the time you will agree to build the building, and place it on the development somewhere. At all points in time, the development must satisfy the following constraints:

Every building must have at least one cell orthogonally adjacent to a road. This may mean that to place a building you may additionally need to place a number of additional road cells to meet this constraint. (Remember that road cells must connect with the perimeter.)
The footprint of the building must be previously empty. You are permitted to rotate the building in order to place it.
Factories and residences may not be immediately adjacent to one another. But factories can be adjacent to other factories, and residences to other residences.
A residence that is placed adjacent to a field achieves a bonus (see below) because the purchaser can see that the value of the property is higher. The bonus is achieved only if the field is present at (or before) the time the residence is placed. Adding a field later does not achieve a bonus for existing adjacent residences.
A residence that is placed adjacent to a pond achieves a bonus in the same way as a field (see below).
You score one point for each cell of a building that is placed. So residences always score 5 points, and factories score a variable number of points. Fields and ponds that are adjacent to a newly placed residence each yield a 2 point bonus for that residence. A residence that is adjacent to two fields (or two ponds) gets just one bonus, while a residence adjacent to a field and a pond gets two bonuses.

You have the option to reject a building request, but after three rejections, the simulation ends. Even if you do a good job of building placement, eventually you will run out of room and be forced to reject requests. Your goal is to have as many points as possible when the simulation ends.

The distribution of shapes in the request sequence is not necessarily uniform. In fact, it may be deliberately nonuniform, to test the robustness of your strategy. We'll provide a variety of sequence generators, and allow you to specify your own for experimentation. For the tournament at the end of the project, we'll use a variety of generators, some of which you will have seen, and some of which will be new.

Things to think about:

What kinds of placement give you the most flexibility for later buildings?
You get to choose the shapes of roads, fields and ponds. What are good shapes?
What does it take to achieve a ``robust'' strategy?
## Initial Implementations

### Approach by Group Member

- Stephanie

  Stephanie's initial implementation approach involved dividing the placement of residences and factory requests on the grid. Residence requests would begin their placements in the top left corner of the grid while factory requests begin their placements in the bottom right corner of the grid. (Insert here how do you pick the next placement?) The resulting strategy evolved so that with the initially included sequencer distribution, that the grid filled up in almost a diagonal fashion from both the top left and bottom right corners with an empty space cutting diagonally across the board from the top right corner to the bottom left corner of the grid, where factory placements created an unusuable strip of grid cells. Stephanie's initial player left out the placing of fields or ponds in an attempt to focus on residence and factory placement before throwing pond and field placements into the mix. (Need to include what road strategy Stephanie used for her initial player. I assume it was the included shortestRoad algorithm but need to verify with her.)

- Yogesh

  Yogesh's initial implementation approach was to have residence placements spiral from the perimeter of the grid, ever inwards, and factory placements start at the center of the grid with subsequent factory request placements spiraling outwards from the center. This involved a greedy choice of residence and factory placements that adhered to the respective spiraling shape he was trying to form; going clockwise from the middle of the left-side perimeter of the grid for residences and going counterclockwise from the center of the grid towards the perimeter for factories. This meant that Yogesh included a preallocated road going from the center of the left perimeter towards the center of the grid in order to ensure that factory as well as future residence placements on the interior of the grid were possible as the perimeter of the board filled up with placements comprising the first loop of residences of his spiraling strategy. (After the first preallocation of road extending from the perimeter to the interior of the grid, all subsequent road placements were made via the shortestRoad algorithm bundled in the project's skeleton code.) Yogesh's initial player prioritized exploring ideal placement of residences and factories and did not deal with pond or field placements.

- Avidan

  Avidan's initial implementation approach was to score each potential move for a building request according to a free-cells heuristic and choose the move with the highest score, choosing greedily from the moves that scored the same. The free-cell heuristic would count the number of free cells on the grid after the potential move placement along with its associated road placement as determined by the shortestRoad algorithm bundled in the project skeleton code. Avidan also did not consider pond and field placements when implementing his initial player. Avidan ran into issues in his attempt to create this initial player that prevented its successful creation. The first of which was the protected status of the Land class. This hindered his simulation of a move on an instance of the Land class for evaluation of the board according to the free-cell heuristic after the move in question. After implementing a workaround (that was sufficient for continued development on his approach though inadmissable for the project submission) his approach ran into the additional problem of attempting to run the heuristic on too large of a search space. Trying to run the heuristic on all possible admissable moves proved to take too much time and his initial player would time-out.
## Thread of Development
### Sep 12, Performance Analysis
### Development Direction
### Sep 19, Performance Analysis
### Development Direction
### Sep 26, Performance Analysis
### Development Direction
### Oct 3, Performance Analysis
### Development Direction
### Oct 10, Performance Analysis
### Development Direction
## Testing & Results
