import tester.*;

// a class that represents an ABST
abstract class ABST<T> {
  IComparator<T> order;

  ABST(IComparator<T> order) {
    this.order = order;
  }

  /*
  Fields:
  this.order ... IComparator<T>
  Methods:
  insert ... ABST<T>
  getLeftMost ... T
  isLeaf ... boolean
  getRight ... ABST<T>
  sameTree ... boolean
  sameTreeHelperLeaf ... boolean
  sameTreeHelperNode ... boolean
  sameData ... boolean
  sameDataForLeaf ... boolean
  sameDataForNode ... boolean
  buildList ... IList<T>
  sameAsList ... boolean
  Methods for Fields:
  sameTree(that) ... Leaf<T>
  sameTree(that) ... Node<T>
  sameData(that) ... Leaf<T>
  sameData(that) ... Node<T>
   */

  // inserts the data into the correct node of the ABST<T>
  public abstract ABST<T> insert(T t);

  // gets the left-most item from the tree
  public abstract T getLeftMost();

  // determines if this is a leaf
  public boolean isLeaf() {
    return false;
  }

  // Builds a new BST without the leftmost item
  public abstract ABST<T> getRight();

  // determines if this tree is the same as given
  public abstract boolean sameTree(ABST<T> that);

  // helper for sameTree in the Leaf class
  public boolean sameTreeHelperLeaf(Leaf<T> that) {
    return false;
  }

  // helper for sameTree in the Node class
  public boolean sameTreeHelperNode(Node<T> that) {
    return false;
  }

  // determines whether this binary search tree has the same books
  // in the same order as the given tree
  abstract boolean sameData(ABST<T> that);

  // determines if this BST has the same books as the given leaf
  boolean sameDataForLeaf(Leaf<T> that) {
    return false;
  }

  // determines if this tree has the same data as the given node
  abstract boolean sameDataForNode(Node<T> t);

  // given a list, inserts item from this tree into the given list
  abstract IList<T> buildList(IList<T> list);

  // checks if this BST of books is the same as the given list of books
  abstract boolean sameAsList(IList<T> list);
}

// An IComparator is any class that can be used for comparing items
interface IComparator<T> {
  // Checks which item comes before
  boolean comesBefore(T t1, T t2);

  // Checks if this is the same as that
  boolean same(T t1, T t2);
}

// Describes a leaf
class Leaf<T> extends ABST<T> {

  Leaf(IComparator<T> order) {
    super(order);
  }

  /*
  Fields:
  this.order ... IComparator<T>
  Methods:
  insert ... ABST<T>
  getLeftMost ... T
  isLeaf ... boolean
  getRight ... ABST<T>
  sameTree ... boolean
  sameTreeHelperLeaf ... boolean
  sameData ... boolean
  sameDataForLeaf ... boolean
  sameDataForNode ... boolean
  buildList ... IList<T>
  sameAsList ... boolean
  Methods for Fields:
  sameTree(that) ... Leaf<T>
  sameTree(that) ... Node<T>
  sameData(that) ... Leaf<T>
  sameData(that) ... Node<T>
   */

  // Inserts an item into a BST
  public ABST<T> insert(T t) {
    return new Node<T>(this.order, t, this, this);
  }

  // Gets the leftmost item in a BST
  public T getLeftMost() {
    throw new RuntimeException("No leftmost item of an empty tree");
  }

  // Checks whether this ABST is a leaf
  public boolean isLeaf() {
    return true;
  }

  // Gets all items in a BST aside from the leftmost
  public ABST<T> getRight() {
    throw new RuntimeException("No right of an empty tree");
  }

  // Checks if this tree is the same as that tree
  public boolean sameTree(ABST<T> that) {
    return that.sameTreeHelperLeaf(this);
  }

  // Helper for sameTree and leaf
  public boolean sameTreeHelperLeaf(Leaf<T> that) {
    return true;
  }

  // Checks if the data in this ABST is the same as the data in that ABST
  boolean sameData(ABST<T> that) {
    return that.sameDataForLeaf(this);
  }

  // Checks if the data for this leaf is the same as that leaf
  boolean sameDataForLeaf(Leaf<T> that) {
    return true;
  }

  // Checks if the data for this node is the same as that node
  boolean sameDataForNode(Node<T> t) {
    return false;
  }

  // Takes the items from this ABST and inserts them into the given list
  IList<T> buildList(IList<T> l) {
    return l;
  }

  // Checks if the items in this ABST is the same as the given list
  boolean sameAsList(IList<T> l) {
    return this.buildList(new MtList<T>()).sameAsListHelper(l, this.order);
  }

}

// Describes a node
class Node<T> extends ABST<T> {
  T data;
  ABST<T> left;
  ABST<T> right;

  Node(IComparator<T> order, T data, ABST<T> left, ABST<T> right) {
    super(order);
    this.data = data;
    this.left = left;
    this.right = right;
  }

  /*
  Fields:
  this.order ... IComparator<T>
  this.data ... T
  this.left ... ABST<T>
  this.right ... ABST<T>
  Methods:
  insert ... ABST<T>
  getLeftMost ... T
  isLeftMost ... boolean
  getRight ... ABST<T>
  sameTree ... boolean
  sameTreeHelperNode ... boolean
  sameData ... boolean
  sameDataForNode ... boolean
  buildList ... IList<T>
  sameAsList ... boolean
  Methods for Fields:
  sameTree(that) ... Leaf<T>
  sameTree(that) ... Node<T>
  sameData(that) ... Leaf<T>
  sameData(that) ... Node<T>
   */

  // Inserts an item into a BST
  public ABST<T> insert(T t) {
    if (this.order.comesBefore(t, this.data)) {
      return new Node<T>(this.order, this.data, this.left.insert(t), this.right);
    }
    else {
      return new Node<T>(this.order, this.data, this.left, this.right.insert(t));
    }
  }

  // Gets the leftmost item in a BST
  public T getLeftMost() {
    if (this.isLeftMost()) {
      return this.data;
    }
    else {
      return this.left.getLeftMost();
    }
  }

  // Checks whether this item is the leftmost item of the ABST
  boolean isLeftMost() {
    return this.left.isLeaf();
  }

  // Gets all items in a BST aside from the leftmost
  public ABST<T> getRight() {
    if (this.isLeftMost()) {
      return new Leaf(this.order);
    }
    else {
      return new Node(this.order, this.data, this.left.getRight(), this.right);
    }
  }

  // Checks if this tree is the same as that tree
  public boolean sameTree(ABST<T> that) {
    return that.sameTreeHelperNode(this);
  }

  // Helper for sameTree and node
  public boolean sameTreeHelperNode(Node<T> that) {
    return this.order.same(this.data, that.data) && this.left.sameTree(that.left)
            && this.right.sameTree(that.right);
  }

  // Checks if the data in this ABST is the same as the data in that ABST
  public boolean sameData(ABST<T> that) {
    return that.sameDataForNode(this);
  }

  // Checks if the data for this node is the same as that node
  public boolean sameDataForNode(Node<T> that) {
    return this.order.same(this.getLeftMost(), that.getLeftMost())
            && this.getRight().sameData(that.getRight());
  }

  // Takes the items from this ABST and inserts them into the given list
  IList<T> buildList(IList<T> list) {
    return this.right.buildList(new ConsList<T>(this.data, this.left.buildList(list)));
  }

  // Checks if the items in this ABST is the same as the given list
  public boolean sameAsList(IList<T> l) {
    return this.buildList(new MtList<T>()).sameAsListHelper(l, this.order);
  }
}

// Describes a book
class Book {
  String title;
  String author;
  int price;

  Book(String title, String author, int price) {
    this.title = title;
    this.author = author;
    this.price = price;
  }

  /*
  Fields:
  this.title ... String
  this.author ... String
  this.price ... int
   */
}

// Describes IComparator Same
abstract class Same implements IComparator<Book> {
  // determines if all elements of b1 are the same for all elements in b2
  public boolean same(Book b1, Book b2) {
    return b1.title.equals(b2.title)
            && b1.author.equals(b2.author)
            && b1.price == b2.price;
  }
}

// Describes IComparator BooksByAuthor
class BooksByAuthor extends Same {
  public boolean comesBefore(Book b1, Book b2) {
    return (b1.author.compareTo(b2.author)) < 0;
  }

  /*
  Methods:
  comesBefore ... boolean
  Methods for Fields:
  comesBefore ... (Book, Book)
   */

}

//to represent a comparator that orders books by title
class BooksByTitle extends Same {
  public boolean comesBefore(Book b1, Book b2) {
    return (b1.title.compareTo(b2.title)) < 0;
  }

  /*
  Methods:
  comesBefore ... boolean
  Methods for Fields:
  comesBefore ... (Book, Book)
   */

}

//to represent a comparator that orders books by price
class BooksByPrice extends Same {
  public boolean comesBefore(Book b1, Book b2) {
    return b1.price < b2.price;
  }

  /*
  Methods:
  comesBefore ... boolean
  Methods for Fields:
  comesBefore ... (Book, Book)
   */

}

// An IList<T> is one of
// - MtList<T>
// - ConsList<T>
interface IList<T> {
  // inserts the items from this list into the given tree
  ABST<T> buildTree(ABST<T> t);

  // Helper for if this list is the same as that list
  boolean sameAsListHelper(IList<T> that, IComparator<T> comp);

  // Checks if this empty list is the same as that empty list
  boolean sameEmpty(MtList<T> other, IComparator<T> comp);

  // Checks if this non-empty list is the same as that empty list
  boolean sameNonEmptyList(ConsList<T> l, IComparator<T> c);
}

// Describes an MtList
class MtList<T> implements IList<T> {

  public ABST<T> buildTree(ABST<T> t) {
    return t;
  }

  /*
  Methods:
  sameAsListHelper ... boolean
  sameEmpty ... boolean
  sameNonEmptyList ... boolean
   */

  // Helper for if this list is the same as that list
  public boolean sameAsListHelper(IList<T> that, IComparator<T> comp) {
    return that.sameEmpty(this, comp);
  }

  // Checks if this empty list is the same as that empty list
  public boolean sameEmpty(MtList<T> other, IComparator<T> comp) {
    return true;
  }

  // Checks if this non-empty list is the same as that empty list
  public boolean sameNonEmptyList(ConsList<T> other, IComparator<T> comp) {
    return false;
  }
}

// Describes a ConsList
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
  Methods:
  buildTree ... ABST<T>
  sameAsListHelper ... boolean
  sameEmpty ... boolean
  sameNonEmptyList ... boolean
   */

  // Builds a BST with this list and the given ABST
  public ABST<T> buildTree(ABST<T> t) {
    return this.rest.buildTree(t.insert(this.first));
  }

  // Helper for if this list is the same as that list
  public boolean sameAsListHelper(IList<T> that, IComparator<T> comp) {
    return that.sameNonEmptyList(this, comp);
  }

  // Checks if this empty list is the same as that empty list
  public boolean sameEmpty(MtList<T> that, IComparator<T> comp) {
    return false;
  }

  // Checks if this non-empty list is the same as that empty list
  public boolean sameNonEmptyList(ConsList<T> l, IComparator<T> c) {
    return c.comesBefore(this.first, l.first) && this.rest.sameAsListHelper(l.rest, c);
  }
}


class ExamplesBook {
  Book Percy = new Book("Percy Jackson", "Rick Riordan", 11);
  Book Harry = new Book("Harry Potter", "JK Rowling", 10);
  Book Matched = new Book("Matched", "Sarah", 4);
  Book idk = new Book("idk", "idk", 0);
  Book abba = new Book("abba", "john", 5);
  Book john = new Book("john", "smith", 3);
  IComparator<Book> byAuthor = new BooksByAuthor();
  IComparator<Book> byTitle = new BooksByTitle();
  IComparator<Book> byPrice = new BooksByPrice();
  Leaf<Book> leaf1 = new Leaf<Book>(byPrice);
  IList<Book> mt = new MtList<Book>();


  Node<Book> node1 = new Node<Book>(byPrice, Percy, new Leaf<Book>(byPrice),
          new Leaf<Book>(byPrice));
  Node<Book> node2 = new Node<Book>(byPrice, Harry, new Leaf<Book>(byPrice),
          new Leaf<Book>(byPrice));
  Node<Book> node3 = new Node<Book>(byPrice, Matched, new Leaf<Book>(byPrice),
          new Leaf<Book>(byPrice));
  Node<Book> node4 = new Node<Book>(byPrice, idk, new Leaf<Book>(byPrice),
          new Leaf<Book>(byPrice));
  Node<Book> node5 = new Node<Book>(byPrice, abba, new Leaf<Book>(byPrice),
          new Leaf<Book>(byPrice));
  Node<Book> node6 = new Node<Book>(byPrice, john, new Leaf<Book>(byPrice),
          new Leaf<Book>(byPrice));
  Node<Book> node7 = new Node<Book>(byPrice, Harry, new Leaf<Book>(byPrice), node1);

  Node<Book> tree1 = new Node<Book>(byPrice, Harry, node3, node1);
  Node<Book> tree1SameData1 = new Node<Book>(byPrice, Matched, new Leaf<Book>(byPrice), node7);
  IList<Book> lob1 = new ConsList<Book>(Matched,
          new ConsList<Book>(Harry,
                  new ConsList<Book>(Percy, mt)));
  IList<Book> lob1rev = new ConsList<Book>(Percy,
          new ConsList<Book>(Harry,
                  new ConsList<Book>(Matched, mt)));
  Node<Book> tree2 = new Node<Book>(byPrice, Harry, new Leaf<Book>(byPrice), node1);
  IList<Book> lob2 = new ConsList<Book>(Harry, new ConsList<Book>(Percy, new MtList<Book>()));
  Node<Book> insertTree1 = new Node<Book>(byPrice, Harry, new Node<Book>(byPrice, Matched, node4,
          new Leaf<Book>(byPrice)), node1);
  Node<Book> tree1SameData2 = new Node<Book>(byPrice, Percy, node2, new Leaf<Book>(byPrice));
  Node<Book> tree1BT = new Node<Book>(byPrice, Matched, new Leaf<Book>(byPrice),
          new Node<Book>(byPrice, Harry, new Leaf<Book>(byPrice),
                  new Node<Book>(byPrice, Percy, new Leaf<Book>(byPrice),
                          new Leaf<Book>(byPrice))));
  Node<Book> tree2BT = new Node<Book>(byPrice, Harry, node3,
          new Node<Book>(byPrice, Harry,
                  new Leaf<Book>(byPrice),
                  new Node<Book>(byPrice, Percy,
                          new Leaf<Book>(byPrice),
                          node1)));
  IList<Book> lobBL1 = new ConsList<Book>(Matched,
          new ConsList<Book>(Harry,
                  new ConsList<Book>(Percy, new ConsList<Book>(Matched,
                          new ConsList<Book>(Harry,
                                  new ConsList<Book>(Percy, mt))))));
  IList<Book> lobBL2 = new ConsList<Book>(Matched,
          new ConsList<Book>(Harry,
                  new ConsList<Book>(Harry, new ConsList<Book>(Percy,
                          new ConsList<Book>(Percy, mt)))));


  // tests the method insert
  boolean testInsert(Tester t) {
    return t.checkExpect(tree1.insert(idk), insertTree1);
  }

  // tests the method getLeftMost
  boolean testGetLeftmost(Tester t) {
    return t.checkExpect(tree1.getLeftMost(), Matched)
            && t.checkExpect(tree2.getLeftMost(), Harry);
  }

  // Tests the expection for getLeftMost
  boolean testBadGetLeftMost(Tester t) {
    return t.checkException(new RuntimeException("No leftmost item of an empty tree"), this.leaf1,
            "getLeftMost");
  }

  // tests the method getRight
  boolean testGetRight(Tester t) {
    return t.checkExpect(tree1.getRight(),
            new Node<Book>(byPrice, Harry, new Leaf<Book>(byPrice), node1));
  }

  // Tests the exception for getRight
  boolean testBadGetRight(Tester t) {
    return t.checkException(new RuntimeException("No right of an empty tree"), this.leaf1,
            "getRight");
  }

  // tests the method sameTree
  boolean testSameTree(Tester t) {
    return t.checkExpect(leaf1.sameTree(node1), false)
            && t.checkExpect(leaf1.sameTree(leaf1), true)
            && t.checkExpect(node1.sameTree(leaf1), false)
            && t.checkExpect(node1.sameTree(node1), true)
            && t.checkExpect(tree1.sameTree(tree1), true);
  }

  // tests the method sameData
  boolean testSameData(Tester t) {
    return t.checkExpect(leaf1.sameData(leaf1), true)
            && t.checkExpect(leaf1.sameData(node1), false)
            && t.checkExpect(node1.sameData(node1), true)
            && t.checkExpect(tree1.sameData(tree1SameData1), true);
  }

  // tests the method buildTree
  boolean testBuildTree(Tester t) {
    return t.checkExpect(mt.buildTree(leaf1), new Leaf<Book>(byPrice))
            && t.checkExpect(lob1.buildTree(leaf1), tree1BT)
            && t.checkExpect(lob2.buildTree(tree1), tree2BT);
  }

  // tests the method buildList
  boolean testBuildList(Tester t) {
    return t.checkExpect(leaf1.buildList(lob1), lob1)
            && t.checkExpect(tree1.buildList(lob1), lobBL1)
            && t.checkExpect(tree1.buildList(lob2), lobBL2);
  }

  // tests the method sameAsList
  boolean testSameAsList(Tester t) {
    return t.checkExpect(tree1.sameAsList(lob1), true)
            && t.checkExpect(tree2.sameAsList(lob2), true)
            && t.checkExpect(leaf1.sameAsList(mt), true)
            && t.checkExpect(leaf1.sameAsList(lob1), false);
  }

  boolean finalTest(Tester t) {
    return t.checkExpect(lob1.buildTree(this.leaf1).buildList(this.mt), this.lob1rev);
  }

}