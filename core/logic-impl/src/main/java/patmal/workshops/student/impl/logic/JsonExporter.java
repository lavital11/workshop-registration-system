package patmal.workshops.student.impl.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Person;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

// Builds the 3 export documents of section 5.1 as Jackson trees, one node at a
// time, so a child never points back to one of its ancestors: a team lists its
// students by details only (never their team again), an instructor's workshops
// list their teams by details only (never the instructor again), and so on.
// None of this reads or changes any manager state - it is purely handed the
// lists it needs.
final class JsonExporter {

    private final ObjectMapper mapper = new ObjectMapper();

    void exportInstructors(List<Instructor> instructors, List<Team> teams, File targetFile) {
        ArrayNode root = mapper.createArrayNode();
        for (Instructor instructor : instructors) {
            ObjectNode instructorNode = personNode(instructor);

            ArrayNode workshopsNode = instructorNode.putArray("workshops");
            for (Workshop workshop : instructor.getWorkshops()) {
                ObjectNode workshopNode = mapper.createObjectNode();
                workshopNode.put("id", workshop.getId());
                workshopNode.put("name", workshop.getName());
                workshopNode.put("description", workshop.getDescription());

                ArrayNode teamsNode = workshopNode.putArray("teams");
                for (Team team : teamsOfWorkshop(teams, workshop)) {
                    ObjectNode teamNode = mapper.createObjectNode();
                    teamNode.put("id", team.getId());
                    teamNode.put("name", team.getName());
                    ArrayNode studentsNode = teamNode.putArray("students");
                    for (Student student : team.getStudents()) {
                        studentsNode.add(personNode(student));
                    }
                    teamsNode.add(teamNode);
                }
                workshopsNode.add(workshopNode);
            }
            root.add(instructorNode);
        }
        write(root, targetFile);
    }

    void exportStudents(List<Student> students, List<Team> teams, File targetFile) {
        ArrayNode root = mapper.createArrayNode();
        for (Student student : students) {
            ObjectNode studentNode = personNode(student);
            Team team = teamOfStudent(teams, student);
            studentNode.set("team", team == null ? null : teamWithWorkshopNode(team));
            root.add(studentNode);
        }
        write(root, targetFile);
    }

    void exportTeams(List<Team> teams, File targetFile) {
        ArrayNode root = mapper.createArrayNode();
        for (Team team : teams) {
            ObjectNode teamNode = mapper.createObjectNode();
            teamNode.put("id", team.getId());
            teamNode.put("name", team.getName());

            ArrayNode studentsNode = teamNode.putArray("students");
            for (Student student : team.getStudents()) {
                studentsNode.add(personNode(student));
            }
            teamNode.set("workshop", workshopWithInstructorNode(team.getWorkshop()));
            root.add(teamNode);
        }
        write(root, targetFile);
    }

    // ---------- shared node builders ----------

    private ObjectNode personNode(Person person) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", person.getId());
        node.put("name", person.getName());
        node.put("email", person.getEmail());
        return node;
    }

    // team + the ids of all its students, its workshop and the workshop's instructor
    private ObjectNode teamWithWorkshopNode(Team team) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", team.getId());
        node.put("name", team.getName());
        ArrayNode studentIds = node.putArray("studentIds");
        for (Student member : team.getStudents()) {
            studentIds.add(member.getId());
        }
        node.set("workshop", workshopWithInstructorNode(team.getWorkshop()));
        return node;
    }

    // workshop + its instructor (or null)
    private ObjectNode workshopWithInstructorNode(Workshop workshop) {
        if (workshop == null) {
            return null;
        }
        ObjectNode node = mapper.createObjectNode();
        node.put("id", workshop.getId());
        node.put("name", workshop.getName());
        node.put("description", workshop.getDescription());
        Instructor instructor = workshop.getInstructor();
        node.set("instructor", instructor == null ? null : personNode(instructor));
        return node;
    }

    private List<Team> teamsOfWorkshop(List<Team> teams, Workshop workshop) {
        return teams.stream()
                .filter(team -> team.getWorkshop() != null
                        && team.getWorkshop().getId().equals(workshop.getId()))
                .collect(Collectors.toList());
    }

    private Team teamOfStudent(List<Team> teams, Student student) {
        for (Team team : teams) {
            if (team.getStudents().contains(student)) {
                return team;
            }
        }
        return null;
    }

    private void write(ArrayNode root, File targetFile) {
        try {
            Files.writeString(targetFile.toPath(), mapper.writeValueAsString(root),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
