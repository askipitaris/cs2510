import tester.Tester;

// A list of T is one of
// - ConsList<T>
// - MtList<T>
interface IList<T> {
  // Check if this list is the same as that list
  boolean classmatesHelp(IList<T> that);

  // Checks if this list contains the given item using the given comparator
  boolean contains(T item, IComparator compare);

  // Counts how many items are shared between this list and the given list
  int countList(IList<T> that, IComparator compare);

  // Checks if this list is exactly the same as that list
  boolean sameList(IList<T> that, IComparator compare);
}

// Describes a ConsList<T>
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // Check if this list is the same as that list
  public boolean classmatesHelp(IList<T> that) {
    return that.contains(this.first, new SameCourse())
            || this.rest.classmatesHelp(that);
  }

  // Check if this list contains the given item using the given comparator
  public boolean contains(T that, IComparator compare) {
    return compare.apply(that, this.first)
            || this.rest.contains(that, compare);
  }

  // Counts how many items are shared between this list and the given list
  public int countList(IList<T> that, IComparator compare) {
    if (that.contains(this.first, compare)) {
      return 1 + this.rest.countList(that, compare);
    }
    else {
      return this.rest.countList(that, compare);
    }
  }

  // Checks if this list is exactly the same as that list
  public boolean sameList(IList<T> that, IComparator compare) {
    return that.contains(this.first, compare)
            && this.rest.sameList(that, compare);
  }
}

// Describes an MtList<T>
class MtList<T> implements IList<T> {

  // Check if this list is the same as that list
  public boolean classmatesHelp(IList<T> that) {
    return false;
  }

  // Check if this list contains the given item using the given comparator
  public boolean contains(T that, IComparator compare) {
    return false;
  }

  // Counts how any items are shared between this list and the given list
  public int countList(IList<T> that, IComparator compare) {
    return 0;
  }

  // Checks if this list is exactly the same as that list
  public boolean sameList(IList<T> that, IComparator compare) {
    return true;
  }
}

// Interface for comparators
interface IComparator<T> {
  // Compare these two items
  boolean apply(T item1, T item2);
}

// Compare courses
class SameCourse implements IComparator<Course> {
  // Compare these two courses
  public boolean apply(Course item1, Course item2) {
    return item1.sameCourse(item2);
  }
}

// Compare students
class SameStudent implements IComparator<Student> {
  // Compare these two students
  public boolean apply(Student item1, Student item2) {
    return item1.sameStudent(item2);
  }
}

// Describes a Course
class Course {
  String name;
  Instructor prof;
  IList<Student> students;

  // Courses are initally created without any students
  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    // Adds this course to the instructors list of courses
    this.prof.updateCourses(this);
    this.students = new MtList<Student>();
  }

  // Courses are initally created without any students
  Course(String name, Instructor prof, IList<Student> students) {
    this.name = name;
    this.prof = prof;
    // Adds this course to the instructors list of courses
    this.prof.updateCourses(this);
    this.students = students;
  }

  // Adds this student to the attendence sheet of the course
  void addStudent(Student s) {
    this.students = new ConsList<Student>(s, this.students);
  }

  // Checks if this course is the same as that course
  boolean sameCourse(Course that) {
    return this.name.equals(that.name)
            && this.prof.sameInstructor(that.prof)
            && this.students.sameList(that.students, new SameStudent());
  }
}


// Describes an Instructor
class Instructor {
  String name;
  IList<Course> courses;

  // Instructors are initally created without any courses
  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }

  Instructor(String name, IList<Course> courses) {
    this.name = name;
    this.courses = courses;
  }

  // Adds the the given course to the instructors list of courses
  void updateCourses(Course c) {
    this.courses = new ConsList<Course>(c, this.courses);
  }

  // Checks if this instructor is the same as that constructor
  boolean sameInstructor(Instructor that) {
    return this.name.equals(that.name);
  }

  // Checks if the given student is in more than one of this Instructor's classes
  boolean dejavu(Student c) {
    return this.courses.countList(c.courses, new SameCourse()) >= 2;
  }
}

// Describes a Student
class Student {
  String name;
  int id;
  IList<Course> courses;

  // Students are initially created without any courses
  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<Course>();
  }

  Student(String name, int id, IList<Course> courses) {
    this.name = name;
    this.id = id;
    this.courses = courses;
  }

  // Enrolls this student in the given course
  void enroll(Course c) {
    this.courses = new ConsList<Course>(c, this.courses);
    // Adds this student to the attendence sheet of the course
    c.addStudent(this);
  }

  // Checks if this student is a classmate with the given student
  boolean classmates(Student c) {
    return this.courses.classmatesHelp(c.courses);
  }

  // Checks if these students are the same
  boolean sameStudent(Student s) {
    return this.id == s.id;
  }
}

// Examples and tests
class ExamplesRegistrar {
  // Instructors are initially constructed without any Courses
  Instructor swasey = new Instructor("Richard Swasey");
  Instructor simon = new Instructor("Peter Simon");
  Instructor rachlin = new Instructor("John Rachlin");

  // Students are initially constructed without taking any Courses
  Student vandoorne = new Student("Stoffel Vandoorne", 1);
  Student russell = new Student("George Russel", 2);
  Student hulkenburg = new Student("Nico Hulkenburg", 3);
  Student eaton = new Student("Abbie Eaton", 4);
  Student wolff = new Student("Susie Wolff", 5);

  // Courses
  Course fina2201 = new Course("Financial Management", this.swasey);
  Course fina2201sec2 = new Course("Financial Management", this.swasey);
  Course econ1115 = new Course("Macroeconomics", this.simon);
  Course econ1116 = new Course("Microeconomics", this.simon);
  Course cs3200 = new Course("Database Designs", this.rachlin);
  Course cs5200 = new Course("Database Management Systems", this.rachlin);

  // Initialize the default data for instructors, students and courses
  void initData() {
    // Instructors
    this.swasey.courses = new ConsList<Course>(this.fina2201,
            new ConsList<Course>(this.fina2201sec2, new MtList<Course>()));
    this.simon.courses = new ConsList<Course>(this.econ1115, new ConsList<Course>(this.econ1116,
            new MtList<>()));
    this.rachlin.courses = new ConsList<Course>(this.cs3200, new ConsList<Course>(this.cs5200,
            new MtList<>()));
    // Students
    this.vandoorne.courses = new ConsList<Course>(this.econ1115,
            new ConsList<Course>(this.econ1116, new MtList<Course>()));
    this.russell.courses = new ConsList<Course>(this.cs3200,
            new ConsList<Course>(this.econ1116, new MtList<Course>()));
    this.hulkenburg.courses = new ConsList<Course>(this.cs3200,
            new ConsList<Course>(this.cs5200, new MtList<Course>()));
    this.eaton.courses = new ConsList<Course>(this.fina2201,
            new ConsList<Course>(this.cs5200, new MtList<Course>()));
    this.wolff.courses = new ConsList<Course>(this.econ1115,
            new ConsList<Course>(this.fina2201, new MtList<Course>()));
    // Courses
    this.fina2201.students = new ConsList<Student>(this.eaton,
            new ConsList<Student>(this.wolff, new MtList<Student>()));
    this.fina2201sec2.students = new MtList<Student>();
    this.econ1115.students = new ConsList<Student>(this.vandoorne,
            new ConsList<Student>(this.wolff, new MtList<Student>()));
    this.econ1116.students = new ConsList<Student>(this.vandoorne,
            new ConsList<Student>(this.russell, new MtList<Student>()));
    this.cs3200.students = new ConsList<Student>(this.russell,
            new ConsList<Student>(this.hulkenburg, new MtList<Student>()));
    this.cs5200.students = new ConsList<Student>(this.hulkenburg,
            new ConsList<Student>(this.eaton, new MtList<Student>()));
  }

  // Tests the addStudent method
  void testAddStudent(Tester t) {
    // Reset data to default
    this.initData();

    // Adds student to a course's attendence sheet
    this.fina2201.addStudent(this.vandoorne);
    t.checkExpect(this.fina2201.students, new ConsList<Student>(this.vandoorne,
            new ConsList<Student>(this.eaton,
                    new ConsList<Student>(this.wolff, new MtList<Student>()))));

    this.econ1115.addStudent(this.russell);
    t.checkExpect(this.econ1115.students, new ConsList<Student>(this.russell,
            new ConsList<Student>(this.vandoorne,
                    new ConsList<Student>(this.wolff, new MtList<Student>()))));

    this.econ1116.addStudent(this.hulkenburg);
    t.checkExpect(this.econ1116.students, new ConsList<Student>(this.hulkenburg,
            new ConsList<Student>(this.vandoorne,
                    new ConsList<Student>(this.russell, new MtList<Student>()))));

    this.cs3200.addStudent(this.eaton);
    t.checkExpect(this.cs3200.students, new ConsList<Student>(this.eaton,
            new ConsList<Student>(this.russell,
                    new ConsList<Student>(this.hulkenburg, new MtList<Student>()))));

    this.cs5200.addStudent(this.wolff);
    t.checkExpect(this.cs5200.students, new ConsList<Student>(this.wolff,
            new ConsList<Student>(this.hulkenburg,
                    new ConsList<Student>(this.eaton, new MtList<Student>()))));
  }

  // Tests the enroll method
  void testEnroll(Tester t) {
    // Reset data to default
    this.initData();

    // Enroll students in courses and check that they get added to attendence sheet
    this.vandoorne.enroll(this.fina2201);
    t.checkExpect(this.vandoorne.courses, new ConsList<Course>(this.fina2201,
            new ConsList<Course>(this.econ1115, new ConsList<Course>(this.econ1116,
                    new MtList<Course>()))));
    t.checkExpect(this.fina2201.students, new ConsList<Student>(this.vandoorne,
            new ConsList<Student>(this.eaton,
                    new ConsList<Student>(this.wolff, new MtList<Student>()))));

    this.russell.enroll(this.econ1115);
    t.checkExpect(this.russell.courses, new ConsList<Course>(this.econ1115,
            new ConsList<Course>(this.cs3200, new ConsList<Course>(this.econ1116,
                    new MtList<Course>()))));
    t.checkExpect(this.econ1115.students, new ConsList<Student>(this.russell,
            new ConsList<Student>(this.vandoorne,
                    new ConsList<Student>(this.wolff, new MtList<Student>()))));

    this.hulkenburg.enroll(this.econ1116);
    t.checkExpect(this.hulkenburg.courses, new ConsList<Course>(this.econ1116,
            new ConsList<Course>(this.cs3200, new ConsList<Course>(this.cs5200,
                    new MtList<Course>()))));
    t.checkExpect(this.econ1116.students, new ConsList<Student>(this.hulkenburg,
            new ConsList<Student>(this.vandoorne,
                    new ConsList<Student>(this.russell, new MtList<Student>()))));

    this.eaton.enroll(this.cs3200);
    t.checkExpect(this.eaton.courses, new ConsList<Course>(this.cs3200,
            new ConsList<Course>(this.fina2201, new ConsList<Course>(this.cs5200,
                    new MtList<Course>()))));
    t.checkExpect(this.cs3200.students, new ConsList<Student>(this.eaton,
            new ConsList<Student>(this.russell,
                    new ConsList<Student>(this.hulkenburg, new MtList<Student>()))));

    this.wolff.enroll(this.cs5200);
    t.checkExpect(this.wolff.courses, new ConsList<Course>(this.cs5200,
            new ConsList<Course>(this.econ1115, new ConsList<Course>(this.fina2201,
                    new MtList<Course>()))));
    t.checkExpect(this.cs5200.students, new ConsList<Student>(this.wolff,
            new ConsList<Student>(this.hulkenburg,
                    new ConsList<Student>(this.eaton, new MtList<Student>()))));
  }

  // Tests the comparator for the SameCourse function object
  void testSameCourse(Tester t) {
    // Reset the data to default
    this.initData();

    t.checkExpect(new SameCourse().apply(this.cs3200, this.cs5200), false);
    t.checkExpect(new SameCourse().apply(this.fina2201, fina2201sec2), false);
    t.checkExpect(new SameCourse().apply(this.econ1116, this.econ1116), true);
  }

  // Tests the comparator for the SameStudent function object
  void testSameStudent(Tester t) {
    // Reset the dat to default
    this.initData();

    t.checkExpect(new SameStudent().apply(this.vandoorne, this.russell), false);
    t.checkExpect(new SameStudent().apply(this.hulkenburg, this.wolff), false);
    t.checkExpect(new SameStudent().apply(this.eaton, this.eaton), true);
  }

  // Test the contains method
  void testContains(Tester t) {
    // Reset data to default
    this.initData();

    t.checkExpect(this.wolff.courses.contains(this.econ1115, new SameCourse()), true);
    t.checkExpect(this.wolff.courses.contains(this.fina2201, new SameCourse()), true);
    t.checkExpect(this.econ1115.students.contains(this.vandoorne, new SameStudent()), true);
    t.checkExpect(this.fina2201.students.contains(this.vandoorne, new SameStudent()), false);
    t.checkExpect(this.hulkenburg.courses.contains(this.cs3200, new SameCourse()), true);
    t.checkExpect(this.hulkenburg.courses.contains(this.cs5200, new SameCourse()), true);
  }

  // Tests the helper for the classmates method
  void testClassmatesHelp(Tester t) {
    // Reset data to default
    this.initData();

    t.checkExpect(this.wolff.courses.classmatesHelp(this.vandoorne.courses), true);
    t.checkExpect(this.wolff.courses.classmatesHelp(this.eaton.courses), true);
    t.checkExpect(this.hulkenburg.courses.classmatesHelp(this.russell.courses), true);
    t.checkExpect(this.hulkenburg.courses.classmatesHelp(this.wolff.courses), false);
    t.checkExpect(this.vandoorne.courses.classmatesHelp(this.eaton.courses), false);
    t.checkExpect(this.eaton.courses.classmatesHelp(this.russell.courses), false);
  }

  // Tests the classmates method
  void testClassmates(Tester t) {
    // Reset data to default
    this.initData();

    t.checkExpect(this.wolff.classmates(this.vandoorne), true);
    t.checkExpect(this.wolff.classmates(this.eaton), true);
    t.checkExpect(this.hulkenburg.classmates(this.russell), true);
    t.checkExpect(this.hulkenburg.classmates(this.wolff), false);
    t.checkExpect(this.vandoorne.classmates(this.eaton), false);
    t.checkExpect(this.eaton.classmates(this.russell), false);

    this.hulkenburg.enroll(this.fina2201sec2);
    t.checkExpect(this.hulkenburg.classmates(this.vandoorne), false);
  }

  // Tests the countList method
  void testCountList(Tester t) {
    // Reset data to default
    this.initData();

    t.checkExpect(this.eaton.courses.countList(this.vandoorne.courses, new SameCourse()), 0);
    t.checkExpect(this.vandoorne.courses.countList(this.wolff.courses, new SameCourse()), 1);
    t.checkExpect(this.econ1115.students.countList(this.econ1116.students, new SameStudent()), 1);
    t.checkExpect(this.fina2201.students.countList(this.cs5200.students, new SameStudent()), 1);
    this.russell.enroll(this.econ1115);
    t.checkExpect(this.vandoorne.courses.countList(this.russell.courses, new SameCourse()), 2);
    this.vandoorne.enroll(this.cs3200);
    t.checkExpect(this.russell.courses.countList(this.vandoorne.courses, new SameCourse()), 3);
    t.checkExpect(new MtList<Course>().countList(this.hulkenburg.courses, new SameCourse()), 0);
  }

  // Tests the samelist method
  void testSameList(Tester t) {
    // Reset data to default
    this.initData();

    t.checkExpect(this.hulkenburg.courses.sameList(this.rachlin.courses, new SameCourse()), true);
    t.checkExpect(this.eaton.courses.sameList(this.eaton.courses, new SameCourse()), true);
    t.checkExpect(this.fina2201.students.sameList(this.cs3200.students, new SameStudent()), false);
    t.checkExpect(this.wolff.courses.sameList(this.russell.courses, new SameCourse()), false);
    t.checkExpect(this.fina2201.students.sameList(new MtList<Student>(), new SameCourse()), false);
  }

  // Tests the dejavu method
  void testDejavu(Tester t) {
    // Reset data to default
    this.initData();

    t.checkExpect(this.swasey.dejavu(this.vandoorne), false);
    t.checkExpect(this.rachlin.dejavu(this.hulkenburg), true);
    t.checkExpect(this.simon.dejavu(this.vandoorne), true);
    t.checkExpect(this.rachlin.dejavu(this.russell), false);
  }
}