import tester.Tester;

import java.awt.*;

//an IPaint is one of
// - Solid
// - Combo
interface IPaint {
  //Compute the final color of an IPaint
  Color getFinalColor();

  //Count the number of times a Solid is used in a recipe
  int countPaints();

  //Count the number of times a paint gets mixed
  int countMixes();

  //Count the formula depth of a paint
  int formulaDepth();

  //Takes a depth and produces a string that represents the content formula of the paint
  String mixingFormula(int depth);
}

//describes a Solid
class Solid implements IPaint {
  String name;
  Color color;

  Solid(String name, Color color) {
    this.name = name;
    this.color = color;
  }

  /*
  Fields:
  this.name ... String
  this.color ... Color
  Methods:
  getFinalColor ... Color
  countPaints ... int
  countMixes ... int
  formulaDepth ... int
  mixingFormula ... String
  */

  //Returns the final color of this solid
  public Color getFinalColor() {
    return new Color(
            this.color.getRed(),
            this.color.getGreen(),
            this.color.getBlue());
  }

  //counts the number of times a solid is used
  public int countPaints() {
    return 1;
  }

  //does not count a solid as a mix
  public int countMixes() {
    return 0;
  }

  //solids do not count as a formula
  public int formulaDepth() {
    return 0;
  }

  //Returns the name of the solid
  public String mixingFormula(int depth) {
    return this.name;
  }
}

//descibes a combo
class Combo implements IPaint {
  String name;
  IMixture operation;

  Combo(String name, IMixture operation) {
    this.name = name;
    this.operation = operation;
  }

  /*
  Fields:
  this.name ... String
  this.operation ... IMixutre
  Methods:
  getFinalColor ... Color
  countPaints ... int
  countMixes ... int
  formulaDepth ... int
  mixingFormula ... String
  Methods for Fields:
  this.operation.computerColor()
  this.operation.countOperations()
  this.operation.countMixOperations()
  this.operation.operationDepth()
  this.operation.getFormula(int depth)
 */

  //Returns the final color of this combo
  public Color getFinalColor() {
    return this.operation.computeColor();
  }

  //Counts the paints used in this combo
  public int countPaints() {
    return this.operation.countOperations();
  }

  //Counts each combo as a mix
  public int countMixes() {
    return this.operation.countMixOperations();
  }

  //Counts how many combos deep a paint is
  public int formulaDepth() {
    return 1 + this.operation.operationDepth();
  }

  //Returns the name of this combo or the next operation
  public String mixingFormula(int depth) {
    if (depth == 0) {
      return this.name;
    }
    else {
      return this.operation.getFormula(depth);
    }
  }
}

//An IMixture is one of
// - a Blend
// - a Brighten
// - a Darken
interface IMixture {
  //Computes the color from an operation
  Color computeColor();

  //Counts a Brighten or Darken as a paint
  int countOperations();

  //Counts an IMixture as a mix
  int countMixOperations();

  //Counts every operation as one layer
  int operationDepth();

  //Gets the formula of this operation
  String getFormula(int depth);
}

//describes a Blend
class Blend implements IMixture {
  IPaint paint1;
  IPaint paint2;

  Blend(IPaint paint1, IPaint paint2) {
    this.paint1 = paint1;
    this.paint2 = paint2;
  }

  /*
  Fields:
  this.paint1 ... IPaint
  this.paint2 ... IPaint
  Methods:
  computeColor ... Color
  mixColors ... Color
  countOperations ... int
  countMixOperations ... int
  operationDepth ... int
  getFormula ... String
  Methods for Fields:
  this.paint1.countPaints()
  this.paint2.countPaints()
  */

  //Computes the final color for this Combo
  public Color computeColor() {
    return mixColors(this.paint1, this.paint2);
  }

  Color mixColors(IPaint color1, IPaint color2) {
    return new Color(
            (color1.getFinalColor().getRed() / 2) + (color2.getFinalColor().getRed() / 2),
            (color1.getFinalColor().getGreen() / 2) + (color2.getFinalColor().getGreen() / 2),
            (color1.getFinalColor().getBlue() / 2) + (color2.getFinalColor().getBlue() / 2)
    );
  }

  //Does not count a blend as a paint
  public int countOperations() {
    return this.paint1.countPaints() + this.paint2.countPaints();
  }

  //Counts a blend as a mix
  public int countMixOperations() {
    return 1 + this.paint1.countMixes() + this.paint2.countMixes();
  }

  //Counts a blend as a layer for formulaDepth
  public int operationDepth() {
    return this.paint1.formulaDepth();
  }

  //adds blend to the formula
  public String getFormula(int depth) {
    return "blend(" + this.paint1.mixingFormula(depth - 1) + ", "
            + this.paint2.mixingFormula(depth - 1) + ")";
  }
}

//describes a Brighten
class Brighten implements IMixture {
  IPaint paint;

  Brighten(IPaint paint) {
    this.paint = paint;
  }

  /*
  Fields:
  this.paint ... IPaint
  Methods:
  makeBright ... IPaint
  countOperations ... int
  countMixOperations ... int
  operationDepth ... int
  getFormula ... String
  */

  //Brightens this Combo
  public Color computeColor() {
    return paint.getFinalColor().brighter();
  }

  //Counts a Brighten as a paint
  public int countOperations() {
    return 1 + this.paint.countPaints();
  }

  //Counts a Brighten as a mix
  public int countMixOperations() {
    return 1 + this.paint.countMixes();
  }

  //Counts a Brighten as a layer
  public int operationDepth() {
    return this.paint.formulaDepth();
  }

  //Adds the brighten to the formula
  public String getFormula(int depth) {
    return "brighten(" + this.paint.mixingFormula(depth - 1) + ")";
  }
}

//describes a Darken
class Darken implements IMixture {
  IPaint paint;

  Darken(IPaint paint) {
    this.paint = paint;
  }

  /*
  Fields:
  this.paint ... IPaint
  Methods:
  makeDark ... IPaint
  countOperations ... int
  countMixOperations ... int
  operationDepth ... int
  getFormula ... String
  */

  //Darkens this Combo
  public Color computeColor() {
    return paint.getFinalColor().darker();
  }

  //Counts a darken as a paint
  public int countOperations() {
    return 1 + this.paint.countPaints();
  }

  //Counts a Darken as a mix
  public int countMixOperations() {
    return 1 + this.paint.countMixes();
  }

  //Counts a Darken as a layer
  public int operationDepth() {
    return this.paint.formulaDepth();
  }

  //Adds darken to the formula
  public String getFormula(int depth) {
    return "darken(" + this.paint.mixingFormula(depth - 1) + ")";
  }
}

//examples and tests
class ExamplesPaint {
  IPaint red = new Solid("red", new Color(255, 0, 0));
  IPaint green = new Solid("green", new Color(0, 255, 0));
  IPaint blue = new Solid("blue", new Color(0, 0, 255));

  IPaint purple = new Combo("purple", new Blend(this.red, this.blue));
  IPaint darkPurple = new Combo("dark purple", new Darken(this.purple));
  IPaint khaki = new Combo("khaki", new Blend(this.red, this.green));
  IPaint yellow = new Combo("yellow", new Brighten(this.khaki));
  IPaint mauve = new Combo("mauve", new Blend(this.purple, this.khaki));
  IPaint pink = new Combo("pink", new Brighten(this.mauve));
  IPaint coral = new Combo("coral", new Blend(this.pink, this.khaki));

  boolean testIPaintExample(Tester t) {
    return t.checkExpect(this.red.getFinalColor(), new Color(255, 0, 0))
            && t.checkExpect(this.green.getFinalColor(), new Color(0, 255, 0))
            && t.checkExpect(this.blue.getFinalColor(), new Color(0, 0, 255));
  }

  boolean testGetFinalColor(Tester t) {
    return t.checkExpect(this.purple.getFinalColor(), new Color(127, 0, 127))
            && t.checkExpect(this.darkPurple.getFinalColor(), new Color(
            this.purple.getFinalColor().getRed(),
            this.purple.getFinalColor().getGreen(),
            this.purple.getFinalColor().getBlue()).darker())
            && t.checkExpect(this.khaki.getFinalColor(), new Color(127, 127, 0))
            && t.checkExpect(this.yellow.getFinalColor(), new Color(
            this.khaki.getFinalColor().getRed(),
            this.khaki.getFinalColor().getGreen(),
            this.khaki.getFinalColor().getBlue()).brighter())
            && t.checkExpect(this.mauve.getFinalColor(), new Color(126, 63, 63))
            && t.checkExpect(this.pink.getFinalColor(), new Color(
            this.mauve.getFinalColor().getRed(),
            this.mauve.getFinalColor().getGreen(),
            this.mauve.getFinalColor().getBlue()).brighter()
    );
  }

  boolean testCountPaints(Tester t) {
    return t.checkExpect(this.purple.countPaints(), 2)
            && t.checkExpect(this.darkPurple.countPaints(), 3)
            && t.checkExpect(this.khaki.countPaints(), 2)
            && t.checkExpect(this.yellow.countPaints(), 3)
            && t.checkExpect(this.mauve.countPaints(), 4)
            && t.checkExpect(this.pink.countPaints(), 5)
            && t.checkExpect(this.coral.countPaints(), 7);
  }

  boolean testCountMixes(Tester t) {
    return t.checkExpect(this.purple.countMixes(), 1)
            & t.checkExpect(this.darkPurple.countMixes(), 2)
            && t.checkExpect(this.khaki.countMixes(), 1)
            && t.checkExpect(this.yellow.countMixes(), 2)
            && t.checkExpect(this.mauve.countMixes(), 3)
            && t.checkExpect(this.pink.countMixes(), 4)
            && t.checkExpect(this.coral.countMixes(), 6);
  }

  boolean testFormulaDepth(Tester t) {
    return t.checkExpect(this.purple.formulaDepth(), 1)
            && t.checkExpect(this.darkPurple.formulaDepth(), 2)
            && t.checkExpect(this.khaki.formulaDepth(), 1)
            & t.checkExpect(this.yellow.formulaDepth(), 2)
            && t.checkExpect(this.mauve.formulaDepth(), 2)
            && t.checkExpect(this.pink.formulaDepth(), 3)
            && t.checkExpect(this.coral.formulaDepth(), 4);
  }

  boolean testMixingFormula(Tester t) {
    return t.checkExpect(this.pink.mixingFormula(2), "brighten(blend(purple, khaki))")
            && t.checkExpect(this.red.mixingFormula(10), "red")
            && t.checkExpect(this.coral.mixingFormula(4),
            "blend(brighten(blend(blend(red, blue), "
                    + "blend(red, green))), blend(red, green))");
  }
}