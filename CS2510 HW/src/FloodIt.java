import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import tester.Tester;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

//Represents a single square of the game area
class Cell {
  // In logical coordinates, with the origin at the top-left corner of the screen
  int x;
  int y;
  boolean flooded;
  Color color;
  // the four adjacent cells to this one
  Cell left;
  Cell top;
  Cell right;
  Cell bottom;

  Cell(int y, int x, Color color) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = false;
  }

  Cell(int y, int x, Color color, boolean flooded) {
    this.x = x;
    this.y = y;
    this.color = color;
    this.flooded = flooded;
  }

  // ArrayList of all possible colors
  static ArrayList<Color> colors = new ArrayList(Arrays.asList(Color.red, Color.blue,
          Color.green, Color.black,
          Color.yellow, Color.cyan, Color.magenta, Color.pink, Color.lightGray, Color.darkGray));
  static int minIndx = 0;
  static int maxInd = colors.size() - 1;

  // Draws a single cell
  public static WorldImage drawCell(Color color) {
    return new RectangleImage(FloodItWorld.SIZE, FloodItWorld.SIZE, OutlineMode.SOLID, color)
            .movePinhole(-FloodItWorld.WIDTH / FloodItWorld.BOARD_SIZE / 2,
                    -FloodItWorld.WIDTH / FloodItWorld.BOARD_SIZE / 2);
  }

  // Gets a random color from the ArrayList<Color> colors
  public static Color randomColor(int color) {
    Random randomNum = new Random();
    return colors.get(minIndx + randomNum.nextInt(color));
  }

  // Gets a random color from the ArrayList<Color> colors with optional seed (used in tests)
  public static Color randomColor(int color, int seed) {
    Random randomNum = new Random(seed);
    return colors.get(minIndx + randomNum.nextInt(color));
  }

  // Checks if this cell is flooded
  boolean isFlooded() {
    return this.flooded;
  }
}

// Renders and runs the game
class FloodItWorld extends World {
  // All the cells of the game
  //Defines an int constant
  static final int BOARD_SIZE = 16;
  static final int SIZE = 20;
  static final int WIDTH = BOARD_SIZE * SIZE;
  static final int HEIGHT = BOARD_SIZE * SIZE;
  WorldScene scene = new WorldScene(0, 0);
  public int color = 9;
  double time = 0;
  Color prevColor;
  int allowedTurns = BOARD_SIZE * 2 + this.color;
  int turns = 0;
  boolean floodingState = false;
  TextImage scoring = new TextImage(this.turns + "/" + this.allowedTurns + " -- timer: "
          + (int) this.time, 14, Color.black);
  TextImage wonText = new TextImage("You win", 14, Color.black);
  ArrayList<ArrayList<Cell>> board;
  Color newColor;
  ArrayList<Cell> floodedCells = new ArrayList<Cell>();

  FloodItWorld(int color) {
    this.board = this.makeGrid(BOARD_SIZE, BOARD_SIZE, color);
  }

  // Constructor with optional seed (used n tests)
  FloodItWorld(int color, int seed) {
    this.board = this.makeGrid(BOARD_SIZE, BOARD_SIZE, color, seed);
  }

  // Produces a grid of items
  public ArrayList<ArrayList<Cell>> makeGrid(int row, int column, int color) {
    ArrayList<ArrayList<Cell>> board = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < column; i++) {
      board.add(new ArrayList<Cell>());
      ArrayList<Cell> r = board.get(i);
      for (int j = 0; j < row; j++) {
        if (i == 0 && j == 0) {
          r.add(new Cell(i * SIZE, j * SIZE, Cell.randomColor(color), true));
        }
        else {
          r.add(new Cell(i * SIZE, j * SIZE, Cell.randomColor(color)));
        }
      }
    }
    linkCells(board);
    return board;
  }

  // Produces a grid of items with optional seed (used in tests)
  public ArrayList<ArrayList<Cell>> makeGrid(int row, int column, int color, int seed) {
    ArrayList<ArrayList<Cell>> board = new ArrayList<ArrayList<Cell>>();
    for (int i = 0; i < column; i++) {
      board.add(new ArrayList<Cell>());
      ArrayList<Cell> r = board.get(i);
      for (int j = 0; j < row; j++) {
        if (i == 0 && j == 0) {
          r.add(new Cell(i * SIZE, j * SIZE, Cell.randomColor(color, seed), true));
        }
        else {
          r.add(new Cell(i * SIZE, j * SIZE, Cell.randomColor(color, seed)));
        }
      }
    }
    linkCells(board);
    return board;
  }

  // Determines the adjecent cells for each cell
  // Effect: Links each cell
  public void linkCells(ArrayList<ArrayList<Cell>> b) {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        if (j + 1 < BOARD_SIZE) {
          b.get(i).get(j).right = b.get(i).get(j + 1);
        }
        if (j - 1 >= 0) {
          b.get(i).get(j).left = b.get(i).get(j - 1);
        }
        if (i + 1 < BOARD_SIZE) {
          b.get(i).get(j).bottom = b.get(i + 1).get(j);
        }
        if (i - 1 >= 0) {
          b.get(i).get(j).top = b.get(i - 1).get(j);
        }
      }
    }
  }

  // Renders the game
  public WorldScene makeScene() {
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        this.scene.placeImageXY(board.get(i).get(j).drawCell(board.get(i).get(j).color),
                (FloodItWorld.WIDTH / FloodItWorld.BOARD_SIZE * j),
                (FloodItWorld.WIDTH / FloodItWorld.BOARD_SIZE * i));
      }
    }
    if (allCellsSameColor() && turns < allowedTurns) {
      this.scoring.text = "You win!   Your score: "
              + (((this.allowedTurns - this.turns) * 100) - (int) this.time);
    }
    else if (turns > allowedTurns) {
      this.scoring.text = "You lose. Press R to reset";
    }
    else {
      this.scoring.text = this.turns + "/" + this.allowedTurns + " -- timer: "
              + (int) this.time;
    }
    this.scene.placeImageXY(this.scoring, FloodItWorld.WIDTH / 2, FloodItWorld.HEIGHT + 10);
    return scene;
  }

  // Effect: Updates how many colors are displayed and starts a new game
  public void onKeyEvent(String key) {
    this.turns = 0;
    if (key.equals("1")) {
      this.color = 1;
    }
    else if (key.equals("2")) {
      this.color = 2;
    }
    else if (key.equals("3")) {
      this.color = 3;
    }
    else if (key.equals("4")) {
      this.color = 4;
    }
    else if (key.equals("5")) {
      this.color = 5;
    }
    else if (key.equals("6")) {
      this.color = 6;
    }
    else if (key.equals("7")) {
      this.color = 7;
    }
    else if (key.equals("8")) {
      this.color = 8;
    }
    else if (key.equals("9")) {
      this.color = 9;
    }
    else if (key.equals("r")) {
      this.color = this.color;
    }
    this.board = this.makeGrid(BOARD_SIZE, BOARD_SIZE, this.color);
    this.time = 0.0;
  }

  // Effect: Updates the game when the mouse is clicked
  public void onMouseClicked(Posn pos) {
    this.prevColor = this.board.get(0).get(0).color;
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        if (((pos.x <= board.get(i).get(j).x + SIZE) && (pos.x >= (board.get(i).get(j).x)))
                && ((pos.y <= board.get(i).get(j).y + SIZE) && (pos.y >= board.get(i).get(j).y))) {
          this.newColor = this.board.get(i).get(j).color;
        }
      }
    }
    if (this.newColor != board.get(0).get(0).color) {
      this.turns++;
    }
  }

  // Updates the world every tick
  public void onTick() {

    // If a cell is flooded, it gets added to the worklist
    for (int i = 0; i < BOARD_SIZE; i++) {
      for (int j = 0; j < BOARD_SIZE; j++) {
        if (this.board.get(i).get(j).flooded) {
          this.floodedCells.add(this.board.get(i).get(j));
        }
      }
    }

    // Every item in the worklist changes colors
    for (Cell c : this.floodedCells) {
      c.color = this.newColor;
      //this.floodedCells.remove(c);
    }

    // Checks the cells adjacent to those in the worklist to see if they should be flooded
    // if so, they get marked as flooded and will be updated in the next tick
    for (Cell c : this.floodedCells) {
      if (c.right != null) {
        if (c.right.color == this.prevColor) {
          c.right.flooded = true;
        }
      }
      if (c.left != null) {
        if (c.left.color == this.prevColor) {
          c.left.flooded = true;
        }
      }
      if (c.top != null) {
        if (c.top.color == this.prevColor) {
          c.top.flooded = true;
        }
      }
      if (c.bottom != null) {
        if (c.bottom.color == this.prevColor) {
          c.bottom.flooded = true;
        }
      }
    }

    // Keep counting time while all the cells are not the same color
    if (!allCellsSameColor()) {
      this.time = this.time + 0.1;
      this.scoring.text = this.turns + "/" + this.allowedTurns + " -- timer: " + (int) this.time;
    }
  }

  // Checks if all the cells are the same color
  public boolean allCellsSameColor() {
    for (int i = 0; i < this.BOARD_SIZE; i++) {
      for (int j = 0; j < this.BOARD_SIZE; j++) {
        // if any cell is not the same as the top left cell, not all the colors are the same
        if (this.board.get(0).get(0).color != this.board.get(i).get(j).color) {
          return false;
        }
      }
    }
    return true;
  }
}

// Examples and tests
class ExamplesFloodIt {
  FloodItWorld world1 = new FloodItWorld(9);
  Cell cell1 = new Cell(0, 0, Color.RED);
  Cell cell2 = new Cell(20, 0, Color.BLUE);

  FloodItWorld fiExample = new FloodItWorld(9);
  WorldScene mt = new WorldScene(0, 0);
  ArrayList<Color> colorsEx = new ArrayList(Arrays.asList(Color.red, Color.blue,
          Color.green, Color.black,
          Color.yellow, Color.cyan, Color.magenta, Color.pink, Color.lightGray, Color.darkGray));
  ArrayList<ArrayList<Cell>> boardExample = new ArrayList<ArrayList<Cell>>();
  ArrayList<Cell> row1 = new ArrayList<Cell>();
  ArrayList<Cell> row2 = new ArrayList<Cell>();
  ArrayList<Cell> row3 = new ArrayList<Cell>();

  // Initialize the example board
  void initBoard() {
    this.cell1.flooded = true;
    this.cell2.flooded = false;

    this.row1 = new ArrayList<Cell>(Arrays.asList(this.cell1, this.cell2,
            new Cell(40, 0, Color.MAGENTA)));

    this.row2 = new ArrayList<Cell>(Arrays.asList(new Cell(0, 20, Color.BLACK),
            new Cell(20, 20, Color.GREEN), new Cell(40, 20, Color.PINK)));

    this.row3 = new ArrayList<Cell>(Arrays.asList(new Cell(0, 40, Color.BLUE),
            new Cell(20, 40, Color.RED), new Cell(40, 40, Color.GREEN)));

    this.boardExample = new ArrayList<ArrayList<Cell>>(Arrays.asList(this.row1, this.row2,
            this.row3));
  }

  // Links the exampleBoard
  // same code from linkCells() however don't get out of bounds exception
  // (since the example board is much smaller than the FloodItWorld.BOARD_SIZE.
  void initLinkBoard() {
    for (int i = 0; i < 1; i++) {
      for (int j = 0; j < 2; j++) {
        if (j + 1 < 2) {
          this.boardExample.get(i).get(j).right = this.boardExample.get(i).get(j + 1);
        }
        if (j - 1 >= 0) {
          this.boardExample.get(i).get(j).left = this.boardExample.get(i).get(j - 1);
        }
        if (i + 1 < 2) {
          this.boardExample.get(i).get(j).bottom = this.boardExample.get(i + 1).get(j);
        }
        if (i - 1 >= 0) {
          this.boardExample.get(i).get(j).top = this.boardExample.get(i - 1).get(j);
        }
      }
    }
  }

  // Runs the world
  void testBigBang(Tester t) {
    world1.bigBang(FloodItWorld.HEIGHT,
            FloodItWorld.WIDTH + FloodItWorld.HEIGHT / FloodItWorld.SIZE, 0.1);
  }

  // Tests the drawCell method
  void testDrawCell(Tester t) {
    this.initBoard();

    t.checkExpect(this.cell1.drawCell(Color.RED),
            new RectangleImage(FloodItWorld.SIZE, FloodItWorld.SIZE, OutlineMode.SOLID, Color.RED)
                    .movePinhole(-FloodItWorld.WIDTH / FloodItWorld.BOARD_SIZE / 2,
                            -FloodItWorld.WIDTH / FloodItWorld.BOARD_SIZE / 2));
    t.checkExpect(this.cell2.drawCell(Color.BLUE),
            new RectangleImage(FloodItWorld.SIZE, FloodItWorld.SIZE, OutlineMode.SOLID, Color.BLUE)
                    .movePinhole(-FloodItWorld.WIDTH / FloodItWorld.BOARD_SIZE / 2,
                            -FloodItWorld.WIDTH / FloodItWorld.BOARD_SIZE / 2));
  }

  // Tests the randomColor method
  void testRandomColor(Tester t) {
    this.initBoard();

    t.checkOneOf(this.cell1.randomColor(9), Color.red, Color.blue, Color.green, Color.black,
            Color.yellow, Color.cyan, Color.magenta, Color.pink, Color.lightGray, Color.darkGray);
    t.checkOneOf(this.cell1.randomColor(8), Color.red, Color.blue, Color.green, Color.black,
            Color.yellow, Color.cyan, Color.magenta, Color.pink, Color.lightGray);
    t.checkOneOf(this.cell1.randomColor(7), Color.red, Color.blue, Color.green, Color.black,
            Color.yellow, Color.cyan, Color.magenta, Color.pink);
    t.checkOneOf(this.cell1.randomColor(6), Color.red, Color.blue, Color.green, Color.black,
            Color.yellow, Color.cyan, Color.magenta);
    t.checkOneOf(this.cell1.randomColor(5), Color.red, Color.blue, Color.green, Color.black,
            Color.yellow, Color.cyan);
    t.checkOneOf(this.cell1.randomColor(4), Color.red, Color.blue, Color.green, Color.black,
            Color.yellow);
  }

  // Tests the isFlooded method
  void testIsFlooded(Tester t) {
    this.initBoard();

    t.checkExpect(this.cell2.isFlooded(), false);
    t.checkExpect(this.cell1.isFlooded(), true);
  }

  // Tests the allCellsSameColor method
  void testAllCellsSameColor(Tester t) {
    this.initBoard();
    t.checkExpect(this.fiExample.allCellsSameColor(), false);
  }

  // Tests the onKeyEventMethod
  void testOnKeyEvent(Tester t) {
    this.initBoard();

    this.fiExample.onKeyEvent("1");
    t.checkExpect(this.fiExample.color, 1);
    this.fiExample.onKeyEvent("2");
    t.checkExpect(this.fiExample.color, 2);
    this.fiExample.onKeyEvent("3");
    t.checkExpect(this.fiExample.color, 3);
    this.fiExample.onKeyEvent("r");
    t.checkExpect(this.fiExample.color, 3);
    this.fiExample.onKeyEvent("4");
    t.checkExpect(this.fiExample.color, 4);
    this.fiExample.onKeyEvent("5");
    t.checkExpect(this.fiExample.color, 5);
    this.fiExample.onKeyEvent("6");
    t.checkExpect(this.fiExample.color, 6);
    this.fiExample.onKeyEvent("7");
    t.checkExpect(this.fiExample.color, 7);
    this.fiExample.onKeyEvent("8");
    t.checkExpect(this.fiExample.color, 8);
    this.fiExample.onKeyEvent("9");
    t.checkExpect(this.fiExample.color, 9);
    this.fiExample.onKeyEvent("r");
    t.checkExpect(this.fiExample.color, 9);
  }

  // Tests the onMouseClick method
  void testOnMouseClick(Tester t) {
    this.initBoard();

    this.fiExample.board = this.fiExample.makeGrid(16, 16, 9);
    this.fiExample.onMouseClicked(new Posn(5, 5));
    t.checkExpect(this.fiExample.newColor, this.fiExample.board.get(0).get(0).color);
    this.fiExample.onMouseClicked(new Posn(15, 5));
    t.checkExpect(this.fiExample.newColor, this.fiExample.board.get(0).get(0).color);
  }

  // Tests part of the makeScene method
  void testMakeScene(Tester t) {
    this.initBoard();

    this.fiExample.turns = 10;
    this.fiExample.allowedTurns = 9;
    this.fiExample.makeScene();
    t.checkExpect(this.fiExample.scoring,
            new TextImage("You lose. Press R to reset", 14, Color.black));
  }

  // Tests the method linkCells
  void testLinkCells(Tester t) {
    this.initBoard();

    t.checkExpect(this.cell1.top, null);
    t.checkExpect(this.cell1.left, null);

    this.initLinkBoard();
    // [][] <-- testing that these two cells would be linked
    // (their bottoms have cells that don't have neighbors to make
    // testing simpler
    t.checkExpect(this.cell1.right, cell2);
    t.checkExpect(this.cell1.left, null);
    t.checkExpect(this.cell1.top, null);
    t.checkExpect(this.cell1.bottom, new Cell(0, 20, Color.BLACK));
    t.checkExpect(this.cell2.bottom, new Cell(20, 20, Color.GREEN));
    t.checkExpect(this.cell2.left, cell1);
    t.checkExpect(this.cell2.right, null);
    t.checkExpect(this.cell2.top, null);
  }
}