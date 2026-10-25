package patmal.workshops.model;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.List;

public class InstructorImpl implements Instructor {

    private final String id;
    private final String name;
    private final String email;

    // workshops this instructor teaches
    private final List<Workshop> workshops = new ArrayList<>();

    public InstructorImpl(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public List<Workshop> getWorkshops() {
        // return a copy so callers can't mess with our internal list
        return new ArrayList<>(workshops);
    }

    // called from WorkshopSystemImpl when a workshop gets associated with this instructor
    public void addWorkshop(Workshop workshop) {
        if (!workshops.contains(workshop)) {
            workshops.add(workshop);
        }
    }

    // called from WorkshopSystemImpl if a workshop is re-assigned to a different instructor
    public void removeWorkshop(Workshop workshop) {
        workshops.remove(workshop);
    }

    @Override
    public String toString() {
        return "Instructor{id=" + id + ", name=" + name + "}";
    }
}
