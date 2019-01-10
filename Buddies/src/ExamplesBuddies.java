import tester.*;

// runs tests for the buddies problem
public class ExamplesBuddies {
  Person ann = new Person("Ann");
  Person bob = new Person("Bob");
  Person cole = new Person("Cole");
  Person dan = new Person("Dan");
  Person ed = new Person("Ed");
  Person fay = new Person("Fay");
  Person gabi = new Person("Gabi");
  Person hank = new Person("Hank");
  Person jan = new Person("Jan");
  Person kim = new Person("Kim");
  Person len = new Person("Len");

  // Examples for maxLikelihood
  Person alex = new Person("Alex", 0.90, 0.95);
  Person philip = new Person("Philip", 0.95, 0.70);
  Person michael = new Person("Michael", 0.95, 0.95);

  // Initialize the data and add everyones buddies
  void initBuddies() {
    this.ann.buddies = new ConsLoBuddy(this.bob, new ConsLoBuddy(this.cole, new MTLoBuddy()));
    this.bob.buddies = new ConsLoBuddy(this.ann, new ConsLoBuddy(
            this.ed, new ConsLoBuddy(this.hank, new MTLoBuddy())));
    this.cole.buddies = new ConsLoBuddy(this.dan, new MTLoBuddy());
    this.dan.buddies = new ConsLoBuddy(this.cole, new MTLoBuddy());
    this.ed.buddies = new ConsLoBuddy(this.fay, new MTLoBuddy());
    this.fay.buddies = new ConsLoBuddy(this.ed, new ConsLoBuddy(this.gabi, new MTLoBuddy()));
    this.gabi.buddies = new ConsLoBuddy(this.ed, new ConsLoBuddy(this.fay, new MTLoBuddy()));
    this.hank.buddies = new MTLoBuddy(); // Poor Hank
    this.jan.buddies = new ConsLoBuddy(this.kim, new ConsLoBuddy(this.len, new MTLoBuddy()));
    this.kim.buddies = new ConsLoBuddy(this.jan, new ConsLoBuddy(this.len, new MTLoBuddy()));
    this.len.buddies = new ConsLoBuddy(this.jan, new ConsLoBuddy(this.kim, new MTLoBuddy()));

    this.alex.buddies = new ConsLoBuddy(this.philip, new MTLoBuddy());
    this.philip.buddies = new ConsLoBuddy(this.alex, new ConsLoBuddy(this.michael,
            new MTLoBuddy()));
    this.michael.buddies = new ConsLoBuddy(this.philip, new ConsLoBuddy(this.alex,
            new MTLoBuddy()));
  }

  // Test the addBuddy method
  void testAddBuddy(Tester t) {
    this.initBuddies();

    this.hank.addBuddy(this.bob);
    t.checkExpect(this.hank.buddies, new ConsLoBuddy(this.bob, new MTLoBuddy()));

    this.bob.addBuddy(this.jan);
    t.checkExpect(this.bob.buddies, new ConsLoBuddy(this.jan,
            new ConsLoBuddy(this.ann, new ConsLoBuddy(
                    this.ed, new ConsLoBuddy(this.hank, new MTLoBuddy())))));
  }

  // Tests the samePerson method
  void testSamePerson(Tester t) {
    this.initBuddies();

    t.checkExpect(this.bob.samePerson(this.hank), false);
    t.checkExpect(this.bob.samePerson(this.bob), true);
  }

  // Tests the contains method
  void testContains(Tester t) {
    this.initBuddies();

    t.checkExpect(this.bob.buddies.contains(this.hank), true);
    t.checkExpect(this.hank.buddies.contains(this.bob), false);
    t.checkExpect(this.len.buddies.contains(this.kim), true);
    t.checkExpect(this.kim.buddies.contains(this.fay), false);
  }

  // Tests the hasDirectBuddy method
  void testHasDirectBuddy(Tester t) {
    this.initBuddies();

    t.checkExpect(this.bob.hasDirectBuddy(this.hank), true);
    t.checkExpect(this.bob.hasDirectBuddy(this.len), false);
    t.checkExpect(this.ed.hasDirectBuddy(this.fay), true);
    t.checkExpect(this.dan.hasDirectBuddy(this.ed), false);
  }

  // Tests the countCommonItems method
  void testCountCommonItems(Tester t) {
    this.initBuddies();

    t.checkExpect(this.bob.buddies.countCommonItems(this.hank.buddies), 0);
    t.checkExpect(this.jan.buddies.countCommonItems(this.kim.buddies), 1);
    t.checkExpect(this.kim.buddies.countCommonItems(this.len.buddies), 1);

    this.kim.addBuddy(this.cole);
    this.len.addBuddy(this.cole);
    t.checkExpect(this.kim.buddies.countCommonItems(this.len.buddies), 2);
  }

  // Tests the countCommonBuddies method
  void testCountCommonBuddies(Tester t) {
    this.initBuddies();

    t.checkExpect(this.bob.countCommonBuddies(this.hank), 0);
    t.checkExpect(this.jan.countCommonBuddies(this.kim), 1);
    t.checkExpect(this.kim.countCommonBuddies(this.len), 1);

    this.kim.addBuddy(this.cole);
    this.len.addBuddy(this.cole);
    t.checkExpect(this.kim.countCommonBuddies(this.len), 2);
  }

  // Tests the anyHasBuddy method
  void testAnyHadBuddy(Tester t) {
    this.initBuddies();

    t.checkExpect(this.bob.buddies.anyHasBuddy(this.cole, new MTLoBuddy()), true);
    t.checkExpect(this.bob.buddies.anyHasBuddy(this.jan, new MTLoBuddy()), false);
    t.checkExpect(this.bob.buddies.anyHasBuddy(this.dan, new MTLoBuddy()), true);
  }

  // Tests the hasExtendedBuddy method
  void testHasExtendedBuddy(Tester t) {
    this.initBuddies();

    t.checkExpect(this.bob.hasExtendedBuddy(this.dan), true);
    t.checkExpect(this.bob.hasExtendedBuddy(this.fay), true);
    t.checkExpect(this.bob.hasExtendedBuddy(this.gabi), true);
    t.checkExpect(this.bob.hasExtendedBuddy(this.kim), false);
  }

  // Tests the countExtendedBuddies method
  void testCountExtendedBuddies(Tester t) {
    this.initBuddies();

    t.checkExpect(this.bob.buddies.countExtendedBuddies(new ConsLoBuddy(this.bob, new MTLoBuddy())),
            7);
    t.checkExpect(this.ed.buddies.countExtendedBuddies(new ConsLoBuddy(this.ed, new MTLoBuddy())),
            2);
    t.checkExpect(this.jan.buddies.countExtendedBuddies(new ConsLoBuddy(this.jan, new MTLoBuddy())),
            2);
  }

  // Tests the countParty method
  void testCountPart(Tester t) {
    this.initBuddies();

    t.checkExpect(this.bob.partyCount(), 8);
    t.checkExpect(this.ed.partyCount(), 3);
    t.checkExpect(this.fay.partyCount(), 3);
    t.checkExpect(this.jan.partyCount(), 3);
  }

  // Tests the maxLikelihood method
  void testMaxLikelihood(Tester t) {
    this.initBuddies();

    t.checkInexact(this.alex.maxLikelihood(this.alex), 1.0, 0.001);
    t.checkInexact(this.alex.maxLikelihood(this.philip), 0.9, 0.001);
    t.checkInexact(this.philip.maxLikelihood(this.alex), 0.95, 0.001);
    t.checkInexact(this.michael.maxLikelihood(this.alex), 0.855, 0.001);
    t.checkInexact(this.alex.maxLikelihood(this.michael), 0.568, 0.001);
  }
}