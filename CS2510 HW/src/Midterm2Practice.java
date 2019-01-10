import tester.Tester;

import java.util.ArrayList;
import java.util.Arrays;

// Useful tools
class Utils {
  // Finds the sum of an ArrayList
  int sum(ArrayList<Integer> lon) {
    int total = 0;
    for (int i = 0; i < lon.size(); i++) {
      total = total + lon.get(i);
    }
    return total;
  }

  // Checks if the ArrayList contain only positive partial sums
  boolean postivePartialSums(ArrayList<Integer> lon) {
    int partialSum = 0;
    for (int i = 0; i < lon.size(); i++) {
      partialSum = partialSum + lon.get(i);
      if (partialSum < 0) {
        return false;
      }
    }
    return true;
  }

  // Checks if this ArrayList is sorted lexographically
  boolean isSorted(ArrayList<String> los) {
    for (int i = 0; i < (los.size() - 1); i++) {
      if (los.get(i).compareTo(los.get(i + 1)) > 0) {
        return false;
      }
    }
    return true;
  }

  // Checks if this ArrayList contains the sequence of ints
  boolean containsSequence(ArrayList<Integer> lon, ArrayList<Integer> seq) {
    for (int i = 0; i < lon.size(); i++) {
      for (int j = 0; j < seq.size(); j++) {
        if (lon.get(i + j) == seq.get(i + j)) {
          return true;
        }
      }
    }
    return false;
  }
}

// Tests and examples
class ExamplesPratice {
  Utils uTest = new Utils();

  ArrayList<Integer> lon1 = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
  ArrayList<Integer> lon2 = new ArrayList<Integer>(Arrays.asList(-3, 2, 3, 4, 5));
  ArrayList<Integer> lon3 = new ArrayList<Integer>(Arrays.asList(1, 2, -3, 4, 5));
  ArrayList<Integer> lon4 = new ArrayList<Integer>(Arrays.asList(1, -5, 3, 4, 5));

  ArrayList<String> los1 = new ArrayList<String>(Arrays.asList("a", "b", "c", "d"));
  ArrayList<String> los2 = new ArrayList<String>(Arrays.asList("a", "b", "b", "c"));
  ArrayList<String> los3 = new ArrayList<String>(Arrays.asList("a", "c", "b", "d"));

  // Tests the sum method
  void testSum(Tester t) {
    t.checkExpect(uTest.sum(this.lon1), 15);
    t.checkExpect(uTest.sum(this.lon2), 11);
    t.checkExpect(uTest.sum(this.lon3), 9);
    t.checkExpect(uTest.sum(this.lon4), 8);
  }

  // Tests the postivePartialSums method
  void testPosPartialSums(Tester t) {
    t.checkExpect(uTest.postivePartialSums(this.lon1), true);
    t.checkExpect(uTest.postivePartialSums(this.lon2), false);
    t.checkExpect(uTest.postivePartialSums(this.lon3), true);
    t.checkExpect(uTest.postivePartialSums(this.lon4), false);
  }

  // Tests the isSorted method
  void testIsSorted(Tester t) {
    t.checkExpect(uTest.isSorted(this.los1), true);
    t.checkExpect(uTest.isSorted(this.los2), true);
    t.checkExpect(uTest.isSorted(this.los3), false);
  }
}