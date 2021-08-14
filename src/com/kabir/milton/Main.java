//package maze;
package com.kabir.milton;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static com.kabir.milton.Cell.Type.PASSAGE;
import static com.kabir.milton.Cell.Type.WALL;
import static java.lang.Integer.parseInt;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

class Maze {
    private final int height;
    private final int width;
    private boolean isSolved = false;
    private final Cell[][] grid;

    public Maze(int height, int width) {
        this.height = height;
        this.width = width;
        grid = new Cell[height][width];
        fillGrid();
    }

    private Maze(int height, int width, Cell[][] grid) {
        this.height = height;
        this.width = width;
        this.grid = grid;
    }

    public Maze(int size) {
        this(size, size);
    }

    private static Cell.Type intToType(int val) {
        return val == 1 ? WALL : PASSAGE;
    }

    public static Maze load(String str) {
        try {
            var whole = str.split("\n");
            var size = whole[0].split(" ");
            var height = parseInt(size[0]);
            var width = parseInt(size[1]);
            var grid = new Cell[height][width];
            for (int i = 0; i < height; i++) {
                var row = whole[i + 1].split(" ");
                for (int j = 0; j < width; j++)
                    grid[i][j] = new Cell(
                            i, j, intToType(parseInt(row[j]))
                    );
            }
            return new Maze(height, width, grid);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Cannot load the maze. " +
                            "It has an invalid format"
            );
        }
    }

    public String export() {
        var sb = new StringBuilder();
        sb.append(height).append(' ')
                .append(width).append('\n');
        for (var row : grid) {
            for (var cell : row)
                sb.append(typeToInt(cell))
                        .append(' ');
            sb.append('\n');
        }
        return sb.toString();
    }

    private int typeToInt(Cell cell) {
        return cell.isWall() ? 1 : 0;
    }

    private void fillGrid() {
        fillAlternately();
        fillGaps();
        makeEntranceAndExit();
        generatePassages();
    }

    private void putCell(int row, int column, Cell.Type type) {
        grid[row][column] = new Cell(row, column, type);
    }

    private void fillAlternately() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((i & 1) == 0 || (j & 1) == 0) {
                    putCell(i, j, WALL);
                } else {
                    putCell(i, j, PASSAGE);
                }
            }
        }
    }

    private void fillGaps() {
        if (height % 2 == 0) wallLastRow();
        if (width % 2 == 0) wallLastColumn();
    }

    private void wallLastColumn() {
        for (int i = 0; i < height; i++)
            putCell(i, width - 1, WALL);
    }

    private void wallLastRow() {
        for (int i = 0; i < width; i++)
            putCell(height - 1, i, WALL);
    }

    private int getExitColumn() {
        return width - 3 + width % 2;
    }

    private void makeEntranceAndExit() {
        putCell(0, 1, PASSAGE);
        putCell(height - 1, getExitColumn(), PASSAGE);
        if (height % 2 == 0)
            putCell(height - 2, getExitColumn(), PASSAGE);
    }

    private void generatePassages() {
        new PassageTree(height, width)
                .generate()
                .forEach(putCell());
    }

    private Consumer<Cell> putCell() {
        return cell -> grid[cell.getRow()][cell.getColumn()] = cell;
    }

    private String toString(boolean showEscape) {
        var sb = new StringBuilder();
        for (var row : grid) {
            for (var cell : row) {
                if (cell.isWall()) {
                    sb.append("██");
                } else if (showEscape && cell.isEscape()) {
                    sb.append("//");
                } else {
                    sb.append("  ");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String findEscape() {
        if (!isSolved) {
            new Fugitive(grid, getEntrance(), getExit())
                    .findEscape()
                    .forEach(putCell());
            isSolved = true;
        }
        return toString(true);
    }

    private Cell getEntrance() {
        return grid[0][1];
    }

    private Cell getExit() {
        return grid[height - 1][getExitColumn()];
    }


}

class Fugitive {
    private static final int[][] DELTAS = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};
    private final int height;
    private final int width;
    private final Node[][] grid;
    private final Node start;
    private final Node end;
    private final PriorityQueue<Node> open = new PriorityQueue<>(comparingInt(Node::getFinalCost));
    private final Set<Node> closed = new HashSet<>();

    public Fugitive(Cell[][] grid, Cell start, Cell end) {
        this.height = grid.length;
        this.width = grid[0].length;
        this.grid = new Node[height][width];
        this.start = new Node(start.getRow(), start.getColumn(), false);
        this.end = new Node(end.getRow(), end.getColumn(), false);
        createNodes(grid);
    }

    private void createNodes(Cell[][] grid) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                var node = new Node(i, j, grid[i][j].isWall());
                node.calcHeuristicTo(end);
                this.grid[i][j] = node;
            }
        }
    }

    public List<Cell> findEscape() {
        open.add(start);
        while (!open.isEmpty()) {
            var cur = open.poll();
            if (isEnd(cur))
                return reconstructPath(cur);
            closed.add(cur);
            updateNeighbors(cur);
        }
        return new ArrayList<>();
    }

    private boolean isEnd(Node currentNode) {
        return currentNode.equals(end);
    }

    private List<Cell> reconstructPath(Node cur) {
        var path = new LinkedList<Cell>();
        path.add(toCell(cur));
        while (cur.getParent() != cur) {
            var parent = cur.getParent();
            path.addFirst(toCell(parent));
            cur = parent;
        }
        return path;
    }

    private Cell toCell(Node node) {
        return new Cell(node.getRow(), node.getColumn(), Cell.Type.ESCAPE);
    }

    private void updateNeighbors(Node cur) {
        for (var delta : DELTAS) {
            var row = cur.getRow() + delta[0];
            var column = cur.getColumn() + delta[1];
            if (inBounds(row, column)) {
                var node = grid[row][column];
                if (!node.isWall() && !closed.contains(node)) {
                    if (open.contains(node)) {
                        if (node.hasBetterPath(cur)) {
                            open.remove(node);
                        } else {
                            continue;
                        }
                    }
                    node.updatePath(cur);
                    open.add(node);
                }
            }
        }
    }

    private boolean inBounds(int row, int column) {
        return row >= 0 && row < height
                && column >= 0 && column < width;
    }
}

class Node {
    private static final int EDGE_COST = 1;
    private final int row;
    private final int column;
    private final boolean isWall;
    private Node parent;
    private int g;
    private int h;
    private int f;

    Node(int row, int column, boolean isWall) {
        this.row = row;
        this.column = column;
        this.isWall = isWall;
        parent = this;
    }

    int getRow() {
        return row;
    }

    int getColumn() {
        return column;
    }

    boolean isWall() {
        return isWall;
    }

    Node getParent() {
        return parent;
    }

    int getFinalCost() {
        return f;
    }

    void calcHeuristicTo(Node node) {
        this.h = Math.abs(node.row - this.row)
                + Math.abs(node.column - this.column);
    }

    boolean hasBetterPath(Node node) {
        return node.g + EDGE_COST < this.g;
    }

    void updatePath(Node node) {
        this.parent = node;
        this.g = node.g + EDGE_COST;
        f = g + h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var node = (Node) o;
        return row == node.row &&
                column == node.column &&
                isWall == node.isWall;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column, isWall);
    }
}

class Cell {
    public enum Type {
        PASSAGE,
        WALL,
        ESCAPE
    }

    private final int row;
    private final int column;
    private final Type type;

    public Cell(int row, int column, Type type) {
        this.row = row;
        this.column = column;
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean isWall() {
        return type == WALL;
    }

    public boolean isEscape() {
        return type == Type.ESCAPE;
    }
}

class PassageTree {
    private final int height;
    private final int width;

    public PassageTree(int height, int width) {
        this.height = (height - 1) / 2;
        this.width = (width - 1) / 2;
    }

    public List<Cell> generate() {
        var edges = createEdges();
        Collections.shuffle(edges);
        var tree = buildRandomSpanningTree(edges);
        return createPassages(tree);
    }

    private List<Edge> createEdges() {
        var edges = new ArrayList<Edge>();
        for (int column = 1; column < width; column++) {
            edges.add(new Edge(toIndex(0, column),
                    toIndex(0, column - 1)));
        }
        for (int row = 1; row < height; row++) {
            edges.add(new Edge(toIndex(row, 0),
                    toIndex(row - 1, 0)));
        }
        for (int row = 1; row < height; row++) {
            for (int column = 1; column < width; column++) {
                edges.add(new Edge(toIndex(row, column),
                        toIndex(row, column - 1)));
                edges.add(new Edge(toIndex(row, column),
                        toIndex(row - 1, column)));
            }
        }
        return edges;
    }

    private int toIndex(int row, int column) {
        return row * width + column;
    }

    private List<Edge> buildRandomSpanningTree(List<Edge> edges) {
        var disjointSets = new DisjointSet(width * height);
        return edges
                .stream()
                .filter(edge -> connects(edge, disjointSets))
                .collect(toList());
    }

    private boolean connects(Edge edge, DisjointSet disjointSet) {
        return disjointSet.union(edge.getFirstCell(), edge.getSecondCell());
    }

    private List<Cell> createPassages(List<Edge> spanningTree) {
        return spanningTree
                .stream()
                .map(edge -> {
                    var first = fromIndex(edge.getFirstCell());
                    var second = fromIndex(edge.getSecondCell());
                    return getPassage(first, second);
                }).collect(toList());
    }

    private Cell fromIndex(int index) {
        var row = index / width;
        var column = index % width;
        return new Cell(row, column, PASSAGE);
    }

    private Cell getPassage(Cell first, Cell second) {
        var row = first.getRow() + second.getRow() + 1;
        var column = first.getColumn() + second.getColumn() + 1;
        return new Cell(row, column, PASSAGE);
    }
}

class Edge {
    private final int firstCell;
    private final int secondCell;

    Edge(int firstCell, int secondCell) {
        this.firstCell = firstCell;
        this.secondCell = secondCell;
    }

    int getFirstCell() {
        return firstCell;
    }

    int getSecondCell() {
        return secondCell;
    }
}

class DisjointSet {
    private final int[] parent;
    private final int[] rank;

    public DisjointSet(int size) {
        parent = new int[size];
        rank = new int[size];
        range(0, size).forEach(this::makeSet);
    }

    private void makeSet(int i) {
        parent[i] = i;
        rank[i] = 0;
    }

    public int find(int i) {
        if (i != parent[i])
            parent[i] = find(parent[i]);
        return parent[i];
    }

    public boolean union(int i, int j) {
        var iRoot = find(i);
        var jRoot = find(j);
        if (iRoot == jRoot)
            return false;
        if (rank[iRoot] < rank[jRoot]) {
            parent[iRoot] = jRoot;
        } else {
            parent[jRoot] = iRoot;
            if (rank[iRoot] == rank[jRoot])
                rank[iRoot]++;
        }
        return true;
    }
}

public class Main {
    public static Scanner scanner;
    public static Maze maze;
    public static boolean isMazeAvailable = false;

    public static void start() {
        scanner = new Scanner(System.in);
        while (true) {
            System.out.println("=== Menu ===");
            System.out.println("1. Generate a new maze");
            System.out.println("2. Load a maze");
            if (isMazeAvailable) {
                System.out.println("3. Save the maze");
                System.out.println("4. Display the maze");
                System.out.println("5. Find the escape");
            }
            System.out.println("0. Exit");

            try {
                var choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 0:
                        exit();
                        return;
                    case 1:
                        generate();
                        break;
                    case 2:
                        load();
                        break;
                    case 3:
                        save();
                        break;
                    case 4:
                        display();
                        break;
                    case 5:
                        findEscape();
                        break;
                    default:
                        System.out.println("Incorrect option. Please try again");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Incorrect option. Please try again");
            } catch (Exception e) {
                System.out.println("Unknown error");
            }
        }
    }

    public static void findEscape() {
        System.out.println(maze.findEscape());
    }

    public static void exit() {
        scanner.close();
        System.out.println("Bye!");
    }

    public static void generate() {
        System.out.println("Enter the size of the new maze (in the [size] or [height width] format)");
        Scanner sc = new Scanner(System.in);
        var line = sc.nextLine();
        var split = line.split(" ");
        if (split.length == 1) {
            var size = parseInt(split[0]);
            maze = new Maze(size);
        } else if (split.length == 2) {
            var height = parseInt(split[0]);
            var width = parseInt(split[1]);
            maze = new Maze(height, width);
        } else {
            System.out.println("Cannot generate a maze. Invalid size");
        }
        isMazeAvailable = true;
        display();
    }

    public static void load() {
        System.out.println("Enter the filename");
        var filename = scanner.nextLine();
        try {
            var content = Files.readString(Paths.get(filename));
            maze = Maze.load(content);
            isMazeAvailable = true;
            System.out.println("The maze is loaded");
        } catch (IOException e) {
            System.out.println("The file " + filename + " does not exist");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void save() {
        System.out.println("Enter the filename");
        var filename = scanner.nextLine();
        try {
            var export = maze.export();
            Files.write(Paths.get(filename), export.getBytes());
            System.out.println("The maze is saved");
        } catch (IOException e) {
            System.out.println("Cannot write to file " + filename);
        }
    }

    public static void display() {
        System.out.println(maze);
    }

    public static void main(String[] args) {
        start();
    }
}
