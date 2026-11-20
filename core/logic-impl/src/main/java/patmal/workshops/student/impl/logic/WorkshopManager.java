package patmal.workshops.student.impl.logic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import patmal.workshops.api.model.Workshop;
import patmal.workshops.student.impl.model.WorkshopImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Owns workshop creation and storage. Creating a workshop can't fail, so there
// is no rejection path here - just the next id and a log line.
final class WorkshopManager {

    private static final Logger LOGGER = LogManager.getLogger(WorkshopManager.class);

    private final Map<Integer, WorkshopImpl> workshops = new LinkedHashMap<>();
    private int nextWorkshopId = 1;

    WorkshopImpl create(String name, String description) {
        WorkshopImpl workshop = new WorkshopImpl(nextWorkshopId, name, description);
        nextWorkshopId++;
        workshops.put(workshop.getId(), workshop);
        LOGGER.info("Created workshop {} ('{}').", workshop.getId(), name);
        return workshop;
    }

    WorkshopImpl get(Integer id) {
        return id == null ? null : workshops.get(id);
    }

    List<Workshop> getAll() {
        return new ArrayList<>(workshops.values());
    }
}
