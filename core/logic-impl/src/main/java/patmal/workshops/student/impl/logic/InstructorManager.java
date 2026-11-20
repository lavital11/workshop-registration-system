package patmal.workshops.student.impl.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.student.impl.model.InstructorImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Owns instructor creation and storage. Nothing outside the logic-impl package
// talks to this class directly - WorkshopSystemImpl delegates to it.
final class InstructorManager {

    private static final Logger LOGGER = LogManager.getLogger(InstructorManager.class);

    private final Map<String, InstructorImpl> instructors = new LinkedHashMap<>();

    InstructorImpl create(String id, String name, String email) {
        if (id == null) {
            LOGGER.warn("Rejected instructor creation: id must not be null.");
            throw new IllegalArgumentException("Instructor id must not be null.");
        }
        if (!EmailValidation.isValid(email)) {
            LOGGER.warn("Rejected instructor creation for id '{}': invalid e-mail address.", id);
            throw new IllegalArgumentException("Invalid e-mail address: " + email);
        }
        if (instructors.containsKey(id)) {
            LOGGER.warn("Rejected instructor creation: id '{}' already exists.", id);
            throw new IllegalArgumentException(
                    "An instructor with id '" + id + "' already exists.");
        }
        InstructorImpl instructor = new InstructorImpl(id, name, email);
        instructors.put(id, instructor);
        LOGGER.info("Created instructor '{}' ({}).", id, name);
        return instructor;
    }

    InstructorImpl get(String id) {
        return instructors.get(id);
    }

    List<Instructor> getAll() {
        return new ArrayList<>(instructors.values());
    }
}
