// represents a Person with a user name and a list of buddies
class Person {

  String username;
  ILoBuddy buddies;
  double diction;
  double hearing;

  Person(String username) {
    this.username = username;
    this.buddies = new MTLoBuddy();
    this.diction = 0.0;
    this.hearing = 0.0;
  }

  Person(String username, double diction, double hearing) {
    this.username = username;
    this.diction = diction;
    this.hearing = hearing;
    this.buddies = new MTLoBuddy();
  }

  // returns true if this Person has that as a direct buddy
  boolean hasDirectBuddy(Person that) {
    return this.buddies.contains(that);
  }

  // Checks if this person is the same as that person
  boolean samePerson(Person that) {
    return this.username.equals(that.username);
  }

  // returns the number of people who will show up at the party
  // given by this person
  int partyCount() {
    return 1 + this.buddies.countExtendedBuddies(new ConsLoBuddy(this, new MTLoBuddy()));
  }

  // returns the number of people that are direct buddies
  // of both this and that person
  int countCommonBuddies(Person that) {
    return this.buddies.countCommonItems(that.buddies);
  }

  // will the given person be invited to a party
  // organized by this person?
  boolean hasExtendedBuddy(Person that) {
    return this.hasDirectBuddy(that)
            || this.buddies.anyHasBuddy(that, new MTLoBuddy());
  }

  // Effect:
  // Change this person's buddy list so that it inclues the given person
  void addBuddy(Person buddy) {
    this.buddies = new ConsLoBuddy(buddy, this.buddies);
  }

  // Calcualtes the max liklihood that this person can correctly convey a messege to that person
  double maxLikelihood(Person that) {
    if (this.equals(that)) {
      return 1;
    }
    else {
      return this.diction * this.buddies.maxLikelihoodHelp(that, new ConsLoBuddy(this,
              new MTLoBuddy()));
    }
  }
}