import javalib.impworld.World;
import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import tester.Tester;

import java.awt.*;
import java.util.*;

// Describes a Vertex
class Vertex {
  int x;
  int y;
  Vertex left;
  Vertex right;
  Vertex top;
  Vertex bottom;
  ArrayList<Edge> outEdges = new ArrayList<Edge>();
  boolean renderRight;
  boolean renderBottom;
  Vertex previous;

  boolean traveled;

  Vertex(int x, int y) {
    this.x = x;
    this.y = y;
    this.left = null;
    this.right = null;
    this.top = null;
    this.bottom = null;
    this.renderRight = true;
    this.renderBottom = true;
    this.traveled = false;
    this.previous = null;
  }

  // Draws a right wall
  WorldImage drawEdgeRight() {
    return new LineImage(new Posn(0, MazeWorld.CELL_SIZE), Color.black)
            .movePinhole(-1 * MazeWorld.CELL_SIZE, MazeWorld.CELL_SIZE / -2);
  }

  // Draws a bottom wall
  WorldImage drawEdgeBottom() {
    return new LineImage(new Posn(MazeWorld.CELL_SIZE, 0), Color.black)
            .movePinhole(MazeWorld.CELL_SIZE / -2, -1 * MazeWorld.CELL_SIZE);
  }

  // Draws rectangles
  WorldImage draw(int x, int y, Color c) {
    return new RectangleImage(MazeWorld.CELL_SIZE - 2, MazeWorld.CELL_SIZE - 2,
            OutlineMode.SOLID, c).movePinhole(-x * MazeWorld.CELL_SIZE / x / 2,
            -x * MazeWorld.CELL_SIZE / x / 2);
  }

  // Finds the previous cell
  void findPrevious() {
    if (this.top != null && !this.top.renderBottom && this.top.previous == null) {
      this.previous = this.top;
    }
    else if (this.left != null && !this.left.renderRight && this.left.previous == null) {
      this.previous = this.left;
    }
    else if (this.bottom != null && !this.renderBottom && this.bottom.previous == null) {
      this.previous = this.bottom;
    }
    else if (this.right != null && !this.renderRight && this.right.previous == null) {
      this.previous = this.right;
    }
  }
}

// Describes a Player
class Player {
  Vertex on;

  Player(Vertex on) {
    this.on = on;
  }

  // Checks if each key input results in a valid move
  boolean validMove(String move) {
    if (move.equals("up") && this.on.top != null) {
      return !this.on.top.renderBottom;
    }
    else if (move.equals("down") && this.on.bottom != null) {
      return !this.on.renderBottom;
    }
    else if (move.equals("left") && this.on.left != null) {
      return !this.on.left.renderRight;
    }
    else if (move.equals("right") && this.on.right != null) {
      return !this.on.renderRight;
    }
    else {
      return false;
    }
  }

  // Draws the player
  WorldImage drawPlayer() {
    return new RectangleImage(MazeWorld.CELL_SIZE - 3, MazeWorld.CELL_SIZE - 3,
            OutlineMode.SOLID, Color.blue).movePinhole(-MazeWorld.CELL_SIZE / 2,
            -MazeWorld.CELL_SIZE / 2);
  }
}

// Describes an Edge
class Edge {
  Vertex from;
  Vertex to;
  int weight;

  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }
}

// Compares the wieghts of Edges
class WeightComparator implements Comparator<Edge> {
  // Compares edges by weight
  public int compare(Edge item1, Edge item2) {
    return item1.weight - item2.weight;
  }
}

//Describes the world
class MazeWorld extends World {
  static final int CELL_SIZE = 20;
  int boardSizeX;
  int boardSizeY;
  HashMap<Vertex, Vertex> map = new HashMap<Vertex, Vertex>();
  ArrayList<Edge> loe = new ArrayList<Edge>();
  ArrayList<Edge> mst = new ArrayList<Edge>();
  ArrayList<Vertex> path = new ArrayList<Vertex>();
  Vertex endCell;

  WorldScene scene = new WorldScene(0, 0);
  ArrayList<ArrayList<Vertex>> board;

  boolean done;

  Player p;

  TextImage won = new TextImage("You Won!", 30, Color.BLACK);
  TextImage lost = new TextImage("You lost!", 30, Color.BLACK);
  double tickRate = 0.01;
  double time;
  TextImage timeLeft;

  MazeWorld(int boardSizeX, int boardSizeY) {
    this.boardSizeX = boardSizeX;
    this.boardSizeY = boardSizeY;
    this.board = this.makeGrid(boardSizeX, boardSizeY);
    this.createEdges(this.board);
    this.createMap(board);
    this.kruskals();
    this.p = new Player(board.get(0).get(0));
    this.endCell = this.board.get(boardSizeY - 1).get(boardSizeX - 1);
    this.time = this.boardSizeX * this.boardSizeY;
    this.timeLeft = new TextImage("Time left " + (int) this.time, 14, Color.black);
    this.renderWorld();
    this.done = false;
  }

  // Special constructor used for testing
  MazeWorld() {
    this.boardSizeX = 2;
    this.boardSizeY = 3;
    this.board = this.makeGrid(2, 3, "test");
    this.board.get(0).get(0).renderRight = false;
    this.board.get(0).get(1).renderRight = true;
    this.board.get(1).get(0).renderRight = true;
    this.board.get(1).get(1).renderRight = true;
    this.board.get(2).get(0).renderRight = true;
    this.board.get(2).get(1).renderRight = true;
    this.map.put(this.board.get(0).get(0), this.board.get(0).get(0));
    this.map.put(this.board.get(0).get(1), this.board.get(0).get(1));
    this.map.put(this.board.get(1).get(0), this.board.get(1).get(0));
    this.map.put(this.board.get(1).get(1), this.board.get(1).get(1));
    this.map.put(this.board.get(2).get(0), this.board.get(2).get(0));
    this.map.put(this.board.get(2).get(1), this.board.get(2).get(1));

    this.board.get(0).get(0).renderBottom = false;
    this.board.get(0).get(1).renderBottom = false;
    this.board.get(1).get(0).renderBottom = false;
    this.board.get(1).get(1).renderBottom = false;
    this.board.get(2).get(0).renderBottom = true;
    this.board.get(2).get(1).renderBottom = true;

    this.loe = new ArrayList<Edge>(Arrays.asList(
            new Edge(new Vertex(0, 0), new Vertex(1, 0), 1),
            new Edge(new Vertex(0, 0), new Vertex(0, 1), 2),
            new Edge(new Vertex(1, 0), new Vertex(1, 1), 3),
            new Edge(new Vertex(0, 1), new Vertex(1, 1), 4),
            new Edge(new Vertex(0, 1), new Vertex(0, 2), 5),
            new Edge(new Vertex(1, 1), new Vertex(1, 2), 6),
            new Edge(new Vertex(0, 2), new Vertex(1, 2), 7)));

    this.mst = new ArrayList<Edge>(Arrays.asList(
            new Edge(new Vertex(0, 0), new Vertex(1, 0), 1),
            new Edge(new Vertex(0, 0), new Vertex(0, 1), 2),
            new Edge(new Vertex(1, 0), new Vertex(1, 1), 3),
            new Edge(new Vertex(0, 1), new Vertex(0, 2), 5),
            new Edge(new Vertex(1, 1), new Vertex(1, 2), 6)));

    this.p = new Player(this.board.get(0).get(0));
    this.done = false;
    this.path = new ArrayList<Vertex>();
    this.endCell = this.board.get(2).get(1);
    if (boardSizeX < 10) {
      time = 100;
    }
    else {
      time = boardSizeX * boardSizeX;
    }
    this.timeLeft = new TextImage("Time left " + (int) this.time, 14, Color.black);
    this.renderWorld();
  }

  // Renders the world grid, start and end positions and single time when the world is created
  WorldScene renderWorld() {
    // Draw starting square
    this.scene.placeImageXY(board.get(0).get(0).draw(this.boardSizeX, this.boardSizeY, Color.GREEN),
            0, 0);
    // Draw ending square
    this.scene.placeImageXY(board.get(this.boardSizeY - 1).get(this.boardSizeX - 1)
                    .draw(this.boardSizeX, this.boardSizeY, Color.RED),
            (boardSizeX - 1) * CELL_SIZE, (boardSizeY - 1) * CELL_SIZE);
    // Draw the grid
    for (int i = 0; i < boardSizeY; i++) {
      for (int j = 0; j < boardSizeX; j++) {
        this.changeRenderBottom(this.board.get(i).get(j));
        this.changeRenderRight(this.board.get(i).get(j));
        if (this.board.get(i).get(j).traveled) {
          this.scene.placeImageXY(board.get(i).get(j).draw(this.boardSizeX,
                  this.boardSizeY, Color.YELLOW), j * CELL_SIZE, i * CELL_SIZE);
        }
        if (board.get(i).get(j).renderRight) {
          this.scene.placeImageXY(board.get(i).get(j).drawEdgeRight(),
                  (MazeWorld.CELL_SIZE * j),
                  (MazeWorld.CELL_SIZE * i));
        }
        if (board.get(i).get(j).renderBottom) {
          this.scene.placeImageXY(board.get(i).get(j).drawEdgeBottom(),
                  (MazeWorld.CELL_SIZE * j),
                  (MazeWorld.CELL_SIZE * i));
        }
      }
    }
    // Draw the player
    this.scene.placeImageXY(p.drawPlayer(), this.p.on.x * CELL_SIZE, this.p.on.y * CELL_SIZE);
    this.scene.placeImageXY(this.timeLeft, CELL_SIZE + 20,
            boardSizeY * CELL_SIZE + CELL_SIZE / 2);
    return scene;
  }

  // Updates the game every tick
  public WorldScene makeScene() {
    // If there are at least two Vertexes in our path that need to be drawn,
    // Set the next item to have this as its previous, then draw this item
    if (path.size() > 1) {
      this.findEnd();
    }
    // If there is only one Vertex left, draw it then mark the maze as complete
    else if (path.size() > 0) {
      this.drawEnd();
    }
    // If the maze is complete, trace back the solution
    else if (this.done && this.endCell.previous != null) {
      this.traceback();
    }
    // Keep counting down time while the player has not completed the maze yet
    if (this.p.on != this.board.get(this.boardSizeY - 1).get(this.boardSizeX - 1)
            && this.p.on != this.endCell) {
      this.time = this.time - this.tickRate;
      this.timeLeft.text = "Time left " + (int) this.time;
    }
    if (this.time == 0 && p.on != this.board.get(boardSizeY - 1).get(boardSizeX - 1)) {
      this.scene.placeImageXY(lost, boardSizeX * CELL_SIZE / 2, boardSizeY * CELL_SIZE / 2);
    }
    // Places the winning text when the maze is solved
    if (p.on == this.board.get(boardSizeY - 1).get(boardSizeX - 1)) {
      this.scene.placeImageXY(won, boardSizeX * CELL_SIZE / 2, boardSizeY * CELL_SIZE / 2);
      time = 0;
    }
    if (this.time <= 0.0) {
      this.scene.placeImageXY(lost, boardSizeX * CELL_SIZE / 2, boardSizeY * CELL_SIZE / 2);
      this.time = 0.0;
    }
    return scene;
  }

  // Changes if the right wall should be rendered for the given vertex
  // Effect: Changes the renderRight field of the vertex
  void changeRenderRight(Vertex v) {
    for (Edge edge : this.mst) {
      if (edge.to.y == edge.from.y) {
        edge.from.renderRight = false;
      }
    }
  }

  // Changes whether the bottom wall should be rendered for the given vertex
  // Effect: Changes the renderRight field of the vertex
  void changeRenderBottom(Vertex v) {
    for (Edge edge : this.mst) {
      if (edge.to.x == edge.from.x) {
        edge.from.renderBottom = false;
      }
    }
  }

  // creates the grid for each cell in the maze
  ArrayList<ArrayList<Vertex>> makeGrid(int bWidth, int bHeight) {
    ArrayList<ArrayList<Vertex>> board = new ArrayList<ArrayList<Vertex>>();
    for (int i = 0; i < bHeight; i++) {
      board.add(new ArrayList<Vertex>());
      ArrayList<Vertex> r = board.get(i);
      for (int j = 0; j < bWidth; j++) {
        r.add(new Vertex(j, i));
      }
    }
    this.linkVertexs(board);
    this.createEdges(board);
    this.createMap(board);
    return board;
  }

  // creates the grid for each cell in the maze
  ArrayList<ArrayList<Vertex>> makeGrid(int bWidth, int bHeight, String a) {
    ArrayList<ArrayList<Vertex>> board = new ArrayList<ArrayList<Vertex>>();
    for (int i = 0; i < bHeight; i++) {
      board.add(new ArrayList<Vertex>());
      ArrayList<Vertex> r = board.get(i);
      for (int j = 0; j < bWidth; j++) {
        r.add(new Vertex(j, i));
      }
    }
    this.linkVertexs(board);
    return board;
  }

  // connects/links each individual cell
  // Effect: Changes the top, bottom, left and right fields of a vertex
  void linkVertexs(ArrayList<ArrayList<Vertex>> b) {
    for (int i = 0; i < this.boardSizeY; i++) {
      for (int j = 0; j < this.boardSizeX; j++) {
        if (j + 1 < this.boardSizeX) {
          b.get(i).get(j).right = b.get(i).get(j + 1);
        }
        if (j - 1 >= 0) {
          b.get(i).get(j).left = b.get(i).get(j - 1);
        }
        if (i + 1 < this.boardSizeY) {
          b.get(i).get(j).bottom = b.get(i + 1).get(j);
        }
        if (i - 1 >= 0) {
          b.get(i).get(j).top = b.get(i - 1).get(j);
        }
      }
    }
  }

  // creates the arraylist of edges in the maze game
  ArrayList<Edge> createEdges(ArrayList<ArrayList<Vertex>> n) {
    Random randomWeight = new Random();
    for (int i = 0; i < n.size(); i++) {
      for (int j = 0; j < n.get(i).size(); j++) {
        if (j < n.get(i).size() - 1) {
          loe.add(new Edge(n.get(i).get(j), n.get(i).get(j).right, randomWeight.nextInt(50)));
        }
        if (i < n.size() - 1) {
          loe.add(new Edge(n.get(i).get(j), n.get(i).get(j).bottom,
                  (int) randomWeight.nextInt(50)));
        }
      }
    }
    Collections.sort(loe, new WeightComparator());
    return loe;
  }

  // creates an initial hashmap where each node is linked to itself
  // map = HashMap<Vertex, Vertex>();
  HashMap<Vertex, Vertex> createMap(ArrayList<ArrayList<Vertex>> vertex) {
    for (int i = 0; i < vertex.size(); i++) {
      for (int j = 0; j < vertex.get(i).size(); j++) {
        this.map.put(vertex.get(i).get(j), vertex.get(i).get(j));
      }
    }
    return map;
  }

  // kruskal's algorithm which creates the minimum spanning tree
  ArrayList<Edge> kruskals() {
    int i = 0;
    while (this.mst.size() < this.loe.size() && i < this.loe.size()) {
      Edge e = loe.get(i);
      if (this.find(this.find(e.from)).equals(this.find(this.find(e.to)))) {
        // do nothing
      }
      else {
        mst.add(e);
        union(this.find(e.from), this.find(e.to));
      }
      i += 1;
    }
    // Adds all the outEdges for each vertex
    for (int y = 0; y < this.boardSizeY; y += 1) {
      for (int x = 0; x < this.boardSizeX; x += 1) {
        for (Edge e : this.mst) {
          if (this.board.get(y).get(x).equals(e.from) || this.board.get(y).get(x).equals(e.to)) {
            this.board.get(y).get(x).outEdges.add(e);
          }
        }
      }
    }
    return this.mst;
  }

  // Unions two vertexs
  // Effect: Changes the value in the hashmap for the key
  void union(Vertex item, Vertex newRep) {
    this.map.put(this.find(item), this.find(newRep));
  }

  // Finds the representative of this node
  Vertex find(Vertex item) {
    if (item.equals(this.map.get(item))) {
      return item;
    }
    else {
      return this.find(this.map.get(item));
    }
  }

  // On key press moves the player, creates a new maze or runs DFS/BFS
  // Effect: Updates the world on key presses
  public void onKeyEvent(String key) {
    if (key.equals("n")) {
      this.scene = this.getEmptyScene();
      this.board = this.makeGrid(boardSizeX, boardSizeY);
      this.createEdges(this.board);
      this.createMap(board);
      this.kruskals();
      this.time = this.boardSizeX * this.boardSizeY + 1;
      this.p = new Player(board.get(0).get(0));
      this.endCell = this.board.get(this.boardSizeY - 1).get(this.boardSizeX - 1);
      this.renderWorld();
    }
    else if (key.equals("up") && p.validMove("up")) {
      p.on.traveled = true;
      p.on = p.on.top;
    }
    else if (key.equals("down") && p.validMove("down")) {
      p.on.traveled = true;
      p.on = p.on.bottom;
    }
    else if (key.equals("left") && p.validMove("left")) {
      p.on.traveled = true;
      p.on = p.on.left;
    }
    else if (key.equals("right") && p.validMove("right")) {
      p.on.traveled = true;
      p.on = p.on.right;
    }
    else if (key.equals("d")) {
      this.endCell = this.board.get(this.boardSizeY - 1).get(this.boardSizeX - 1);
      this.path = new Graph().pathDFS(this.board.get(0).get(0), this.board.get(this.boardSizeY - 1)
              .get(this.boardSizeX - 1));
    }
    else if (key.equals("b")) {
      this.endCell = this.board.get(this.boardSizeY - 1).get(this.boardSizeX - 1);
      this.path = new Graph().pathBFS(this.board.get(0).get(0), this.board.get(this.boardSizeY - 1)
              .get(this.boardSizeX - 1));
    }
    this.scene.placeImageXY(p.drawPlayer(), p.on.x * CELL_SIZE, p.on.y * CELL_SIZE);
    this.renderWorld();
  }

  // Effect: Update the game every tick
  public void onTick() {
    // I am actually useless and don't need to exist because things are being rendered in makeScene
  }

  // Draw the entire path taken to the end of the maze
  // Effect: Updates the maze with Cyan squares indicating the path
  void findEnd() {
    Vertex next = path.remove(0);
    this.scene.placeImageXY(next.draw(this.boardSizeX, this.boardSizeY, Color.CYAN),
            next.x * CELL_SIZE, next.y * CELL_SIZE);
  }

  // Draws the last item before the end of the maze
  // Effect: Updates the maze with Cyan squares indicating the path
  void drawEnd() {
    Vertex next = path.remove(0);
    this.scene.placeImageXY(next.draw(this.boardSizeX, this.boardSizeY, Color.CYAN),
            next.x * CELL_SIZE, next.y * CELL_SIZE);
    if (!this.endCell.left.renderRight && this.endCell.left.previous != null) {
      this.endCell.previous = this.endCell.left;
    }
    else if (!this.endCell.top.renderBottom && this.endCell.top.previous != null) {
      this.endCell.previous = this.endCell.top;
    }
    else {
      this.endCell.previous = next;
    }
    this.done = true;
  }

  // Traces back to the start of the maze
  // Effect: Updates the maze with Magenta squares indicating the solution
  void traceback() {
    if (this.endCell.x == this.boardSizeX - 1 && this.endCell.y == this.boardSizeY - 1) {
      this.scene.placeImageXY(this.endCell.draw(this.boardSizeX, this.boardSizeY,
              Color.magenta), this.endCell.x * CELL_SIZE,
              this.endCell.y * CELL_SIZE);
    }
    this.scene.placeImageXY(this.endCell.previous.draw(this.boardSizeX, this.boardSizeY,
            Color.magenta), this.endCell.previous.x * CELL_SIZE,
            this.endCell.previous.y * CELL_SIZE);
    this.endCell = this.endCell.previous;
  }
}

// An ICollection is one of
// - A Queue
// - A Stack
interface ICollection<T> {
  // Adds an item to this ICollection
  void add(T item);

  // Removes an item from this ICollection
  T remove();

  // Returns the size of this ICollection
  int size();
}

// Describes a Queue
// Used in Breadth-first Search
class Queue<T> implements ICollection<T> {
  Deque<T> items;

  Queue() {
    this.items = new Deque<T>();
  }

  // Adds an item to this Queue
  public void add(T item) {
    this.items.addAtTail(item);
  }

  // Removes an item from this Queue
  public T remove() {
    return this.items.removeFromHead();
  }

  // Returns the size of this Queue
  public int size() {
    return this.items.size();
  }
}

// Describes a Stack
// Used in Depth-first Search
class Stack<T> implements ICollection<T> {
  Deque<T> items;

  Stack() {
    this.items = new Deque<T>();
  }

  // Adds an item to a Stack
  public void add(T item) {
    this.items.addAtHead(item);
  }

  // Removes and item to a Stack
  public T remove() {
    return this.items.removeFromHead();
  }

  // Returns the size of this Stack
  public int size() {
    return this.items.size();
  }
}

// Describes graph solving algorithms
class Graph {

  ArrayList<Vertex> allVertices;

  Graph() {
  }

  // Finds the path using a Stack
  // Is an implementation of DFS
  ArrayList<Vertex> pathDFS(Vertex from, Vertex to) {
    return this.createPath(from, to, new Stack<Vertex>());
  }

  // Finds the path using a Queue
  // Is an implementation of BFSD
  ArrayList<Vertex> pathBFS(Vertex from, Vertex to) {
    return this.createPath(from, to, new Queue<Vertex>());
  }

  // FInds the path using an ICollection
  ArrayList<Vertex> createPath(Vertex from, Vertex to, ICollection<Vertex> worklist) {
    ArrayList<Vertex> path = new ArrayList<Vertex>();

    worklist.add(from);
    while (worklist.size() > 0) {
      Vertex next = worklist.remove();
      if (next == to) {
        return path;
      }
      else if (path.contains(next)) {
        // Do nothing
      }
      else {
        for (Edge e : next.outEdges) {
          worklist.add(e.from);
          worklist.add(e.to);
          if (path.contains(e.from)) {
            next.previous = e.from;
          }
          else if (path.contains(e.to)) {
            next.previous = e.to;
          }
        }
        path.add(next);
      }
    }
    return path;
  }
}

//Examples and tests
class ExamplesMazeGame {
  MazeWorld RunGame = new MazeWorld(50, 30);

  Graph g = new Graph();

  // Tests the makeGrid method
  void testMakeGrid(Tester t) {
    MazeWorld w1 = new MazeWorld();
    t.checkExpect(w1.board, new ArrayList<ArrayList<Vertex>>(Arrays.asList(
            new ArrayList<Vertex>(Arrays.asList(w1.board.get(0).get(0), w1.board.get(0).get(1))),
            new ArrayList<Vertex>(Arrays.asList(w1.board.get(1).get(0), w1.board.get(1).get(1))),
            new ArrayList<Vertex>(Arrays.asList(w1.board.get(2).get(0), w1.board.get(2).get(1))))));
  }

  // Tests linkVertexs method
  void testLinkVertexs(Tester t) {
    MazeWorld w1 = new MazeWorld();
    t.checkExpect(w1.board.get(0).get(0).right, w1.board.get(0).get(1));
    t.checkExpect(w1.board.get(0).get(0).bottom, w1.board.get(1).get(0));
    t.checkExpect(w1.board.get(0).get(0).top, null);
    t.checkExpect(w1.board.get(0).get(0).left, null);
  }

  // Tests createEdges method
  void testCreateEdges(Tester t) {

    MazeWorld w1 = new MazeWorld();
    t.checkExpect(w1.loe.get(0),
            new Edge(new Vertex(w1.board.get(0).get(0).x, w1.board.get(0).get(0).y),
                    new Vertex(w1.board.get(0).get(1).x, w1.board.get(0).get(1).y), 1));
    t.checkExpect(w1.loe.get(1),
            new Edge(new Vertex(w1.board.get(0).get(0).x, w1.board.get(0).get(0).y),
                    new Vertex(w1.board.get(1).get(0).x, w1.board.get(1).get(0).y), 2));
    t.checkExpect(w1.loe.get(2),
            new Edge(new Vertex(w1.board.get(0).get(1).x, w1.board.get(0).get(1).y),
                    new Vertex(w1.board.get(1).get(1).x, w1.board.get(1).get(1).y), 3));
    t.checkExpect(w1.loe.get(3),
            new Edge(new Vertex(w1.board.get(1).get(0).x, w1.board.get(1).get(0).y),
                    new Vertex(w1.board.get(1).get(1).x, w1.board.get(1).get(1).y), 4));
    t.checkExpect(w1.loe.get(4),
            new Edge(new Vertex(w1.board.get(1).get(0).x, w1.board.get(1).get(0).y),
                    new Vertex(w1.board.get(2).get(0).x, w1.board.get(2).get(0).y), 5));
    t.checkExpect(w1.loe.get(5),
            new Edge(new Vertex(w1.board.get(1).get(1).x, w1.board.get(1).get(1).y),
                    new Vertex(w1.board.get(2).get(1).x, w1.board.get(2).get(1).y), 6));
    t.checkExpect(w1.loe.get(6),
            new Edge(new Vertex(w1.board.get(2).get(0).x, w1.board.get(2).get(0).y),
                    new Vertex(w1.board.get(2).get(1).x, w1.board.get(2).get(1).y), 7));
  }

  // Tests createMap method
  void testCreateMap(Tester t) {
    MazeWorld w1 = new MazeWorld();
    t.checkExpect(w1.map.get(w1.board.get(0).get(0)), w1.board.get(0).get(0));
    t.checkExpect(w1.map.get(w1.board.get(0).get(1)), w1.board.get(0).get(1));
    t.checkExpect(w1.map.get(w1.board.get(1).get(0)), w1.board.get(1).get(0));
    t.checkExpect(w1.map.get(w1.board.get(1).get(1)), w1.board.get(1).get(1));
    t.checkExpect(w1.map.get(w1.board.get(2).get(0)), w1.board.get(2).get(0));
    t.checkExpect(w1.map.get(w1.board.get(2).get(1)), w1.board.get(2).get(1));
  }

  // Tests kruskals method
  void testKruskals(Tester t) {
    MazeWorld w1 = new MazeWorld();

    w1.makeGrid(w1.boardSizeX, w1.boardSizeY);
    t.checkExpect(w1.mst.get(0), new Edge(w1.mst.get(0).from, w1.mst.get(0).to, 1));
    t.checkExpect(w1.mst.get(1), new Edge(w1.mst.get(1).from, w1.mst.get(1).to, 2));
    t.checkExpect(w1.mst.get(2), new Edge(w1.mst.get(2).from, w1.mst.get(2).to, 3));
    t.checkExpect(w1.mst.get(3), new Edge(w1.mst.get(3).from, w1.mst.get(3).to, 5));
    t.checkExpect(w1.mst.get(4), new Edge(w1.mst.get(4).from, w1.mst.get(4).to, 6));
  }

  // Tests union method
  void testUnion(Tester t) {
    MazeWorld w1 = new MazeWorld();

    w1.union(w1.board.get(0).get(0), w1.board.get(0).get(1));
    t.checkExpect(w1.find(w1.board.get(0).get(0)), w1.board.get(0).get(1));
    w1.union(w1.board.get(0).get(1), w1.board.get(1).get(1));
    t.checkExpect(w1.find(w1.board.get(0).get(1)), w1.board.get(1).get(1));
    w1.union(w1.board.get(2).get(0), w1.board.get(0).get(1));
    t.checkExpect(w1.find(w1.board.get(0).get(0)), w1.board.get(1).get(1));
  }

  // Tests find method
  void testFind(Tester t) {
    MazeWorld w1 = new MazeWorld();
    t.checkExpect(w1.find(w1.board.get(0).get(0)), w1.board.get(0).get(0));
    t.checkExpect(w1.find(w1.board.get(2).get(0)), w1.board.get(2).get(0));
  }

  // Tests onKeyEvent method
  void testOnKeyEvent(Tester t) {

    MazeWorld w1 = new MazeWorld();
    w1.onKeyEvent("right");
    t.checkExpect(w1.p.on, w1.board.get(0).get(1));
    w1.onKeyEvent("down");
    t.checkExpect(w1.p.on, w1.board.get(1).get(1));
    w1.onKeyEvent("up");
    t.checkExpect(w1.p.on, w1.board.get(0).get(1));
    w1.onKeyEvent("left");
    t.checkExpect(w1.p.on, w1.board.get(0).get(0));
    w1.onKeyEvent("d");
    t.checkExpect(w1.path, new ArrayList<Vertex>(Arrays.asList(w1.board.get(0).get(0))));
    w1.onKeyEvent("b");
    t.checkExpect(w1.path, new ArrayList<Vertex>(Arrays.asList(w1.board.get(0).get(0))));
  }

  // Tests validMove method
  void testValidMove(Tester t) {
    MazeWorld w1 = new MazeWorld();

    t.checkExpect(w1.p.validMove("up"), false);
    t.checkExpect(w1.p.validMove("left"), false);
    t.checkExpect(w1.p.validMove("down"), true);
    t.checkExpect(w1.p.validMove("right"), true);
  }

  // Test renderRight method
  void testChangeRenderRight(Tester t) {
    MazeWorld w1 = new MazeWorld();

    w1.changeRenderRight(w1.board.get(0).get(0));
    t.checkExpect(w1.board.get(0).get(0).renderRight, false);

    w1.changeRenderRight(w1.board.get(2).get(0));
    t.checkExpect(w1.board.get(2).get(0).renderRight, true);
  }

  // Tests renderBottom method
  void testChangeRenderBottom(Tester t) {
    MazeWorld w1 = new MazeWorld();
    w1.changeRenderBottom(w1.board.get(0).get(0));
    t.checkExpect(w1.board.get(0).get(0).renderBottom, false);

    w1.changeRenderBottom(w1.board.get(0).get(1));
    t.checkExpect(w1.board.get(0).get(1).renderBottom, false);

    w1.changeRenderBottom(w1.board.get(2).get(0));
    t.checkExpect(w1.board.get(2).get(0).renderBottom, true);

    w1.changeRenderBottom(w1.board.get(1).get(0));
    t.checkExpect(w1.board.get(1).get(0).renderBottom, false);

    w1.changeRenderBottom(w1.board.get(1).get(1));
    t.checkExpect(w1.board.get(1).get(1).renderBottom, false);

    w1.changeRenderBottom(w1.board.get(2).get(1));
    t.checkExpect(w1.board.get(2).get(1).renderBottom, true);
  }

  // Tests add (queue) method
  void testAddAtTail(Tester t) {
    Queue<Vertex> q = new Queue<Vertex>();

    t.checkExpect(q.size(), 0);
    q.add(new Vertex(0, 0));
    t.checkExpect(q.size(), 1);
  }

  // Tests size
  void testSize(Tester t) {
    Queue<Vertex> q = new Queue<Vertex>();
    Stack<Vertex> s = new Stack<Vertex>();

    t.checkExpect(s.size(), 0);
    s.add(new Vertex(1, 0));
    t.checkExpect(s.size(), 1);

    t.checkExpect(q.size(), 0);
    q.add(new Vertex(0, 0));
    t.checkExpect(q.size(), 1);
  }

  //Tests remove (queue and stack) method
  void testRemoveFromHead(Tester t) {
    Queue<Vertex> q = new Queue<Vertex>();
    q.add(new Vertex(0, 0));

    t.checkExpect(q.remove(), new Vertex(0, 0));
  }

  //Tests add (stack) method
  void testAddToHead(Tester t) {
    Stack<Vertex> s = new Stack<Vertex>();
    t.checkExpect(s.size(), 0);
    s.add(new Vertex(0, 0));

    t.checkExpect(s.size(), 1);
  }

  //Tests add (stack) method
  void testAddToTail(Tester t) {
    Stack<Vertex> s = new Stack<Vertex>();
    t.checkExpect(s.size(), 0);
    s.add(new Vertex(0, 0));

    t.checkExpect(s.size(), 1);
  }

  // Tests pathDFS method
  void testPathDFS(Tester t) {
    MazeWorld w1 = new MazeWorld();

    t.checkExpect(g.pathDFS(w1.board.get(0).get(0), w1.board.get(2).get(1)),
            new ArrayList<Vertex>(Arrays.asList(w1.board.get(0).get(0))));
  }

  // Tests pathDFS method
  void testPathBFS(Tester t) {
    MazeWorld w1 = new MazeWorld();

    t.checkExpect(g.pathBFS(w1.board.get(0).get(0), w1.board.get(2).get(1)),
            new ArrayList<Vertex>(Arrays.asList(w1.board.get(0).get(0))));
  }

  // Tests hasPath method
  void testHasPath(Tester t) {
    MazeWorld w1 = new MazeWorld();

    t.checkExpect(g.createPath(w1.board.get(0).get(0), w1.board.get(2).get(1), new Stack<Vertex>()),
            new ArrayList<Vertex>(Arrays.asList(w1.board.get(0).get(0))));

    t.checkExpect(g.createPath(w1.board.get(0).get(0), w1.board.get(2).get(1), new Queue<Vertex>()),
            new ArrayList<Vertex>(Arrays.asList(w1.board.get(0).get(0))));
  }

  // Tests the timer
  void testTimer(Tester t) {
    MazeWorld w1 = new MazeWorld();
    w1.onTick();

    t.checkInexact(w1.time, 100.0, 0.001);
  }

  // Run world
  void testBigBang(Tester t) {
    /*MazeWorld w1 = new MazeWorld();
    w1.bigBang(100, 100, .1);*/

    this.RunGame.bigBang(this.RunGame.boardSizeX * MazeWorld.CELL_SIZE,
            this.RunGame.boardSizeY * MazeWorld.CELL_SIZE + MazeWorld.CELL_SIZE,
            this.RunGame.tickRate);
  }
}