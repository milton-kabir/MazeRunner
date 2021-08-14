//package maze;
package com.kabir.milton;

import java.util.*;
import java.util.function.Consumer;

import static com.kabir.milton.Cell.Type.PASSAGE;
import static com.kabir.milton.Cell.Type.WALL;
import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

class Maze {
    private final int height;
    private final int width;
    private final Cell[][] grid;

    public Maze(int height, int width) {
        this.height = height;
        this.width = width;
        grid = new Cell[height][width];
        fillGrid();
    }

    public Maze(int size) {
        this(size, size);
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
                    sb.append("▓▓");
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
    public static Maze maze;

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
        display();
    }

    public static void display() {
        System.out.println(maze);
    }

    public static void main(String[] args) {

        generate();
    }
}
