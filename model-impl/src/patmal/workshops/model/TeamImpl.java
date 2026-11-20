package patmal.workshops.model;

import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class TeamImpl implements Team {

    private final Integer id;
    private final String name;
    private final Workshop workshop;
    private final Set<Student> students = new LinkedHashSet<>();

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
        // return a copy so callers can't mess with our internal list
        return new ArrayList<>(students);
    }

    @Override
    public Workshop getWorkshop() {
        return workshop;
    }

    // called from WorkshopSystemImpl while building the team
    public void addStudent(Student student) {
        students.add(student);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TeamImpl other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Team{id=" + id + ", name=" + name + "}";
    }
}
