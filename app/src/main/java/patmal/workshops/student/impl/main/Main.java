package patmal.workshops.student.impl.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.logic.WorkshopSystemImpl;

import java.io.File;
import java.util.List;

// Demo application (section 6.1): builds a small WorkshopSystem, exports it
// with the three JSON methods into the current folder, and logs what it did.
public final class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        WorkshopSystem system = new WorkshopSystemImpl();

        Instructor dana = system.createInstructor("i1", "Dana Levi", "dana.levi@univ.edu");
        Workshop javaWorkshop = system.createWorkshop("Java Workshop", "Intro to Java");
        Workshop webWorkshop = system.createWorkshop("Web Workshop", "Intro to Web");
        system.associateWorkshopWithInstructor(dana, javaWorkshop);
        system.associateWorkshopWithInstructor(dana, webWorkshop);

        system.createStudent("s1", "Alice Cohen", "alice@students.edu");
        system.createStudent("s2", "Bob Katz", "bob@students.edu");
        system.createStudent("s3", "Carol Mizrahi", "carol@students.edu");
        system.createStudent("s4", "David Peretz", "david@students.edu"); // stays free

        system.createTeam("Alpha", javaWorkshop, List.of("s1", "s2"));
        system.createTeam("Beta", webWorkshop, List.of("s3"));

        LOGGER.info("Demo data ready: 1 instructor, 2 workshops, 4 students "
                + "(1 free), 2 teams.");

        File instructorsFile = new File("instructors.json");
        File studentsFile = new File("students.json");
        File teamsFile = new File("teams.json");

        system.exportInstructorsDataToJson(instructorsFile);
        system.exportStudentsDataToJson(studentsFile);
        system.exportTeamsDataToJson(teamsFile);

        LOGGER.info("Wrote {}", instructorsFile.getAbsolutePath());
        LOGGER.info("Wrote {}", studentsFile.getAbsolutePath());
        LOGGER.info("Wrote {}", teamsFile.getAbsolutePath());
    }
}
