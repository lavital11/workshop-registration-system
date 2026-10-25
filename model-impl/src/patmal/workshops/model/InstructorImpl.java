package patmal.workshops.model;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of {@link Instructor}.
 *
 * <p>The list of workshops is owned by this object but kept up to date by the
 * logic layer ({@code WorkshopSystemImpl}) through {@link #addWorkshop} /
 * {@link #removeWorkshop}, which are intentionally not part of the
 * {@link Instructor} API - callers of the public API can only read the
 * relation, never mutate it directly.</p>
 */
public class InstructorImpl implements Instructor {

    private final String id;
    private final String name;
    private final String email;

    /** Workshops this instructor teaches, in the order they were associated. */
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
        // Defensive copy: callers must not be able to corrupt our internal state.
        return new ArrayList<>(workshops);
    }

    /**
     * Internal mutator used by the logic layer when a workshop is associated
     * with this instructor. Not part of the public {@link Instructor} API.
     */
    public void addWorkshop(Workshop workshop) {
        if (!workshops.contains(workshop)) {
            workshops.add(workshop);
        }
    }

    /**
     * Internal mutator used by the logic layer when a workshop is
     * re-associated with a different instructor. Not part of the public
     * {@link Instructor} API.
     */
    public void removeWorkshop(Workshop workshop) {
        workshops.remove(workshop);
    }

    @Override
    public String toString() {
        return "Instructor{id=" + id + ", name=" + name + "}";
    }
}
