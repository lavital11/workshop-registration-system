package patmal.workshops.tests;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.logic.WorkshopSystemImpl;

import java.util.List;

// Plain-Java smoke tests for the equals/hashCode fix and the two behavioral
// requirements called out in the review (empty team rejected, reassigning a
// workshop keeps both sides consistent). Not shipped in the submitted jars.
public class ConventionChecks {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        checkEqualsAndHashCode();
        checkStudentAndInstructorWithSameIdAreNotEqual();
        checkEmptyTeamIsRejected();
        checkReassigningWorkshopKeepsBothSidesConsistent();

        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void checkEqualsAndHashCode() {
        WorkshopSystemImpl systemA = new WorkshopSystemImpl();
        WorkshopSystemImpl systemB = new WorkshopSystemImpl();

        Student s1FromA = systemA.createStudent("s1", "Alice", "alice@example.com");
        Student s1FromB = systemB.createStudent("s1", "Alice Clone", "clone@example.com");
        Student s2FromA = systemA.createStudent("s2", "Bob", "bob@example.com");

        check("same id -> equal", s1FromA.equals(s1FromB));
        check("equal objects -> equal hashCode", s1FromA.hashCode() == s1FromB.hashCode());
        check("different id -> not equal", !s1FromA.equals(s2FromA));
        check("not equal to null", !s1FromA.equals(null));
        check("reflexive", s1FromA.equals(s1FromA));
    }

    private static void checkStudentAndInstructorWithSameIdAreNotEqual() {
        WorkshopSystemImpl system = new WorkshopSystemImpl();
        Instructor instructor = system.createInstructor("p1", "Carol", "carol@example.com");
        Student student = system.createStudent("p1", "Carol", "carol@example.com");

        check("student and instructor with same id -> not equal", !student.equals(instructor));
        check("instructor and student with same id -> not equal (symmetric)",
                !instructor.equals(student));
    }

    private static void checkEmptyTeamIsRejected() {
        WorkshopSystemImpl system = new WorkshopSystemImpl();
        Workshop workshop = system.createWorkshop("Java", "Intro to Java");

        boolean rejected = false;
        try {
            system.createTeam("Empty", workshop, List.of());
        } catch (IllegalArgumentException e) {
            rejected = true;
        }
        check("empty-member team is rejected", rejected);
    }

    private static void checkReassigningWorkshopKeepsBothSidesConsistent() {
        WorkshopSystemImpl system = new WorkshopSystemImpl();
        Instructor instructorA = system.createInstructor("i1", "Dan", "dan@example.com");
        Instructor instructorB = system.createInstructor("i2", "Eve", "eve@example.com");
        Workshop workshop = system.createWorkshop("Web", "Intro to Web");

        system.associateWorkshopWithInstructor(instructorA, workshop);
        check("workshop starts with instructor A",
                system.getWorkshop(workshop.getId()).getInstructor().equals(instructorA));
        check("instructor A teaches the workshop",
                system.getInstructor("i1").getWorkshops().contains(workshop));

        system.associateWorkshopWithInstructor(instructorB, workshop);
        check("workshop now has instructor B",
                system.getWorkshop(workshop.getId()).getInstructor().equals(instructorB));
        check("instructor B teaches the workshop",
                system.getInstructor("i2").getWorkshops().contains(workshop));
        check("instructor A no longer teaches the workshop",
                !system.getInstructor("i1").getWorkshops().contains(workshop));
    }

    private static void check(String description, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + description);
            passed++;
        } else {
            System.out.println("[FAIL] " + description);
            failed++;
        }
    }
}
