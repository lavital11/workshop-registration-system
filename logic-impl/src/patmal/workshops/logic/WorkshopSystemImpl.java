package patmal.workshops.logic;

import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.model.InstructorImpl;
import patmal.workshops.model.StudentImpl;
import patmal.workshops.model.TeamImpl;
import patmal.workshops.model.WorkshopImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Implementation notes:
// - instructor/student ids come from the caller and must be unique.
// - workshop/team ids are assigned here, starting from 1, and only once the
//   corresponding create call has passed validation (so a rejected call
//   never "uses up" an id).
// - all create/associate calls validate everything first, then mutate state,
//   so a failed call leaves the system exactly as it was.
// - unknown ids: getX(id) returns null, getXsOfY(id) returns an empty list.
// - every getter returns a copy of the internal list, never the list itself.
public class WorkshopSystemImpl implements WorkshopSystem {

    private final Map<String, InstructorImpl> instructors = new LinkedHashMap<>();
    private final Map<String, StudentImpl> students = new LinkedHashMap<>();
    private final Map<Integer, WorkshopImpl> workshops = new LinkedHashMap<>();
    private final Map<Integer, TeamImpl> teams = new LinkedHashMap<>();

    private int nextWorkshopId = 1;
    private int nextTeamId = 1;

    public WorkshopSystemImpl() {
    }

    // ---------- creation ----------

    @Override
    public Instructor createInstructor(String id, String name, String email) {
        if (id == null || instructors.containsKey(id)) {
            throw new IllegalArgumentException("An instructor with id '" + id + "' already exists (or id is null).");
        }
        InstructorImpl instructor = new InstructorImpl(id, name, email);
        instructors.put(id, instructor);
        return instructor;
    }

    @Override
    public Student createStudent(String id, String name, String email) {
        if (id == null || students.containsKey(id)) {
            throw new IllegalArgumentException("A student with id '" + id + "' already exists (or id is null).");
        }
        StudentImpl student = new StudentImpl(id, name, email);
        students.put(id, student);
        return student;
    }

    @Override
    public Workshop createWorkshop(String name, String description) {
        // creating a workshop can't fail, so just grab the next id
        WorkshopImpl workshop = new WorkshopImpl(nextWorkshopId, name, description);
        nextWorkshopId++;
        workshops.put(workshop.getId(), workshop);
        return workshop;
    }

    @Override
    public void associateWorkshopWithInstructor(Instructor instructor, Workshop workshop) {
        InstructorImpl instructorImpl = instructor == null ? null : instructors.get(instructor.getId());
        WorkshopImpl workshopImpl = workshop == null ? null : workshops.get(workshop.getId());
        if (instructorImpl == null) {
            throw new IllegalArgumentException("Unknown instructor: " + instructor);
        }
        if (workshopImpl == null) {
            throw new IllegalArgumentException("Unknown workshop: " + workshop);
        }

        // if the workshop already had a different instructor, detach it first
        Instructor previousInstructor = workshopImpl.getInstructor();
        if (previousInstructor != null) {
            InstructorImpl previousImpl = instructors.get(previousInstructor.getId());
            if (previousImpl != null) {
                previousImpl.removeWorkshop(workshopImpl);
            }
        }
        workshopImpl.setInstructor(instructorImpl);
        instructorImpl.addWorkshop(workshopImpl);
    }

    @Override
    public Team createTeam(String name, Workshop workshop, List<String> members) {
        WorkshopImpl workshopImpl = workshop == null ? null : workshops.get(workshop.getId());
        if (workshopImpl == null) {
            throw new IllegalArgumentException("Unknown workshop: " + workshop);
        }
        if (members == null) {
            throw new IllegalArgumentException("members must not be null");
        }

        // check all members first, before changing any state
        Set<String> seenIds = new HashSet<>();
        List<StudentImpl> resolvedMembers = new ArrayList<>();
        for (String studentId : members) {
            if (!seenIds.add(studentId)) {
                throw new IllegalArgumentException("Student listed twice in the same team: " + studentId);
            }
            StudentImpl studentImpl = students.get(studentId);
            if (studentImpl == null) {
                throw new IllegalArgumentException("Unknown student id: " + studentId);
            }
            if (studentImpl.getTeam() != null) {
                throw new IllegalArgumentException("Student '" + studentId + "' is already in another team.");
            }
            resolvedMembers.add(studentImpl);
        }

        // everything checked out, now actually create the team
        TeamImpl team = new TeamImpl(nextTeamId, name, workshopImpl);
        nextTeamId++;
        for (StudentImpl studentImpl : resolvedMembers) {
            team.addStudent(studentImpl);
            studentImpl.setTeam(team);
        }
        teams.put(team.getId(), team);
        return team;
    }

    // ---------- queries ----------

    @Override
    public List<Instructor> getInstructors() {
        return new ArrayList<>(instructors.values());
    }

    @Override
    public Instructor getInstructor(String id) {
        return instructors.get(id);
    }

    @Override
    public List<Student> getStudents() {
        return new ArrayList<>(students.values());
    }

    @Override
    public Student getStudent(String id) {
        return students.get(id);
    }

    @Override
    public List<Workshop> getWorkshops() {
        return new ArrayList<>(workshops.values());
    }

    @Override
    public Workshop getWorkshop(Integer id) {
        return workshops.get(id);
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    @Override
    public Team getTeam(Integer id) {
        return teams.get(id);
    }

    @Override
    public List<Student> getFreeStudents() {
        List<Student> free = new ArrayList<>();
        for (StudentImpl student : students.values()) {
            if (student.getTeam() == null) {
                free.add(student);
            }
        }
        return free;
    }

    @Override
    public List<Student> getStudentsOfInstructor(String instructorId) {
        InstructorImpl instructor = instructors.get(instructorId);
        if (instructor == null) {
            return new ArrayList<>();
        }

        Set<Integer> workshopIds = new HashSet<>();
        for (Workshop w : instructor.getWorkshops()) {
            workshopIds.add(w.getId());
        }

        List<Student> result = new ArrayList<>();
        for (TeamImpl team : teams.values()) {
            Workshop teamWorkshop = team.getWorkshop();
            if (teamWorkshop != null && workshopIds.contains(teamWorkshop.getId())) {
                for (Student s : team.getStudents()) {
                    if (!result.contains(s)) {
                        result.add(s);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<Student> getStudentsOfWorkshop(Integer workshopId) {
        WorkshopImpl workshop = workshopId == null ? null : workshops.get(workshopId);
        if (workshop == null) {
            return new ArrayList<>();
        }

        List<Student> result = new ArrayList<>();
        for (TeamImpl team : teams.values()) {
            Workshop teamWorkshop = team.getWorkshop();
            if (teamWorkshop != null && workshopId.equals(teamWorkshop.getId())) {
                for (Student s : team.getStudents()) {
                    if (!result.contains(s)) {
                        result.add(s);
                    }
                }
            }
        }
        return result;
    }
}
