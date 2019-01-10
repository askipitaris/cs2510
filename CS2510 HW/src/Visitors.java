import tester.Tester;

// An IArith is one of:
// - Const
// - Formula
interface IArith {
  // Accepts a visitor in an IArith
  <R> R accept(IArithVisitor<R> visitor);
}

// Describes a Const
class Const implements IArith {
  double num;

  Const(double num) {
    this.num = num;
  }

  // Accepts a visitor into a Const
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visit(this);
  }
}

// Describes Formula
class Formula implements IArith {
  IFunc2<Double, Double, Double> fun;
  String name;
  IArith left;
  IArith right;

  Formula(IFunc2<Double, Double, Double> fun, String name, IArith left, IArith right) {
    this.fun = fun;
    this.name = name;
    this.left = left;
    this.right = right;
  }

  // Accepts a visitor into a Formula
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visit(this);
  }
}

// Describes a IFunc2
interface IFunc2<A1, A2, R> {
  // [A1, A2] -> R
  R apply(A1 a1, A2 a2);
}

// Adds items
class Add implements IFunc2<Double, Double, Double> {
  // Adds the items
  public Double apply(Double a1, Double a2) {
    return a1 + a2;
  }
}

// Subtracts items
class Sub implements IFunc2<Double, Double, Double> {
  // Subtracts the items
  public Double apply(Double a1, Double a2) {
    return a1 - a2;
  }
}

// Multiplies items
class Multiply implements IFunc2<Double, Double, Double> {
  // Subtracts the items
  public Double apply(Double a1, Double a2) {
    return a1 * a2;
  }
}

// Divides items
class Div implements IFunc2<Double, Double, Double> {
  // Subtracts the items
  public Double apply(Double a1, Double a2) {
    return a1 / a2;
  }
}

// Describes a IArithVisitor that visits IArith and returns type R
interface IArithVisitor<R> {
  // Visits a Const
  R visit(Const constItem);

  // Visits a Formula
  R visit(Formula formulaItem);
}

// Evaluates an entire IArith
class EvalVisitor implements IArithVisitor<Double> {
  // Evaluates the result of a const
  public Double visit(Const constItem) {
    return constItem.num;
  }

  // Evaluates the result of a formula
  public Double visit(Formula formulaItem) {
    return formulaItem.fun.apply(formulaItem.left.accept(this),
            formulaItem.right.accept(this));
  }
}

// Prints an entire IArith
class PrintVisitor implements IArithVisitor<String> {
  // Prints the Const
  public String visit(Const constItem) {
    return Double.toString(constItem.num);
  }

  // Prints the whole formula
  public String visit(Formula formulaItem) {
    return "(" + formulaItem.name + " " + formulaItem.left.accept(this)
            + " " + formulaItem.right.accept(this) + ")";
  }
}

// Doubles the value of all Consts in an IArith
class DoublerVisitor implements IArithVisitor<IArith> {
  // Doubles a Const
  public IArith visit(Const constItem) {
    return new Const(constItem.num * 2);
  }

  // Doubles every Const in this Formula
  public IArith visit(Formula formulaItem) {
    return new Formula(formulaItem.fun, formulaItem.name,
            formulaItem.left.accept(this),
            formulaItem.right.accept(this));
  }
}

// Checks if all Const values in an IArith are less than 10.0
class AllSmallVisitor implements IArithVisitor<Boolean> {
  // Checks if this Const is less than 10.0
  public Boolean visit(Const constItem) {
    return constItem.num < 10;
  }

  //  Checks if all Consts in this Formula are less than 10.0
  public Boolean visit(Formula formulaItem) {
    return formulaItem.left.accept(this)
            && formulaItem.right.accept(this);
  }
}

// Checks to make sure there is no instance of dividing by zero in an IArith
// Returns true if there is no instance of dividing by zero within this IArith
class NoDivBy0 implements IArithVisitor<Boolean> {
  // Any individual Const is not being divided by zero and therefore returns true
  public Boolean visit(Const constItem) {
    return true;
  }

  // Checks that no right argument evaluates to zero
  public Boolean visit(Formula formulaItem) {
    if (formulaItem.name.equals("div")) {
      return !(formulaItem.right.accept(new EvalVisitor()) < 0.0001
              && formulaItem.right.accept(new EvalVisitor()) > -.0001)
              && formulaItem.left.accept(this)
              && formulaItem.right.accept(this);
    }
    else {
      return formulaItem.left.accept(new NoDivBy0())
              && formulaItem.right.accept(this);
    }
  }
}

// Examples of IAriths and tests for visitors
class ExampleVisitors {
  IArith constOne = new Const(1.0);
  IArith constTwo = new Const(2.0);
  IArith constFive = new Const(5.0);
  IArith constSix = new Const(6.0);

  IArith add1 = new Formula(new Add(), "plus", this.constFive, this.constTwo);
  IArith sub1 = new Formula(new Sub(), "subtract", this.constFive, this.constTwo);
  IArith mutliply1 = new Formula(new Multiply(), "multiply", this.constFive, this.constTwo);
  IArith div1 = new Formula(new Div(), "div", this.constSix, this.constTwo);

  IArith formula1 = new Formula(new Multiply(), "multiply", this.div1, this.mutliply1);
  IArith formula2 = new Formula(new Add(), "plus", new Const(13), this.constSix);
  IArith formula3 = new Formula(new Sub(), "subtract", this.formula1, this.formula2);
  IArith formula4 = new Formula(new Div(), "div", this.formula3, new Const(0));
  IArith formula5 = new Formula(new Div(), "div", new Const(0), this.formula3);
  IArith formula6 = new Formula(new Add(), "plus", this.formula4, this.formula5);

  // Tests the accept method
  boolean testAccept(Tester t) {
    return t.checkExpect(this.add1.accept(new PrintVisitor()), "(plus 5.0 2.0)")
            && t.checkExpect(this.add1.accept(new EvalVisitor()), 7.0)
            && t.checkExpect(this.add1.accept(new DoublerVisitor()),
            new Formula(new Add(), "plus", new Const(10.0), new Const(4.0)))
            && t.checkExpect(this.add1.accept(new AllSmallVisitor()), true)
            && t.checkExpect(this.add1.accept(new NoDivBy0()), true);
  }

  // Tests the apply method
  boolean testApply(Tester t) {
    return t.checkInexact(new Add().apply(2.25, 2.25), 4.50, .0001)
            && t.checkInexact(new Sub().apply(5.60, 3.30), 2.30, .0001)
            && t.checkInexact(new Multiply().apply(2.0, 4.5), 9.0, .0001)
            && t.checkInexact(new Div().apply(1.0, 8.0), .125, .0001);
  }

  // Tests the visit method
  boolean testVisit(Tester t) {
    return t.checkInexact(new EvalVisitor().visit(new Const(1.0)), 1.0, .0001)
            && t.checkInexact(new EvalVisitor().visit(
            new Formula(new Add(), "plus", this.constFive, this.constTwo)), 7.0, .0001)
            && t.checkExpect(new PrintVisitor().visit(new Const(1.0)), "1.0")
            && t.checkExpect(new PrintVisitor().visit(
            new Formula(new Add(), "plus", this.constFive, this.constTwo)), "(plus 5.0 2.0)")
            && t.checkExpect(new DoublerVisitor().visit(new Const(1.0)), new Const(2.0))
            && t.checkExpect(new DoublerVisitor().visit(
            new Formula(new Add(), "plus", this.constFive, this.constTwo)),
            new Formula(new Add(), "plus", new Const(10.0), new Const(4.0)))
            && t.checkExpect(new AllSmallVisitor().visit(new Const(1.0)), true)
            && t.checkExpect(new AllSmallVisitor().visit(new Const(20.0)), false)
            && t.checkExpect(new AllSmallVisitor().visit(
            new Formula(new Add(), "plus", this.constFive, this.constTwo)), true)
            && t.checkExpect(new AllSmallVisitor().visit(
            new Formula(new Add(), "plus", new Const(13), this.constTwo)), false)
            && t.checkExpect(new NoDivBy0().visit(new Const(1.0)), true)
            && t.checkExpect(new NoDivBy0().visit(
            new Formula(new Add(), "plus", this.constFive, this.constTwo)), true)
            && t.checkExpect(new NoDivBy0().visit(
            new Formula(new Div(), "div", this.constFive, new Const(0.0))), false);
  }

  // Tests the eval visitor
  boolean testEvalVisitor(Tester t) {
    return t.checkInexact(this.constOne.accept(new EvalVisitor()), 1.0, .0001)
            && t.checkInexact(this.add1.accept(new EvalVisitor()), 7.0, .0001)
            && t.checkInexact(this.sub1.accept(new EvalVisitor()), 3.0, .0001)
            && t.checkInexact(this.mutliply1.accept(new EvalVisitor()), 10.0, .0001)
            && t.checkInexact(this.div1.accept(new EvalVisitor()), 3.0, .0001)
            && t.checkInexact(this.formula1.accept(new EvalVisitor()), 30.0, .0001)
            && t.checkInexact(new Formula(new Div(), "div", this.constOne, new Const(8))
            .accept(new EvalVisitor()), .125, .0001);
  }

  // Tests the print visitor
  boolean testPrintVisitor(Tester t) {
    return t.checkExpect(this.constOne.accept(new PrintVisitor()), "1.0")
            && t.checkExpect(this.add1.accept(new PrintVisitor()), "(plus 5.0 2.0)")
            && t.checkExpect(this.sub1.accept(new PrintVisitor()), "(subtract 5.0 2.0)")
            && t.checkExpect(this.mutliply1.accept(new PrintVisitor()), "(multiply 5.0 2.0)")
            && t.checkExpect(this.div1.accept(new PrintVisitor()), "(div 6.0 2.0)")
            && t.checkExpect(this.formula1.accept(new PrintVisitor()),
            "(multiply (div 6.0 2.0) (multiply 5.0 2.0))")
            && t.checkExpect(this.formula2.accept(new PrintVisitor()),
            "(plus 13.0 6.0)")
            && t.checkExpect(this.formula3.accept(new PrintVisitor()),
            "(subtract (multiply (div 6.0 2.0) (multiply 5.0 2.0)) (plus 13.0 6.0))")
            && t.checkExpect(this.formula4.accept(new PrintVisitor()),
            "(div (subtract (multiply (div 6.0 2.0) (multiply 5.0 2.0)) (plus 13.0 6.0)) 0.0)")
            && t.checkExpect(this.formula5.accept(new PrintVisitor()),
            "(div 0.0 (subtract (multiply (div 6.0 2.0) (multiply 5.0 2.0)) (plus 13.0 6.0)))")
            && t.checkExpect(this.formula6.accept(new PrintVisitor()),
            "(plus (div (subtract (multiply (div 6.0 2.0) (multiply 5.0 2.0)) "
                    + "(plus 13.0 6.0)) 0.0) "
                    + "(div 0.0 (subtract (multiply (div 6.0 2.0) (multiply 5.0 2.0)) "
                    + "(plus 13.0 6.0))))");
  }

  // Tests the double visitor
  boolean testDoublerVisitor(Tester t) {
    return t.checkExpect(this.constOne.accept(new DoublerVisitor()), new Const(2.0))
            && t.checkExpect(this.add1.accept(new DoublerVisitor()),
            new Formula(new Add(), "plus", new Const(10.0), new Const(4.0)))
            && t.checkExpect(this.sub1.accept(new DoublerVisitor()),
            new Formula(new Sub(), "subtract", new Const(10.0), new Const(4.0)))
            && t.checkExpect(this.mutliply1.accept(new DoublerVisitor()),
            new Formula(new Multiply(), "multiply", new Const(10.0), new Const(4.0)))
            && t.checkExpect(this.div1.accept(new DoublerVisitor()),
            new Formula(new Div(), "div", new Const(12.0), new Const(4.0)))
            && t.checkExpect(this.formula1.accept(new DoublerVisitor()),
            new Formula(new Multiply(), "multiply",
                    new Formula(new Div(), "div", new Const(12.0), new Const(4.0)),
                    new Formula(new Multiply(), "multiply", new Const(10.0),
                            new Const(4.0))));
  }

  // Tests the AllSmall visitor
  boolean testAllSmallVisitor(Tester t) {
    return t.checkExpect(this.constOne.accept(new AllSmallVisitor()), true)
            && t.checkExpect(this.add1.accept(new AllSmallVisitor()), true)
            && t.checkExpect(this.sub1.accept(new AllSmallVisitor()), true)
            && t.checkExpect(this.mutliply1.accept(new AllSmallVisitor()), true)
            && t.checkExpect(this.div1.accept(new AllSmallVisitor()), true)
            && t.checkExpect(this.formula1.accept(new AllSmallVisitor()), true)
            && t.checkExpect(this.formula2.accept(new AllSmallVisitor()), false)
            && t.checkExpect(this.formula3.accept(new AllSmallVisitor()), false)
            && t.checkExpect(this.formula4.accept(new AllSmallVisitor()), false)
            && t.checkExpect(this.formula5.accept(new AllSmallVisitor()), false)
            && t.checkExpect(this.formula6.accept(new AllSmallVisitor()), false);
  }

  // Tests the NoDivBy0 visitor
  boolean testNoDivBy0(Tester t) {
    return t.checkExpect(this.constOne.accept(new NoDivBy0()), true)
            && t.checkExpect(new Const(0).accept(new NoDivBy0()), true)
            && t.checkExpect(this.formula1.accept(new NoDivBy0()), true)
            && t.checkExpect(this.formula2.accept(new NoDivBy0()), true)
            && t.checkExpect(this.formula3.accept(new NoDivBy0()), true)
            && t.checkExpect(this.formula4.accept(new NoDivBy0()), false)
            && t.checkExpect(this.formula5.accept(new NoDivBy0()), true)
            && t.checkExpect(this.formula6.accept(new NoDivBy0()), false);
  }
}