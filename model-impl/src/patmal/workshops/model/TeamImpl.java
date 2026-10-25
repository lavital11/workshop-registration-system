package patmal.workshops.model;

import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of {@link Team}.
 *
 * <p>The id is assigned once, by the logic layer, as a sequential number
 * starting from 1, and never changes afterwards. The workshop is fixed at
 * creation time (a team always belongs to exactly one workshop). Members are
 * added through {@link #addStudent}, an internal mutator that is not part of
 * the public {@link Team} API - the logic layer validates every member
 * before adding any of them, so a team is only ever in a fully valid state.</p>
 */
public class TeamImpl implements Team {

    private final Integer id;
    private final String name;
    private final Workshop workshop;
    private final List<Student> students = new ArrayList<>();

    public TeamImpl(Integer id, String name, Workshop workshop) {
        this.id = id;
        this.name = name;
        this.workshop = workshop;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Student> getStudents() {
        // Defensive copy: callers must not be able to corrupt our internal state.
        return new ArrayList<>(students);
    }

    @Override
    public Workshop getWorkshop() {
        return workshop;
    }

    /**
     * Internal mutator used by the logic layer while creating the team.
     * Not part of the public {@link Team} API.
     */
    public void addStudent(Student student) {
        if (!students.contains(student)) {
            students.add(student);
        }
    }

    @Override
    public String toString() {
        return "Team{id=" + id + ", name=" + name + "}";
    }
}
