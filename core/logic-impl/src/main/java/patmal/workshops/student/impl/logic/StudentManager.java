package patmal.workshops.student.impl.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.model.Student;
import patmal.workshops.student.impl.model.StudentImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Owns student creation and storage, including which students are still free
// (not yet on a team).
final class StudentManager {

    private static final Logger LOGGER = LogManager.getLogger(StudentManager.class);

    private final Map<String, StudentImpl> students = new LinkedHashMap<>();

    StudentImpl create(String id, String name, String email) {
        if (id == null) {
            LOGGER.warn("Rejected student creation: id must not be null.");
            throw new IllegalArgumentException("Student id must not be null.");
        }
        if (!EmailValidation.isValid(email)) {
            LOGGER.warn("Rejected student creation for id '{}': invalid e-mail address.", id);
            throw new IllegalArgumentException("Invalid e-mail address: " + email);
        }
        if (students.containsKey(id)) {
            LOGGER.warn("Rejected student creation: id '{}' already exists.", id);
            throw new IllegalArgumentException("A student with id '" + id + "' already exists.");
        }
        StudentImpl student = new StudentImpl(id, name, email);
        students.put(id, student);
        LOGGER.info("Created student '{}' ({}).", id, name);
        return student;
    }

    StudentImpl get(String id) {
        return students.get(id);
    }

    List<Student> getAll() {
        return new ArrayList<>(students.values());
    }

    List<Student> getFree() {
        List<Student> free = new ArrayList<>();
        for (StudentImpl student : students.values()) {
            if (student.getTeam() == null) {
                free.add(student);
            }
        }
        return free;
    }
}
