import tester.Tester;

// Predicates
interface IPred<T> {
  boolean apply(T t);
}

// Describes the DataIsGreaterThan5 predicate
class DataIsGreaterThan5 implements IPred<Integer> {
  public boolean apply(Integer t) {
    return t > 5;
  }
}

// Describes the DataStringLengthGreaterThan5 predicate
class DataStringLengthGreaterThan5 implements IPred<String> {
  public boolean apply(String t) {
    return t.length() > 5;
  }
}

// Describes a Deque
class Deque<T> {
  Sentinel<T> header;

  Deque() {
    header = new Sentinel<T>();
  }

  Deque(Sentinel<T> that) {
    this.header = that;
  }

  // Determines the size of this deque
  int size() {
    return this.header.next.sizeHelper(0);
  }

  // given a data, insert the data at the front of the list
  void addAtHead(T data) {
    this.header.addAtHead(data);
  }

  // EFFECT: insert the data at the tail of the list
  void addAtTail(T data) {
    this.header.addAtTail(data);
  }

  // Returns the Node that was removed (if possible) from the Deque
  T removeFromHead() {
    return this.header.next.removeFrom();
  }

  // Returns a new Deque with the last element removed
  T removeFromTail() {
    return this.header.prev.removeFrom();
  }

  // Finds a specific node in this deque given a predicate
  ANode<T> find(IPred<T> p) {
    return this.header.next.findHelper(p);
  }

  // Checks if this node is the same as that node
  boolean sameNode(Node<T> n) {
    return this.equals(n);
  }

  // Removes the given node from this deque
  void removeNode(ANode<T> n) {
    n.removeNodeHelper();
  }
}

// Describes an ANode
abstract class ANode<T> {
  ANode<T> next;
  ANode<T> prev;

  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }

  // Helps determine the size of this deque
  abstract int sizeHelper(int acc);

  // Checks if this item is a sentinel
  abstract boolean isSentinel();

  // EFFECT: Set this node have the given nodes ad prev and next
  void setNode(ANode<T> prev, ANode<T> next) {
    this.next = next;
    this.prev = prev;
  }

  // Removes this node from the deque
  abstract T removeFrom();

  // Helps find the first node given a predicate
  abstract ANode<T> findHelper(IPred<T> p);

  // Helps remove this node
  abstract void removeNodeHelper();
}

// Describes a node
class Node<T> extends ANode<T> {
  T data;

  Node(T data) {
    super(null, null);
    this.data = data;
  }

  Node(T data, ANode<T> node1, ANode<T> node2) {
    super(null, null);
    this.data = data;
    if (node1 == null || node2 == null) {
      throw new IllegalArgumentException("Nodes must not be null");
    }
    else {
      this.next = node1;
      this.prev = node2;
    }
    node1.prev = this;
    node2.next = this;
  }

  // Helps determine the size of this deque
  int sizeHelper(int acc) {
    if (this.next.isSentinel()) {
      return 1 + acc;
    }
    else {
      return this.next.sizeHelper(acc + 1);
    }
  }

  // This is not a sentinel
  boolean isSentinel() {
    return false;
  }

  // Removes this node from the deque
  T removeFrom() {
    this.prev.next = this.next;
    this.next.prev = this.prev;
    return data;
  }

  // Helps find the first node using the given predicate
  ANode<T> findHelper(IPred<T> p) {
    if (p.apply(this.data)) {
      return this;
    }
    else {
      return this.next.findHelper(p);
    }
  }

  // Helps remove this node
  void removeNodeHelper() {
    this.removeFrom();
  }
}

// Describes a sentinel
class Sentinel<T> extends ANode<T> {

  Sentinel() {
    super(null, null);
    prev = this;
    next = this;
  }

  // Helps determine the size of this deque
  int sizeHelper(int acc) {
    return acc;
  }

  // This is a sentinel
  boolean isSentinel() {
    return true;
  }

  // Cannot remove a sentinel
  T removeFrom() {
    throw new RuntimeException("Can't remove from an empty list");
  }

  // Helps find the first node using the given predicate
  ANode<T> findHelper(IPred<T> p) {
    return this;
  }

  // Helps remove this node
  void removeNodeHelper() {
    return;
  }

  // EFFECT: Add the node after the header
  void addAtHead(T t) {
    new Node<T>(t, this.next, this);
  }

  // EFFECT: Add the node before the header
  void addAtTail(T t) {
    new Node<T>(t, this, this.prev);
  }
}

// Examples and tests
class ExamplesDeque {
  Sentinel<String> sentinel1;
  Sentinel<String> sentinel2;
  Sentinel<String> sentinel3;
  Deque<String> deque1;
  Deque<String> deque2;
  Deque<String> deque3;
  ANode<String> node1;
  ANode<String> node2;
  ANode<String> node3;
  ANode<String> node4;
  ANode<String> node5;
  ANode<String> node6;
  ANode<String> node7;
  ANode<String> node8;
  ANode<String> node9;

  // Reset the data to default values
  void initData() {
    sentinel1 = new Sentinel<String>();
    sentinel2 = new Sentinel<String>();
    sentinel3 = new Sentinel<String>();

    // Empty list
    deque1 = new Deque<String>(sentinel1);
    deque2 = new Deque<String>(sentinel2);
    deque3 = new Deque<String>(sentinel3);

    // Nodes initally constructed with prev and next set to null
    node1 = new Node<String>("abc");
    node2 = new Node<String>("bcd");
    node3 = new Node<String>("cde");
    node4 = new Node<String>("def");

    node5 = new Node<String>("a");
    node6 = new Node<String>("c");
    node7 = new Node<String>("b");
    node8 = new Node<String>("e");
    node9 = new Node<String>("d");

    this.sentinel2.prev = node4;
    this.sentinel2.next = node1;
    this.node1.prev = sentinel2;
    this.node1.next = node2;
    this.node2.prev = node1;
    this.node2.next = node3;
    this.node3.prev = node2;
    this.node3.next = node4;
    this.node4.prev = node3;
    this.node4.next = sentinel2;

    this.sentinel3.prev = node9;
    this.sentinel3.next = node5;
    this.node5.prev = sentinel3;
    this.node5.next = node6;
    this.node6.prev = node5;
    this.node6.next = node7;
    this.node7.prev = node6;
    this.node7.next = node8;
    this.node8.prev = node7;
    this.node8.next = node9;
    this.node9.prev = node8;
    this.node9.next = sentinel3;
  }

  // Tests the size method
  void testSize(Tester t) {
    this.initData();

    t.checkExpect(deque1.size(), 0);
    t.checkExpect(deque2.size(), 4);
    t.checkExpect(deque3.size(), 5);
  }

  // Tests the addAtHead method
  void testAddAtHead(Tester t) {
    this.initData();

    Sentinel<String> sentinel4 = new Sentinel<String>();
    Deque<String> aAH1 = new Deque<String>(sentinel4);
    Node<String> node10 = new Node<String>("a");
    sentinel4.prev = node10;
    sentinel4.next = node10;
    node10.prev = sentinel4;
    node10.next = sentinel4;
    this.deque1.addAtHead("a");
    t.checkExpect(deque1, aAH1);

    Sentinel<String> sentinel5 = new Sentinel<String>();
    Deque<String> aAH2 = new Deque<String>(sentinel5);
    sentinel5.prev = node4;
    sentinel5.next = node10;
    node10.prev = sentinel5;
    node10.next = node1;
    node1.prev = node10;
    node1.next = node2;
    node2.prev = node1;
    node2.next = node3;
    node3.prev = node2;
    node3.next = node4;
    node4.prev = node3;
    node4.next = sentinel5;
    this.deque2.addAtHead("a");
    t.checkExpect(deque2, aAH2);
  }

  // Tests the addAtTail method
  void testAddAtTail(Tester t) {
    this.initData();

    Sentinel<String> sentinel6 = new Sentinel<String>();
    Deque<String> aAT1 = new Deque<String>(sentinel6);
    Node<String> node11 = new Node<String>("a");
    sentinel6.prev = node11;
    sentinel6.next = node11;
    node11.prev = sentinel6;
    node11.next = sentinel6;
    this.deque1.addAtTail("a");
    t.checkExpect(deque1, aAT1);

    Sentinel<String> sentinel7 = new Sentinel<String>();
    Deque<String> aAT2 = new Deque<String>(sentinel7);
    Node<String> node12 = new Node<String>("a");

    sentinel7.prev = node12;
    sentinel7.next = node1;
    node1.prev = sentinel7;
    node1.next = node2;
    node2.prev = node1;
    node2.next = node3;
    node3.prev = node2;
    node3.next = node4;
    node4.prev = node3;
    node4.next = node12;
    node12.prev = node4;
    node12.next = sentinel7;

    this.deque2.addAtTail("a");
    t.checkExpect(deque2, aAT2);
  }

  // Tests the removeFromHead method
  void testRemoveFromHead(Tester t) {
    this.initData();

    t.checkException(new RuntimeException("Can't remove from an empty list"),
            this.deque1, "removeFromHead");

    Sentinel<String> sentinel8 = new Sentinel<String>();
    Deque<String> rFH1 = new Deque<String>(sentinel8);
    sentinel8.prev = node4;
    sentinel8.next = node2;
    this.node2.prev = sentinel8;
    this.node2.next = node3;
    this.node3.prev = node2;
    this.node3.next = node4;
    this.node4.prev = node3;
    this.node4.next = sentinel8;
    this.deque2.removeFromHead();
    t.checkExpect(deque2, rFH1);
  }

  // Tests the removeFromTail method
  void testRemoveFromTail(Tester t) {
    this.initData();

    t.checkException(new RuntimeException("Can't remove from an empty list"),
            this.deque1, "removeFromTail");

    Sentinel<String> sentinel9 = new Sentinel<String>();
    Deque<String> rFT1 = new Deque<String>(sentinel9);
    sentinel9.prev = node3;
    sentinel9.next = node1;
    this.node1.prev = sentinel9;
    this.node1.next = node2;
    this.node2.prev = node1;
    this.node2.next = node3;
    this.node3.prev = node2;
    this.node3.next = sentinel9;
    this.deque2.removeFromTail();
    t.checkExpect(deque2, rFT1);
  }

  // Tests the find method
  void testFind(Tester t) {
    this.initData();
    Sentinel<String> sentinel10 = new Sentinel<String>();
    Deque<String> f1 = new Deque<String>(sentinel10);
    Node<String> node13 = new Node<String>("123456");
    sentinel10.prev = node13;
    sentinel10.next = node1;
    this.node1.prev = sentinel10;
    this.node1.next = node2;
    this.node2.prev = node1;
    this.node2.next = node3;
    this.node3.prev = node2;
    this.node3.next = node4;
    this.node4.prev = node3;
    this.node4.next = node13;
    node13.prev = node4;
    node13.next = sentinel10;
    t.checkExpect(f1.find(new DataStringLengthGreaterThan5()), node13);
    t.checkExpect(this.deque1.find(new DataStringLengthGreaterThan5()), sentinel1);

  }

  // Tests the find method
  void testFindHelper(Tester t) {
    this.initData();
    Sentinel<String> sentinel10 = new Sentinel<String>();
    Node<String> node13 = new Node<String>("123456");
    sentinel10.prev = node13;
    sentinel10.next = node1;
    this.node1.prev = sentinel10;
    this.node1.next = node2;
    this.node2.prev = node1;
    this.node2.next = node3;
    this.node3.prev = node2;
    this.node3.next = node4;
    this.node4.prev = node3;
    this.node4.next = node13;
    node13.prev = node4;
    node13.next = sentinel10;
    t.checkExpect(this.sentinel1.findHelper(new DataStringLengthGreaterThan5()), sentinel1);
    t.checkExpect(this.node1.findHelper(new DataStringLengthGreaterThan5()), node13);
  }

  // Tests the removeFrom method
  void testRemoveFrom(Tester t) {
    this.initData();

    t.checkException(new RuntimeException("Can't remove from an empty list"),
            this.sentinel1, "removeFrom");
    t.checkExpect(this.node4.removeFrom(), "def");
    this.node4.removeFrom();
    t.checkExpect(this.node3.next, sentinel2);
  }

  // Tests the isSentinel method
  void testIsSentinel(Tester t) {
    this.initData();

    t.checkExpect(sentinel1.isSentinel(), true);
    t.checkExpect(this.node4.isSentinel(), false);
  }

  // Tests the removeNode method
  void testRemoveNode(Tester t) {
    this.initData();
    Sentinel<String> sentinel11 = new Sentinel<String>();
    Deque<String> rN1 = new Deque<String>(sentinel11);
    rN1.removeNode(sentinel11);
    t.checkExpect(rN1, rN1);

    Sentinel<String> sentinel12 = new Sentinel<String>();
    Deque<String> rN2 = new Deque<String>(sentinel12);
    Node<String> node = new Node<String>("cde");
    sentinel12.prev = node4;
    sentinel12.next = node1;
    this.node1.prev = sentinel12;
    this.node1.next = node2;
    this.node2.prev = node1;
    this.node2.next = node;
    node.prev = node2;
    node.next = node4;
    this.node4.prev = node;
    this.node4.next = sentinel12;
    this.deque2.removeNode(node);
    t.checkExpect(this.deque2, rN2);
  }

  // Tests the removeNodeHelper methods
  void testRemoveNodeHelper(Tester t) {
    this.initData();
    this.sentinel1.removeNodeHelper();
    t.checkExpect(this.sentinel1, this.sentinel1);
    this.node4.removeNodeHelper();
    t.checkExpect(this.node3, node3);

  }
}