package patmal.workshops.student.impl.logic;

import org.junit.jupiter.api.Test;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Workshop;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// JUnit port of the plain-Java smoke tests from EX1 (tests/ConventionChecks.java):
// the equals/hashCode fix and the two behavioral requirements called out in the
// review (empty team rejected, reassigning a workshop keeps both sides consistent).
class ConventionChecksTest {

    @Test
    void equalsAndHashCodeAreBasedOnId() {
        WorkshopSystemImpl systemA = new WorkshopSystemImpl();
        WorkshopSystemImpl systemB = new WorkshopSystemImpl();

        Student s1FromA = systemA.createStudent("s1", "Alice", "alice@example.com");
        Student s1FromB = systemB.createStudent("s1", "Alice Clone", "clone@example.com");
        Student s2FromA = systemA.createStudent("s2", "Bob", "bob@example.com");

        assertTrue(s1FromA.equals(s1FromB), "same id -> equal");
        assertEquals(s1FromA.hashCode(), s1FromB.hashCode(), "equal objects -> equal hashCode");
        assertFalse(s1FromA.equals(s2FromA), "different id -> not equal");
        assertFalse(s1FromA.equals(null), "not equal to null");
        assertTrue(s1FromA.equals(s1FromA), "reflexive");
    }

    @Test
    void studentAndInstructorWithSameIdAreNotEqual() {
        WorkshopSystemImpl system = new WorkshopSystemImpl();
        Instructor instructor = system.createInstructor("p1", "Carol", "carol@example.com");
        Student student = system.createStudent("p1", "Carol", "carol@example.com");

        assertFalse(student.equals(instructor),
                "student and instructor with same id -> not equal");
        assertFalse(instructor.equals(student),
                "instructor and student with same id -> not equal (symmetric)");
    }

    @Test
    void emptyMemberTeamIsRejected() {
        WorkshopSystemImpl system = new WorkshopSystemImpl();
        Workshop workshop = system.createWorkshop("Java", "Intro to Java");

        assertThrows(IllegalArgumentException.class,
                () -> system.createTeam("Empty", workshop, List.of()));
    }

    @Test
    void reassigningWorkshopKeepsBothSidesConsistent() {
        WorkshopSystemImpl system = new WorkshopSystemImpl();
        Instructor instructorA = system.createInstructor("i1", "Dan", "dan@example.com");
        Instructor instructorB = system.createInstructor("i2", "Eve", "eve@example.com");
        Workshop workshop = system.createWorkshop("Web", "Intro to Web");

        system.associateWorkshopWithInstructor(instructorA, workshop);
        assertEquals(instructorA, system.getWorkshop(workshop.getId()).getInstructor(),
                "workshop starts with instructor A");
        assertTrue(system.getInstructor("i1").getWorkshops().contains(workshop),
                "instructor A teaches the workshop");

        system.associateWorkshopWithInstructor(instructorB, workshop);
        assertEquals(instructorB, system.getWorkshop(workshop.getId()).getInstructor(),
                "workshop now has instructor B");
        assertTrue(system.getInstructor("i2").getWorkshops().contains(workshop),
                "instructor B teaches the workshop");
        assertFalse(system.getInstructor("i1").getWorkshops().contains(workshop),
                "instructor A no longer teaches the workshop");
    }
}
