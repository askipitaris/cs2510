/*
import tester.*;

class CakeRecipe {
  */
/*
 * - the weight of flour should be equal to the weight of sugar
 *
 * - the weight of the eggs should be equal to the weight of the butter
 *
 * - the weight of the eggs + the weight of the milk should be
 *   equal to the weight of the sugar (or flour)
 *//*


  //initializes variables
  double flour;
  double sugar;
  double butter;
  double egg;
  double milk;

  //main constructor that takes in 5 fields and determines whether the cake is perfect
  CakeRecipe(double f, double s, double b, double e, double m) {
    Utils u = new Utils();
    this.flour = u.checkIngredients(f, s);
    this.sugar = u.checkIngredients(s, f);
    this.butter = u.checkIngredients(b, e);
    this.egg = u.checkIngredients(e, b);
    this.milk = u.checkIngredientsMilk(e, m, s);
  }

  //constructor that takes in 3 fields
  CakeRecipe(double f, double e, double m) {
    this(f, f, e, e, m);
  }

  //constructor that takes in 3 fields and boolean that
  //determines whether they are volume measurements
  CakeRecipe(double f, double e, double m, boolean areVolumes) {
    Utils u = new Utils();
    if (areVolumes) {
      this.flour = f * 4.25;
      this.sugar = f * 4.25;
      this.butter = e * 1.75;
      this.egg = e * 1.75;
      this.milk = u.checkIngredientsMilk(e * 1.75, m * 8, f * 4.25);
    } else {

      this.flour = u.checkIngredients(f, f);
      this.sugar = u.checkIngredients(f, f);
      this.butter = u.checkIngredients(e, e);
      this.egg = u.checkIngredients(e, e);
      this.milk = u.checkIngredientsMilk(e, m, f);
    }
  }

  // Chekcs if all the ingredients have the correct ratios
  boolean sameRecipe(CakeRecipe other) {
    return Math.abs(this.flour - other.flour) < .001 &&
            Math.abs(this.butter - other.butter) < .001 &&
            Math.abs((this.flour - (other.egg + other.milk))) < .001;
  }
}

class Utils {
  //checks the weights of two ingredients
  double checkIngredients(double ingred1, double ingred2) {
    if (ingred1 == ingred2) {
      return ingred1;
    } else {
      throw new IllegalArgumentException("This is not a perfect cake.");
    }
  }

  //checks if the weight of the milk and eggs is equals to the weight of the sugar
  double checkIngredientsMilk(double ingredegg, double ingredmilk, double ingredsugar) {
    if (ingredegg + ingredmilk == ingredsugar) {
      return ingredmilk;
    } else {
      throw new IllegalArgumentException("This is not a perfect cake.");
    }
  }

}

class ExamplesCakeRecipe {
  //cake made with main constructor
  CakeRecipe cake1 = new CakeRecipe(4, 4, 2, 2, 2);
  //cake made with three field constructor
  CakeRecipe cake2 = new CakeRecipe(4, 2, 2);
  //same cake
  CakeRecipe cake3 = new CakeRecipe(5.9 / 4.25, 1.1 / 1.75, 4.8 / 8, true);
  // as this cake
  CakeRecipe cake4 = new CakeRecipe(5.9, 1.1, 4.8);
  //big boy cake by volume
  CakeRecipe cake5 = new CakeRecipe(56, 68, 14.875, true);
  //same big boy cake by weight
  CakeRecipe cake6 = new CakeRecipe(238, 119, 119);
  //same big boy cake by weight main constructor
  CakeRecipe cake7 = new CakeRecipe(238, 238, 119, 119, 119);

  //tests for sameRecipe method
  boolean testSameRecipe(Tester t) {
    return t.checkExpect(this.cake2.sameRecipe(this.cake3), false) &&
            t.checkExpect(this.cake7.sameRecipe(this.cake1), false) &&
            t.checkExpect(this.cake2.sameRecipe(this.cake4), false) &&
            t.checkExpect(this.cake3.sameRecipe(this.cake5), false) &&
            t.checkExpect(this.cake5.sameRecipe(this.cake6), true) &&
            t.checkExpect(this.cake1.sameRecipe(this.cake2), true) &&
            t.checkExpect(this.cake3.sameRecipe(this.cake4), true) &&
            t.checkExpect(this.cake7.sameRecipe(this.cake5), true);
  }

  //tests for Exceptions
  boolean testConstructor(Tester t) {
    return t.checkException(new IllegalArgumentException("This is not a perfect cake."),
            new Utils(), "checkIngredients", 3.0, 2.0) &&
            t.checkException(new IllegalArgumentException("This is not a perfect cake."),
                    new Utils(), "checkIngredientsMilk", 2.0, 2.0, 2.0);
  }
}*/
