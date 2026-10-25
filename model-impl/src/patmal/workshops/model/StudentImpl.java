package patmal.workshops.model;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;

import java.util.ArrayList;
import java.util.List;

// Most of a student's info (team name, instructor, team mates) is derived
// from the team they're in, so we only keep a reference to the team here
// instead of storing that data separately.
public class StudentImpl implements Student {

    private final String id;
    private final String name;
    private final String email;

    // null means the student is free (no team)
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

    // called from WorkshopSystemImpl to put the student in a team (or take them out)
    public void setTeam(Team team) {
        this.team = team;
    }

    // used by WorkshopSystemImpl to check if this student is free
    public Team getTeam() {
        return team;
    }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name=" + name + "}";
    }
}
