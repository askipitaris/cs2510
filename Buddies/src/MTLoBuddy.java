// represents an empty list of Person's buddies
class MTLoBuddy implements ILoBuddy {
  MTLoBuddy() {
  }

  // Checks if this list of buddies contains that person
  public boolean contains(Person that) {
    return false;
  }

  // Counts how many buddies are shared between this person and that person
  public int countCommonItems(ILoBuddy that) {
    return 0;
  }

  // Checks if any person on this list of buddies has that person as a buddy
  public boolean anyHasBuddy(Person that, ILoBuddy seenAcc) {
    return false;
  }

  // Counts the number of extened buddies
  public int countExtendedBuddies(ILoBuddy seenAcc) {
    return 0;
  }

  // Helper for the maxLikelihood method
  public double maxLikelihoodHelp(Person that, ILoBuddy seenAcc) {
    return 0;
  }
}