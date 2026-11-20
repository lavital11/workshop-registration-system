package patmal.workshops.student.impl.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.model.Team;
import patmal.workshops.student.impl.model.StudentImpl;
import patmal.workshops.student.impl.model.TeamImpl;
import patmal.workshops.student.impl.model.WorkshopImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Owns team creation and storage. Depends on StudentManager to resolve and
// validate the member ids a team is created with.
final class TeamManager {

    private static final Logger LOGGER = LogManager.getLogger(TeamManager.class);

    private final StudentManager studentManager;
    private final Map<Integer, TeamImpl> teams = new LinkedHashMap<>();
    private int nextTeamId = 1;

    TeamManager(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    TeamImpl create(String name, WorkshopImpl workshop, List<String> members) {
        if (members == null) {
            LOGGER.warn("Rejected team creation: members must not be null.");
            throw new IllegalArgumentException("members must not be null");
        }
        if (members.isEmpty()) {
            LOGGER.warn("Rejected team creation: a team needs at least one member.");
            throw new IllegalArgumentException("A team must have at least one student member.");
        }

        // check all members first, before changing any state
        Set<String> seenIds = new HashSet<>();
        List<StudentImpl> resolvedMembers = new ArrayList<>();
        for (String studentId : members) {
            if (!seenIds.add(studentId)) {
                LOGGER.warn("Rejected team creation: student '{}' listed twice.", studentId);
                throw new IllegalArgumentException(
                        "Student listed twice in the same team: " + studentId);
            }
            StudentImpl studentImpl = studentManager.get(studentId);
            if (studentImpl == null) {
                LOGGER.warn("Rejected team creation: unknown student id '{}'.", studentId);
                throw new IllegalArgumentException("Unknown student id: " + studentId);
            }
            if (studentImpl.getTeam() != null) {
                LOGGER.warn("Rejected team creation: student '{}' already has a team.",
                        studentId);
                throw new IllegalArgumentException(
                        "Student '" + studentId + "' is already in another team.");
            }
            resolvedMembers.add(studentImpl);
        }

        // everything checked out, now actually create the team
        TeamImpl team = new TeamImpl(nextTeamId, name, workshop);
        nextTeamId++;
        for (StudentImpl studentImpl : resolvedMembers) {
            team.addStudent(studentImpl);
            studentImpl.setTeam(team);
        }
        teams.put(team.getId(), team);
        LOGGER.info("Created team {} ('{}') with {} member(s).",
                team.getId(), name, resolvedMembers.size());
        return team;
    }

    TeamImpl get(Integer id) {
        return teams.get(id);
    }

    List<Team> getAll() {
        return new ArrayList<>(teams.values());
    }
}
