// represents a list of Person's buddies
interface ILoBuddy {
  // Checks if this list of buddies contains this person
  boolean contains(Person that);

  // Counts how many buddies are shared between this person and that person
  int countCommonItems(ILoBuddy that);

  // Checks if any person on this list of buddies has that person as a buddy
  boolean anyHasBuddy(Person that, ILoBuddy seenAcc);

  // Counts the number of extended buddies
  int countExtendedBuddies(ILoBuddy seenAcc);

  // Helper for the maxLikelihood method
  double maxLikelihoodHelp(Person that, ILoBuddy seenAcc);
}