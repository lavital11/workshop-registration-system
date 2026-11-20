package patmal.workshops.student.impl.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.model.InstructorImpl;
import patmal.workshops.student.impl.model.WorkshopImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Thin delegator (section 3.2's recommendation): all the storage and the
// per-entity rules live in the 4 manager classes below. This class is left
// with only the operations that coordinate between two managers at once
// (associating a workshop with an instructor, and the two "students of X"
// queries that cross the team/workshop/instructor relationship).
public class WorkshopSystemImpl implements WorkshopSystem {

    private static final Logger LOGGER = LogManager.getLogger(WorkshopSystemImpl.class);

    private final InstructorManager instructorManager = new InstructorManager();
    private final StudentManager studentManager = new StudentManager();
    private final WorkshopManager workshopManager = new WorkshopManager();
    private final TeamManager teamManager = new TeamManager(studentManager);
    private final JsonExporter jsonExporter = new JsonExporter();
    private final CsvImporter csvImporter = new CsvImporter(studentManager);

    public WorkshopSystemImpl() {
    }

    @Override
    public Instructor createInstructor(String id, String name, String email) {
        return instructorManager.create(id, name, email);
    }

    @Override
    public Student createStudent(String id, String name, String email) {
        return studentManager.create(id, name, email);
    }

    @Override
    public Workshop createWorkshop(String name, String description) {
        return workshopManager.create(name, description);
    }

    @Override
    public void associateWorkshopWithInstructor(Instructor instructor, Workshop workshop) {
        InstructorImpl instructorImpl = instructor == null
                ? null : instructorManager.get(instructor.getId());
        WorkshopImpl workshopImpl = workshop == null
                ? null : workshopManager.get(workshop.getId());
        if (instructorImpl == null) {
            LOGGER.warn("Rejected association: unknown instructor {}.", instructor);
            throw new IllegalArgumentException("Unknown instructor: " + instructor);
        }
        if (workshopImpl == null) {
            LOGGER.warn("Rejected association: unknown workshop {}.", workshop);
            throw new IllegalArgumentException("Unknown workshop: " + workshop);
        }

        // if the workshop already had a different instructor, detach it first
        Instructor previousInstructor = workshopImpl.getInstructor();
        if (previousInstructor != null) {
            InstructorImpl previousImpl = instructorManager.get(previousInstructor.getId());
            if (previousImpl != null) {
                previousImpl.removeWorkshop(workshopImpl);
            }
        }
        workshopImpl.setInstructor(instructorImpl);
        instructorImpl.addWorkshop(workshopImpl);
    }

    @Override
    public Team createTeam(String name, Workshop workshop, List<String> members) {
        WorkshopImpl workshopImpl = workshop == null ? null : workshopManager.get(workshop.getId());
        if (workshopImpl == null) {
            LOGGER.warn("Rejected team creation: unknown workshop {}.", workshop);
            throw new IllegalArgumentException("Unknown workshop: " + workshop);
        }
        return teamManager.create(name, workshopImpl, members);
    }

    // ---------- queries ----------

    @Override
    public List<Instructor> getInstructors() {
        return instructorManager.getAll();
    }

    @Override
    public Instructor getInstructor(String id) {
        return instructorManager.get(id);
    }

    @Override
    public List<Student> getStudents() {
        return studentManager.getAll();
    }

    @Override
    public Student getStudent(String id) {
        return studentManager.get(id);
    }

    @Override
    public List<Workshop> getWorkshops() {
        return workshopManager.getAll();
    }

    @Override
    public Workshop getWorkshop(Integer id) {
        return workshopManager.get(id);
    }

    @Override
    public List<Team> getTeams() {
        return teamManager.getAll();
    }

    @Override
    public Team getTeam(Integer id) {
        return teamManager.get(id);
    }

    @Override
    public List<Student> getFreeStudents() {
        return studentManager.getFree();
    }

    @Override
    public List<Student> getStudentsOfInstructor(String instructorId) {
        Instructor instructor = instructorManager.get(instructorId);
        if (instructor == null) {
            return new ArrayList<>();
        }

        Set<Integer> workshopIds = new HashSet<>();
        for (Workshop workshop : instructor.getWorkshops()) {
            workshopIds.add(workshop.getId());
        }

        Set<Student> result = new LinkedHashSet<>();
        for (Team team : teamManager.getAll()) {
            Workshop teamWorkshop = team.getWorkshop();
            if (teamWorkshop != null && workshopIds.contains(teamWorkshop.getId())) {
                result.addAll(team.getStudents());
            }
        }
        return new ArrayList<>(result);
    }

    @Override
    public List<Student> getStudentsOfWorkshop(Integer workshopId) {
        Workshop workshop = workshopManager.get(workshopId);
        if (workshop == null) {
            return new ArrayList<>();
        }

        Set<Student> result = new LinkedHashSet<>();
        for (Team team : teamManager.getAll()) {
            Workshop teamWorkshop = team.getWorkshop();
            if (teamWorkshop != null && workshopId.equals(teamWorkshop.getId())) {
                result.addAll(team.getStudents());
            }
        }
        return new ArrayList<>(result);
    }

    // ---------- JSON export (API 2.0, section 5.1) ----------

    @Override
    public void exportInstructorsDataToJson(File targetFileName) {
        jsonExporter.exportInstructors(instructorManager.getAll(), teamManager.getAll(),
                targetFileName);
    }

    @Override
    public void exportStudentsDataToJson(File targetFileName) {
        jsonExporter.exportStudents(studentManager.getAll(), teamManager.getAll(),
                targetFileName);
    }

    @Override
    public void exportTeamsDataToJson(File targetFileName) {
        jsonExporter.exportTeams(teamManager.getAll(), targetFileName);
    }

    // ---------- CSV import (bonus, section 5.1/4.4) ----------

    @Override
    public List<Student> importStudentDataFromFile(File source) {
        return csvImporter.importFrom(source);
    }
}
