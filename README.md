# MAZE RUNNER

The rules of the correct maze:

`There should be walls around the maze, except for two cells: entrance and exit.
Any empty cell must be accessible from the entrance or exit of the maze. It is not possible to walk along the maze diagonally, only vertically and horizontally.
There's got to be a path from the entrance to the exit. It doesn't matter what is considered an entrance and what is an exit as they are interchangeable.
The maze should not contain 3x3 blocks consisting of walls only. Try to fill the entire maze area with pathways.`

The algorithm I used for building a maze, based on the construction of a minimal spanning tree. You can [watch the video](https://www.youtube.com/watch?v=KurrmC-sd6k) on how this algorithm works.

The program provides the following options: 

`1. Generate a new maze.`

`2. Load a maze.`

`3. Save the maze.`

`4. Display the maze.`

`5. Find the escape.`

`0. Exit.`

Example

After starting, the program prints a menu listing only appropriate options. When a user has chosen an option, the program performs the corresponding action.

The data about the escape path is not saved anywhere, and it does not display the escape path when the user chooses the fourth option. The escape path is marked with '/' symbol.
````
`=== Menu ===`

`1.Generate a new maze`

`2.Load a maze`

`0.Exit`

`>2`

`>maze.txt`

````

````
`=== Menu ===`

`1. Generate a new maze`

`2. Load a maze`

`3. Save the maze`

`4. Display the maze`

`5. Find the escape`

`0. Exit`

`>4`
██████████████████████████████████████████████████  ██████████
██      ██                          ██  ██          ██  ██  ██
██  ██████████████  ██████████████████  ██  ██████████  ██  ██
██  ██                          ██      ██  ██          ██  ██
██  ██████████████████  ██████████  ██████  ██████████  ██  ██
██  ██  ██  ██  ██  ██  ██  ██              ██  ██          ██
██  ██  ██  ██  ██  ██  ██  ██  ██████████████  ██████  ██  ██
██  ██  ██          ██  ██              ██      ██      ██  ██
██  ██  ██████  ██████  ██  ██████████████  ██████████  ██████
██  ██      ██          ██              ██      ██      ██  ██
██  ██  ██████  ██████  ██████████  ██████  ██████  ██████  ██
██  ██  ██  ██  ██  ██  ██                  ██          ██  ██
██  ██  ██  ██████  ██  ██████████  ██████████  ██  ██████  ██
██              ██  ██      ██  ██          ██  ██  ██  ██  ██
██████  ██  ██████  ██████  ██  ██  ██████████  ██████  ██  ██
██      ██  ██  ██  ██  ██      ██  ██      ██  ██          ██
██████  ██████  ██  ██  ██  ██████  ██  ██████  ██████  ██████
██          ██  ██      ██      ██          ██          ██  ██
██  ██████  ██  ██████  ██  ██████████  ██████  ██████████  ██
██  ██      ██          ██  ██          ██      ██      ██  ██
██████████  ██████████  ██  ██  ██████████  ██████  ██████  ██
██                  ██          ██  ██      ██  ██      ██  ██
██████████████████  ██████  ██████  ██████  ██  ██  ██████  ██
██                          ██      ██  ██                  ██
██████████  ██████████████  ██  ██████  ██  ██████████████  ██
██          ██  ██      ██  ██  ██  ██              ██  ██  ██
██████  ██████  ██████  ██  ██  ██  ██  ██████████████  ██████
██      ██      ██                      ██  ██              ██
██████████████  ██████████████  ██████  ██  ██████  ██  ██████
██                                  ██              ██      ██
██████████████████████████  ██████████████████████████████████


````
````
`=== Menu ===`

`1. Generate a new maze`

`2. Load a maze`

`3. Save the maze`

`4. Display the maze`

`5. Find the escape`

`0. Exit`

`>5`
██████████████████████████████████████████████████//██████████
██      ██                          ██  ██//////////██  ██  ██
██  ██████████████  ██████████████████  ██//██████████  ██  ██
██  ██                          ██      ██//██          ██  ██
██  ██████████████████  ██████████  ██████//██████████  ██  ██
██  ██  ██  ██  ██  ██  ██  ██//////////////██  ██          ██
██  ██  ██  ██  ██  ██  ██  ██//██████████████  ██████  ██  ██
██  ██  ██          ██  ██//////        ██      ██      ██  ██
██  ██  ██████  ██████  ██//██████████████  ██████████  ██████
██  ██      ██          ██//////////    ██      ██      ██  ██
██  ██  ██████  ██████  ██████████//██████  ██████  ██████  ██
██  ██  ██  ██  ██  ██  ██        //        ██          ██  ██
██  ██  ██  ██████  ██  ██████████//██████████  ██  ██████  ██
██              ██  ██      ██  ██//        ██  ██  ██  ██  ██
██████  ██  ██████  ██████  ██  ██//██████████  ██████  ██  ██
██      ██  ██  ██  ██  ██      ██//██      ██  ██          ██
██████  ██████  ██  ██  ██  ██████//██  ██████  ██████  ██████
██          ██  ██      ██      ██//////    ██          ██  ██
██  ██████  ██  ██████  ██  ██████████//██████  ██████████  ██
██  ██      ██          ██  ██//////////██      ██      ██  ██
██████████  ██████████  ██  ██//██████████  ██████  ██████  ██
██                  ██    //////██  ██      ██  ██      ██  ██
██████████████████  ██████//██████  ██████  ██  ██  ██████  ██
██                        //██      ██  ██                  ██
██████████  ██████████████//██  ██████  ██  ██████████████  ██
██          ██  ██      ██//██  ██  ██              ██  ██  ██
██████  ██████  ██████  ██//██  ██  ██  ██████████████  ██████
██      ██      ██        //////        ██  ██              ██
██████████████  ██████████████//██████  ██  ██████  ██  ██████
██                        //////    ██              ██      ██
██████████████████████████//██████████████████████████████████

````
````

`=== Menu ===`

`1. Generate a new maze`

`2. Load a maze`

`3. Save the maze`

`4. Display the maze`

`5. Find the escape`

`0. Exit`

`>0`

`Bye!`
