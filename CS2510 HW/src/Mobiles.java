import javalib.funworld.WorldScene;
import javalib.worldcanvas.WorldCanvas;
import javalib.worldimages.*;
import tester.Tester;

import java.awt.*;
// and predefined colors (Red, Green, Yellow, Blue, Black, White

interface IMobile {
  // Calculates the total weight of a mobile
  int totalWeight();

  // Calculates the total height of a mobile
  int totalHeight();

  // Checks if both sides of a mobile are balanced
  boolean isBalanced();

  // Builds a new mobile
  IMobile buildMobile(IMobile balanced, int dLength, int dStrutLength);

  // Calculates the width of the strut on left hand side
  int totalLeftSide();

  // Calculates the width of the strut on right hand side
  int totalRightSide();

  // Calculates total width of mobile
  int curWidth();

  // Produces an image of the mobile
  WorldImage drawMobile();

  // Factor to multiply by to get rendered drawing
  static int scaleUnits = 20;
}

// Describes a Simple
class Simple implements IMobile {
  int length;
  int weight;
  Color color;

  Simple(int l, int w, Color c) {
    this.length = l;
    this.weight = w;
    this.color = c;
  }

  // Calculates the total weight
  public int totalWeight() {
    return this.weight;
  }

  // Calculates the total height
  public int totalHeight() {
    return this.length + this.weight / 10;
  }

  // Checks if both sides of the mobile are balanced,
  // returns true because there is only one side
  public boolean isBalanced() {
    return true;
  }

  // Builds a new mobile
  public IMobile buildMobile(IMobile balanced, int dLength, int dStrutLength) {
    double ratio;
    int rs;
    int ls;

    if (this.totalWeight() < balanced.totalWeight()) {
      ratio = this.totalWeight() / balanced.totalWeight();
      ls = (int) (ratio * dStrutLength);
      rs = (int) ((1 - ratio) * dStrutLength);
    }
    else if (this.totalWeight() == balanced.totalWeight()) {
      ratio = .5;
      rs = (int) (ratio * dStrutLength);
      ls = (int) ((1 - ratio) * dStrutLength);// stop me if you can
    }

    else {
      ratio = balanced.totalWeight() / this.totalWeight();
      rs = (int) (ratio * dStrutLength);
      ls = (int) ((1 - ratio) * dStrutLength);
    }

    return new Complex(dLength, ls, rs, this, balanced);
  }

  // Calculates the width on the left hand side
  public int totalLeftSide() {
    double width = Math.ceil(this.weight / 10.0); // convert weight to width
    width /= 2.0; // half its width contributes to overall width
    return (int) width;
  }

  // Calculates the width on the right hand side
  public int totalRightSide() {
    double width = Math.ceil(this.weight / 10.0); // convert weight to width
    width /= 2.0; // half its width contributes to overall width
    return (int) width;
  }

  // Calculates total mobile width
  public int curWidth() {
    double width = Math.ceil(this.weight / 10.0); // convert weight to width
    return (int) width;
  }

  // Creates an image of the mobile
  public WorldImage drawMobile() {
    WorldImage vwire = (new VisiblePinholeImage(
            new RectangleImage(2, IMobile.scaleUnits * this.length, OutlineMode.SOLID, Color.BLACK)))
            .movePinhole(-1.25, 2 * 5 * this.length);
    WorldImage weight = (new VisiblePinholeImage(new RectangleImage(this.curWidth() * 10,
            (this.totalHeight() - this.length) * 10, OutlineMode.SOLID, this.color))).movePinhole(-1.25,
            -5 * (this.totalHeight() - this.length));
    WorldImage combo = new OverlayOffsetAlign(AlignModeX.PINHOLE, AlignModeY.PINHOLE, vwire, 0, 0,
            weight);
    combo = (new VisiblePinholeImage(combo).movePinhole(0, -this.length * IMobile.scaleUnits));
    WorldImage label = new TextImage(String.valueOf(this.weight), 12, Color.BLACK);

    // might need to change -20 to something relative to height of block
    combo = new OverlayOffsetAlign(AlignModeX.PINHOLE, AlignModeY.BOTTOM, label, 0,
            -IMobile.scaleUnits, combo);
    combo = (new VisiblePinholeImage(combo)
            .movePinhole(-combo.getWidth() / 2 + 10 * this.curWidth() / 2, -combo.getHeight() / 2));
    return combo;
  }
}

// Describes a Complex
class Complex implements IMobile {
  int length;
  int leftside;
  int rightside;
  IMobile left;
  IMobile right;

  Complex(int l, int ls, int rs, IMobile left, IMobile right) {
    this.length = l;
    this.leftside = ls;
    this.rightside = rs;
    this.left = left;
    this.right = right;
  }

  // Calculates the total weight of a mobile
  public int totalWeight() {
    return this.right.totalWeight() + this.left.totalWeight();
  }

  // Calculates the total height of the mobile
  public int totalHeight() {
    // return the length + max height
    if (this.right.totalHeight() > this.left.totalHeight()) {
      return this.right.totalHeight() + this.length;
    }
    return this.left.totalHeight() + this.length;
  }

  // Checks if both sides of the mobile are balanced
  public boolean isBalanced() {
    return this.right.totalWeight() * this.rightside == this.left.totalWeight() * this.leftside;
  }

  // Builds a new mobile
  public IMobile buildMobile(IMobile balanced, int dLength, int dStrutLength) {
    double ratio;
    int rs;
    int ls;

    if (this.totalWeight() < balanced.totalWeight()) {
      ratio = this.totalWeight() / ((double) balanced.totalWeight());
      // ls = ratio*rs
      // ls + rs = dLength
      // (ratio + 1) * rs = dLength
      ls = (int) (dStrutLength / (ratio + 1));
      rs = (int) (ratio * ls);
    }

    else if (this.totalWeight() == balanced.totalWeight()) {
      ratio = .5;
      rs = (int) (ratio * dStrutLength);
      ls = (int) ((1 - ratio) * dStrutLength);// stop me if you can
    }

    else {
      // rs = ratio*ls
      // ls + rs = dLength
      // (ratio+1)*ls = dLength
      // ls = dLength / (ratio + 1)
      ratio = balanced.totalWeight() / ((double) this.totalWeight());
      rs = (int) (dStrutLength / (ratio + 1));
      ls = (int) (ratio * rs);
    }
    return new Complex(dLength, ls, rs, this, balanced);
  }

  // Calculates total mobile width
  public int curWidth() {
    return this.totalLeftSide() + this.totalRightSide();
  }

  // Calculates the width on the left hand side
  public int totalLeftSide() {
    return Math.max(this.leftside + this.left.totalLeftSide(),
            this.right.totalLeftSide() - this.rightside);
  }

  // Calculates the width on the right hand side
  public int totalRightSide() {
    return Math.max(this.rightside + this.right.totalRightSide(),
            this.left.totalRightSide() - this.leftside);
  }

  // Creates an image for the mobile
  public WorldImage drawMobile() {
    WorldImage vwire = (new VisiblePinholeImage(
            new RectangleImage(2, IMobile.scaleUnits * this.length, OutlineMode.SOLID, Color.BLACK)))
            .movePinhole(0, 2 * 5 * this.length);
    WorldImage leftHWire = (new VisiblePinholeImage(
            new RectangleImage(IMobile.scaleUnits * this.leftside, 2, OutlineMode.SOLID, Color.BLACK)))
            .movePinhole(2 * 5 * this.leftside, 0);
    WorldImage rightHWire = (new VisiblePinholeImage(
            new RectangleImage(IMobile.scaleUnits * this.rightside, 2, OutlineMode.SOLID, Color.BLACK)))
            .movePinhole(-2 * 5 * this.rightside, 0);

    WorldImage firstCombo = new OverlayOffsetAlign(AlignModeX.PINHOLE, AlignModeY.PINHOLE,
            leftHWire, 0, 0, vwire);

    WorldImage secondCombo = new OverlayOffsetAlign(AlignModeX.PINHOLE, AlignModeY.PINHOLE,
            rightHWire, 0, 0, firstCombo);
    secondCombo = new VisiblePinholeImage(secondCombo).movePinhole(0,
            -IMobile.scaleUnits * this.length);

    WorldImage leftMobileImg = this.left.drawMobile();
    WorldImage rightMobileImg = this.right.drawMobile();
    secondCombo = new OverlayOffsetAlign(AlignModeX.PINHOLE, AlignModeY.PINHOLE, secondCombo,
            -IMobile.scaleUnits * this.leftside, IMobile.scaleUnits * this.length, leftMobileImg);

    secondCombo = new VisiblePinholeImage(secondCombo).movePinhole(-secondCombo.getWidth() / 2,
            -secondCombo.getHeight() / 2);
    secondCombo = new VisiblePinholeImage(secondCombo)
            .movePinhole(10 * this.left.curWidth() / 2 + this.leftside * IMobile.scaleUnits, 0);

    secondCombo = new OverlayOffsetAlign(AlignModeX.PINHOLE, AlignModeY.PINHOLE, secondCombo,
            IMobile.scaleUnits * this.rightside, IMobile.scaleUnits * this.length, rightMobileImg);

    secondCombo = new VisiblePinholeImage(secondCombo).movePinhole(-secondCombo.getWidth() / 2,
            -secondCombo.getHeight() / 2);
    secondCombo = new VisiblePinholeImage(secondCombo)
            .movePinhole(10 * this.left.curWidth() / 2 + this.leftside * IMobile.scaleUnits, 0);

    return secondCombo;
  }
}

class ExamplesMobiles {
  // simple
  IMobile exampleSimple = new Simple(2, 20, Color.BLUE); // given example
  // complex of a simple and simple
  IMobile exampleComplex2 = new Complex(2, 5, 3, new Simple(2, 36, Color.RED),
          new Simple(1, 60, Color.GREEN));
  // complex of a simple and complex
  IMobile exampleComplex1 = new Complex(2, 8, 1, new Simple(1, 12, Color.RED), exampleComplex2);
  // complex of a simple and complex
  IMobile exampleComplex = new Complex(1, 9, 3, new Simple(1, 36, Color.BLUE), exampleComplex1);
  // complex of 2 complexes
  IMobile exampleComplex3 = new Complex(5, 10, 4, exampleComplex, exampleComplex2);

  // Tests for totalWeight
  boolean testTotalWeight(Tester t) {
    return t.checkExpect(exampleSimple.totalWeight(), 20)
            && t.checkExpect(exampleComplex.totalWeight(), 144)
            && t.checkExpect(exampleComplex1.totalWeight(), 108)
            && t.checkExpect(exampleComplex2.totalWeight(), 96)
            && t.checkExpect(exampleComplex3.totalWeight(), 240);
  }

  // Tests for totalHeight
  boolean testTotalHeight(Tester t) {
    return t.checkExpect(exampleSimple.totalHeight(), 4)
            && t.checkExpect(exampleComplex.totalHeight(), 12)
            && t.checkExpect(exampleComplex1.totalHeight(), 11)
            && t.checkExpect(exampleComplex2.totalHeight(), 9)
            && t.checkExpect(exampleComplex3.totalHeight(), 17);
  }

  // Tests for isBalanced
  boolean testIsBalanced(Tester t) {
    return t.checkExpect(this.exampleSimple.isBalanced(), true)
            && t.checkExpect(this.exampleComplex.isBalanced(), true)
            && t.checkExpect(this.exampleComplex1.isBalanced(), true)
            && t.checkExpect(this.exampleComplex2.isBalanced(), true)
            && t.checkExpect(this.exampleComplex3.isBalanced(), false);
  }

  // Tests for buildMobile
  boolean testBuildMobile(Tester t) {
    return t.checkExpect(this.exampleComplex.buildMobile(this.exampleComplex, 2, 20),
            new Complex(2, 10, 10, this.exampleComplex, this.exampleComplex))
            && t.checkExpect(this.exampleSimple.buildMobile(this.exampleSimple, 4, 50),
            new Complex(4, 25, 25, this.exampleSimple, this.exampleSimple))
            && t.checkExpect(this.exampleComplex3.buildMobile(this.exampleComplex2, 5, 60),
            new Complex(5, 16, 42, this.exampleComplex3, this.exampleComplex2));
  }

  // Tests for curWidth
  boolean testCurWidth(Tester t) {
    return t.checkExpect(this.exampleComplex.curWidth(), 21)
            && t.checkExpect(this.exampleSimple.curWidth(), 2)
            && t.checkExpect(this.exampleComplex1.curWidth(), 16)
            && t.checkExpect(this.exampleComplex2.curWidth(), 13)
            && t.checkExpect(this.exampleComplex3.curWidth(), 31);
  }

  // Tests for drawMobile
  boolean testDrawMobile(Tester t) {
    int totalWidth = 600;
    int totalHeight = 600;
    WorldCanvas c = new WorldCanvas(totalWidth, totalHeight);
    WorldScene s = new WorldScene(totalWidth, totalHeight);
    WorldImage img = this.exampleComplex.drawMobile();
    s = s.placeImageXY(img, totalWidth / 2, totalHeight / 2);
    return c.drawScene(s) && c.show();
  }

  // Tests for totalLeftSide
  boolean testTotalLeftSide(Tester t) {
    return t.checkExpect(this.exampleComplex.totalLeftSide(), 11)
            && t.checkExpect(this.exampleSimple.totalLeftSide(), 1)
            && t.checkExpect(this.exampleComplex1.totalLeftSide(), 9)
            && t.checkExpect(this.exampleComplex2.totalLeftSide(), 7)
            && t.checkExpect(this.exampleComplex3.totalLeftSide(), 21);
  }

  // Tests for totalLeftSide
  boolean testTotalRightSide(Tester t) {
    return t.checkExpect(this.exampleComplex.totalRightSide(), 10)
            && t.checkExpect(this.exampleSimple.totalRightSide(), 1)
            && t.checkExpect(this.exampleComplex1.totalRightSide(), 7)
            && t.checkExpect(this.exampleComplex2.totalRightSide(), 6)
            && t.checkExpect(this.exampleComplex3.totalRightSide(), 10);
  }
}
