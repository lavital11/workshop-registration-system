package patmal.workshops.model;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of {@link Student}.
 *
 * <p>Most of a student's data is <em>derived</em> from the team the student
 * currently belongs to, rather than duplicated: this object only stores a
 * reference to its {@link Team} (or {@code null} for a free student) and
 * computes {@link #getTeamName()}, {@link #getInstructor()} and
 * {@link #getTeamMates()} on demand. This makes it impossible for the
 * derived data to drift out of sync with the team it came from.</p>
 */
public class StudentImpl implements Student {

    private final String id;
    private final String name;
    private final String email;

    /** null means the student is free (not in any team). */
    private Team team;

    public StudentImpl(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getTeamName() {
        return team == null ? null : team.getName();
    }

    @Override
    public Instructor getInstructor() {
        if (team == null || team.getWorkshop() == null) {
            return null;
        }
        return team.getWorkshop().getInstructor();
    }

    @Override
    public List<Student> getTeamMates() {
        List<Student> mates = new ArrayList<>();
        if (team != null) {
            for (Student member : team.getStudents()) {
                if (!member.getId().equals(this.id)) {
                    mates.add(member);
                }
            }
        }
        return mates;
    }

    /**
     * Internal mutator used by the logic layer to assign (or clear) team
     * membership. Not part of the public {@link Student} API.
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Internal accessor used by the logic layer to check whether this
     * student is free, without needing extra bookkeeping of its own. Not
     * part of the public {@link Student} API.
     */
    public Team getTeam() {
        return team;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name=" + name + "}";
    }
}
