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

/**
 * In-memory implementation of {@link WorkshopSystem}.
 *
 * <p><b>Identifiers.</b> Instructor and student ids are supplied by the
 * caller and must be unique within their own kind. Workshop and team ids are
 * assigned by this class as sequential integers starting from 1, in creation
 * order, each kind having its own counter. A counter is only advanced after
 * every validation for the corresponding create-operation has already
 * passed, so a rejected operation never consumes an id.</p>
 *
 * <p><b>Validation.</b> Every public mutator validates its arguments
 * completely before touching any internal state. This guarantees that a
 * rejected call (one that throws) leaves the system exactly as it was
 * before the call - nothing is "half applied".</p>
 *
 * <p><b>Unknown keys.</b> This implementation's chosen (and documented, as
 * required by the exercise) behaviour is: a lookup of a single object by an
 * id that does not exist returns {@code null}; a lookup that returns a
 * collection for an id that does not exist returns an empty list. No
 * exception is thrown for an unknown id.</p>
 *
 * <p><b>Returned lists.</b> Every method returning a {@link List} returns a
 * fresh copy that contains no duplicates and is never {@code null}, so that
 * callers can never corrupt this object's internal state by mutating the
 * list they got back.</p>
 */
public class WorkshopSystemImpl implements WorkshopSystem {

    private final Map<String, InstructorImpl> instructors = new LinkedHashMap<>();
    private final Map<String, StudentImpl> students = new LinkedHashMap<>();
    private final Map<Integer, WorkshopImpl> workshops = new LinkedHashMap<>();
    private final Map<Integer, TeamImpl> teams = new LinkedHashMap<>();

    /** Next id to assign to a workshop; starts at 1 as required. */
    private int nextWorkshopId = 1;

    /** Next id to assign to a team; starts at 1 as required. */
    private int nextTeamId = 1;

    /** Public no-argument constructor, required so the tester can instantiate this class. */
    public WorkshopSystemImpl() {
    }

    // ------------------------------------------------------------------
    // Creation
    // ------------------------------------------------------------------

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
        // Workshop creation cannot fail, so the id counter can be consumed directly.
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

        // Keep both sides of the relation consistent, including the case
        // where the workshop already had a (different) instructor.
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

        // Validate every member before mutating anything, so a rejected
        // call leaves every student exactly as free/assigned as before.
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

        // All validations passed: only now is the id counter consumed and
        // the state actually mutated.
        TeamImpl team = new TeamImpl(nextTeamId, name, workshopImpl);
        nextTeamId++;
        for (StudentImpl studentImpl : resolvedMembers) {
            team.addStudent(studentImpl);
            studentImpl.setTeam(team);
        }
        teams.put(team.getId(), team);
        return team;
    }

    // ------------------------------------------------------------------
    // Querying
    // ------------------------------------------------------------------

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
