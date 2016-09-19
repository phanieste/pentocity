# g5 Pentocity Solution
Currently, our solution is a basic player that focuses only on placing residences and factories
and does not place any field or water. For road placement, we use a slightly modified version of
the findShortestRoad algorithm from the default Player. We are experimenting with two different
strategies with our basic Player, one called the "cup" strategy, which builds residences along
the outer edges of a spiral shape, and factories expanding outward from the center of the spiral.
Another strategy is the "diag" strategy which builds toward the center along a diagonal from two
corners. Both strategies separate residences and factories into their own zones. Currently, the
`cupStrategy` variable in the `Player` class can be set to either `true` or `false` to use either
the cup or diag strategy, respectively.
