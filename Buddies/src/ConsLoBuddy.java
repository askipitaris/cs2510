// represents a list of Person's buddies
class ConsLoBuddy implements ILoBuddy {

  Person first;
  ILoBuddy rest;

  ConsLoBuddy(Person first, ILoBuddy rest) {
    this.first = first;
    this.rest = rest;
  }

  // Checks if this list of buddies contains that person
  public boolean contains(Person that) {
    return this.first.samePerson(that)
            || this.rest.contains(that);
  }

  // Counts how many buddies are shared between this person and that person
  public int countCommonItems(ILoBuddy that) {
    if (that.contains(this.first)) {
      return 1 + this.rest.countCommonItems(that);
    }
    else {
      return this.rest.countCommonItems(that);
    }
  }

  // Checks if any person on this list of buddies has that person as a buddy
  public boolean anyHasBuddy(Person that, ILoBuddy seenAcc) {
    if (seenAcc.contains(this.first)) {
      return this.rest.anyHasBuddy(that, seenAcc);
    }
    else {
      return this.first.hasDirectBuddy(that)
              || this.first.buddies.anyHasBuddy(that, new ConsLoBuddy(this.first, seenAcc))
              || this.rest.anyHasBuddy(that, new ConsLoBuddy(this.first, seenAcc));
    }
  }

  // Count the number of extended buddies
  public int countExtendedBuddies(ILoBuddy seenAcc) {
    if (seenAcc.contains(this.first)) {
      return this.rest.countExtendedBuddies(seenAcc);
    }
    else {
      return 1 + this.rest.countExtendedBuddies(new ConsLoBuddy(this.first, seenAcc))
              + this.first.buddies.countExtendedBuddies(new ConsLoBuddy(this.first, seenAcc))
              // If this person and this.first are both direct buddies with someone,
              // this person will be double counted. This means you need to remove the number of
              // common buddies between these two people to get the right number.
              - this.countCommonItems(this.first.buddies);
    }
  }

  // Helper for the maxLikelihood method
  public double maxLikelihoodHelp(Person that, ILoBuddy seenAcc) {
    if (this.first.hasExtendedBuddy(that) && seenAcc.contains(that)) {
      return Math.max(
              this.first.hearing
                      * (this.first.diction * this.first.buddies.maxLikelihoodHelp(that,
                      new ConsLoBuddy(this.first, seenAcc))),
              this.rest.maxLikelihoodHelp(that, new ConsLoBuddy(this.first, seenAcc)));
    }
    else if (this.first.equals(that)) {
      return 1;
    }
    else {
      return 0;
    }
  }
}