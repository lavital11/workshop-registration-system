package patmal.workshops.student.impl.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.logic.WorkshopSystemImpl;

import java.io.File;
import java.util.List;

// Demo application (section 6.1). With the CSV bonus (8.1) done, the demo
// students are no longer created in code: they come from a students.csv file
// (bundled in src/main/resources, also provided as a program argument), via
// importStudentDataFromFile.
public final class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            LOGGER.error("Usage: java -jar workshop-cli.jar <path-to-students.csv>");
            return;
        }

        WorkshopSystem system = new WorkshopSystemImpl();

        Instructor dana = system.createInstructor("i1", "Dana Levi", "dana.levi@univ.edu");
        Workshop javaWorkshop = system.createWorkshop("Java Workshop", "Intro to Java");
        Workshop webWorkshop = system.createWorkshop("Web Workshop", "Intro to Web");
        system.associateWorkshopWithInstructor(dana, javaWorkshop);
        system.associateWorkshopWithInstructor(dana, webWorkshop);

        List<Student> imported = system.importStudentDataFromFile(new File(args[0]));
        LOGGER.info("Imported {} student(s) from {}.", imported.size(), args[0]);

        // students.csv has s1..s4: s1/s2 join Alpha, s3 joins Beta, s4 stays free
        system.createTeam("Alpha", javaWorkshop, List.of("s1", "s2"));
        system.createTeam("Beta", webWorkshop, List.of("s3"));

        LOGGER.info("Demo data ready: 1 instructor, 2 workshops, {} students, 2 teams.",
                imported.size());

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
