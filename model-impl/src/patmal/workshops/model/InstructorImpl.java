package patmal.workshops.model;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class InstructorImpl implements Instructor {

    private final String id;
    private final String name;
    private final String email;

    // workshops this instructor teaches
    private final Set<Workshop> workshops = new LinkedHashSet<>();

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

    // Internal: intended to be called only by WorkshopSystemImpl; not part of the public
    // model contract. Called when a workshop gets associated with this instructor.
    public void addWorkshop(Workshop workshop) {
        workshops.add(workshop);
    }

    // Internal: intended to be called only by WorkshopSystemImpl; not part of the public
    // model contract. Called if a workshop is re-assigned to a different instructor.
    public void removeWorkshop(Workshop workshop) {
        workshops.remove(workshop);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof InstructorImpl other && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Instructor{id=" + id + ", name=" + name + "}";
    }
}
