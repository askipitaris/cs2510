// CS 2510, Assignment 3

import tester.Tester;

// to represent a list of Strings
interface ILoString {
  // combine all Strings in this list into one
  String combine();

  // sort the strings in alphabetical order
  ILoString sort();

  // insert a string into a non-empty string
  ILoString insert(String s);

  // checks if a list is sorted
  boolean isSorted();

  // helps check if a list is sorted
  boolean isSortedHelp(ILoString that);

  // checks if it a list is empty
  boolean isMtLoString();

  // checks if a list is cons
  boolean isConsLoString();

  // weaves two lists together
  ILoString interleave(ILoString that);

  // reverses a list
  ILoString reverse();

  // helps reverse a list of strings
  ILoString reverseHelper(ILoString los);

  //check if the list is doubled
  boolean isDoubledList();

  //takes in a strings and compares it to first of
  //the LoStrings and returns whether they are the same.
  boolean doubleListHelper(String string);

  // determines if a list is a palindrome
  boolean isPalindromeList();

  // helps determine if a list is a palindrome
  boolean isPalindromeHelp(ILoString clos);

  // merges two lists
  ILoString merge(ILoString that);

  // determines the length of a list
  int length();
}

/************************************************************************************************/

/************************************************************************************************/

// to represent an empty list of Strings
class MtLoString implements ILoString {
  MtLoString() {
  }

  // combine all Strings in this list into one
  public String combine() {
    return "";
  }

  // sort the list of strings in alphabetical order
  public ILoString sort() {
    return this;
  }

  // sort the string into a non-empty list of string
  public ILoString insert(String s) {
    return new ConsLoString(s, this);
  }

  // checks if a list of strings is sorted,
  // will always be true because it is an empty list
  public boolean isSorted() {
    return true;
  }

  //helps check if a list is sorted
  public boolean isSortedHelp(ILoString that) {
    return that.isMtLoString();
  }

  // checks if a list is empty
  public boolean isMtLoString() {
    return true;
  }

  // checks if it is a ConsLoStrig
  public boolean isConsLoString() {
    return false;
  }

  // weaves two lists together
  public ILoString interleave(ILoString that) {
    return that;

  }

  // reverses a list
  public ILoString reverse() {
    return this;
  }

  // helps reverse a list
  public ILoString reverseHelper(ILoString los) {
    return los;
  }

  // determines if a list is a double
  public boolean isDoubledList() {
    return true;
  }

  // helps determine if a list is a double
  public boolean doubleListHelper(String string) {
    return true;
  }

  // determines if a list is a palindrome
  public boolean isPalindromeList() {
    return true;
  }

  // helps determine if a list is a palindrome
  public boolean isPalindromeHelp(ILoString clos) {
    return true;
  }

  // merges two lists
  public ILoString merge(ILoString that) {
    return this;
  }

  // determines the length of an ILoString
  public int length() {
    return 0;
  }

}
/************************************************************************************************/

/************************************************************************************************/

// to represent a nonempty list of Strings
class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
  TEMPLATE
  FIELDS:
  ... this.first ...         -- String
  ... this.rest ...          -- ILoString

  METHODS
  ... this.combine() ...     -- String
  ... this.sort() ...        -- ILoString

  METHODS FOR FIELDS
  ... this.first.concat(String) ...        -- String
  ... this.first.compareTo(String) ...     -- int
  ... this.rest.combine() ...              -- String

  */

  // combine all Strings in this list into one
  public String combine() {
    return this.first.concat(this.rest.combine());
  }

  // Sort a list of strings in alphabetical order
  public ILoString sort() {
    return this.rest.sort().insert(this.first);
  }

  // Inserts a string into a non-empty list of strings
  public ILoString insert(String s) {
    if (this.first.toLowerCase().compareTo(s.toLowerCase()) <= 0) {
      return new ConsLoString(this.first, this.rest.insert(s));
    }
    else {
      return new ConsLoString(s, this);
    }
  }

  // checks if a list of strings is sorted
  public boolean isSorted() {
    return this.isSortedHelp(this.sort());
  }

  // checks if a list is empty
  public boolean isMtLoString() {
    return false;
  }

  // checks if a list is a Cons
  public boolean isConsLoString() {
    return true;
  }

  // helps check if the strings are sorted
  public boolean isSortedHelp(ILoString that) {
    if (that.isConsLoString()) {
      return this.isSameItem((ConsLoString) that);
    }
    else {
      return false;
    }
  }

  // checks if two items are the same
  boolean isSameItem(ConsLoString that) {
    return this.first.equals(that.first)
            && this.rest.isSortedHelp(that.rest);
  }

  // weaves two lists together
  public ILoString interleave(ILoString that) {
    return new ConsLoString(this.first, that.interleave(this.rest));
  }

  // reverses a list
  public ILoString reverse() {
    return reverseHelper(new MtLoString());
  }

  // helps reverse a list
  public ILoString reverseHelper(ILoString los) {
    return this.rest.reverseHelper(new ConsLoString(this.first, los));
  }

  // determines if a list is doubled
  public boolean isDoubledList() {
    if (this.length() % 2 == 0) {
      return this.rest.doubleListHelper(this.first);
    }
    else {
      return false;
    }
  }

  // helps determine if a list is doubled
  public boolean doubleListHelper(String string) {
    return this.first.equals(string) && this.rest.isDoubledList();
  }

  // determines if a list is a palindrome
  public boolean isPalindromeList() {
    return this.isPalindromeHelp(this.reverse());
  }

  // helps determine if a list is a palindrome
  public boolean isPalindromeHelp(ILoString clos) {
    if (clos.isConsLoString()) {
      return this.isPalindromeSame((ConsLoString) clos);
    }
    else {
      return false;
    }
  }

  // determines if two parts of a list are the same
  boolean isPalindromeSame(ConsLoString clos) {
    return this.first.equals(clos.first)
            && this.rest.isPalindromeHelp(clos.rest);
  }

  // merges two lists
  public ILoString merge(ILoString that) {
    return this.interleave(that).sort();
  }

  // determines the length of a list
  public int length() {
    return 1 + this.rest.length();
  }


}
/************************************************************************************************/

/************************************************************************************************/

// to represent examples for lists of strings
class ExamplesStrings {
  ILoString mary = new ConsLoString("Mary ",
          new ConsLoString("had ",
                  new ConsLoString("a ",
                          new ConsLoString("little ",
                                  new ConsLoString("lamb.", new MtLoString())))));

  ILoString upHill = new ConsLoString("Jack ", new ConsLoString("and ",
          new ConsLoString("Jill ",
                  new ConsLoString("went ",
                          new ConsLoString("up ",
                                  new ConsLoString("the ",
                                          new ConsLoString("hill.", new MtLoString())))))));

  ILoString doubleList = new ConsLoString("Mumbo", new ConsLoString("Mumbo",
          new ConsLoString("Jumbo", new ConsLoString("Jumbo", new ConsLoString("Bumbo",
                  new ConsLoString("Bumbo", new MtLoString()))))));

  ILoString palindrome = new ConsLoString("Hey",
          new ConsLoString("is", new ConsLoString("is",
                  new ConsLoString("Hey", new MtLoString()))));

  // don't worry about it
  ILoString exampleTest = new ConsLoString("a ", new ConsLoString("and ", new ConsLoString("had ",
          new ConsLoString("hill.", new ConsLoString("Jack ",
                  new ConsLoString("Jill ", new ConsLoString("lamb.",
                          new ConsLoString("little ", new ConsLoString("Mary ",
                                  new ConsLoString("the ",
                                          new ConsLoString("up ",
                                                  new ConsLoString("went ",
                                                          new MtLoString()))))))))))));

  // test the method combine for the lists of Strings
  boolean testCombine(Tester t) {
    return
            t.checkExpect(this.mary.combine(), "Mary had a little lamb.");
  }

  // test the method that sorts a list of strings
  boolean testSort(Tester t) {
    return t.checkExpect(this.mary.sort(),
            new ConsLoString("a ", new ConsLoString("had ",
                    new ConsLoString("lamb.", new ConsLoString("little ",
                            new ConsLoString("Mary ", new MtLoString())))))) &&

            t.checkExpect(this.upHill.sort(),
                    new ConsLoString("and ", new ConsLoString("hill.",
                            new ConsLoString("Jack ", new ConsLoString("Jill ",
                                    new ConsLoString("the ", new ConsLoString("up ",
                                            new ConsLoString("went ", new MtLoString())))))))) &&

            t.checkExpect((this.mary.interleave(this.upHill)).sort(), this.exampleTest);

  }

  // test the method that checks if a list is sorted
  boolean testIsSorted(Tester t) {
    return t.checkExpect(this.mary.isSorted(), false)
            && t.checkExpect(this.mary.sort().isSorted(), true)
            && t.checkExpect(this.upHill.isSorted(), false)
            && t.checkExpect(this.upHill.sort().isSorted(), true);
  }

  // test the method that weaves two lists together
  boolean testInterleave(Tester t) {
    return t.checkExpect(this.mary.interleave(this.upHill),
            new ConsLoString("Mary ", new ConsLoString("Jack ", new ConsLoString("had ",
                    new ConsLoString("and ", new ConsLoString("a ", new ConsLoString("Jill ",
                            new ConsLoString("little ", new ConsLoString("went ",
                                    new ConsLoString("lamb.", new ConsLoString("up ",
                                            new ConsLoString("the ", new ConsLoString("hill.",
                                                    new MtLoString())))))))))))));
  }


  // test the method that merges two lists
  boolean testMerge(Tester t) {
    return t.checkExpect(this.upHill.merge(this.mary), this.upHill.interleave(this.mary).sort())
            && t.checkExpect(this.doubleList.merge(this.palindrome),
            this.doubleList.interleave(this.palindrome).sort());
  }

  // test the method that reverses a list
  boolean testReverse(Tester t) {
    return t.checkExpect(this.mary.reverse(), new ConsLoString("lamb.",
            new ConsLoString("little ", new ConsLoString("a ",
                    new ConsLoString("had ", new ConsLoString("Mary ",
                            new MtLoString()))))));
  }

  // test the method that checks if a list is doubled
  boolean testerIsDoubledList(Tester t) {
    return t.checkExpect(this.mary.isDoubledList(), false) &&
            t.checkExpect(this.doubleList.isDoubledList(), true);
  }

  // tests the method that checks if a list is a plaindrome
  boolean testerIsPalindrome(Tester t) {
    return t.checkExpect(this.mary.isPalindromeList(), false)
            && t.checkExpect(this.palindrome.isPalindromeList(), true);
  }
}
