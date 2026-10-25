package patmal.workshops.model;

import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Workshop;

/**
 * Concrete implementation of {@link Workshop}.
 *
 * <p>The id is assigned once, by the logic layer, as a sequential number
 * starting from 1, and never changes afterwards. The instructor starts as
 * {@code null} (no instructor yet) and is set through {@link #setInstructor},
 * an internal mutator that is not part of the public {@link Workshop} API.</p>
 */
public class WorkshopImpl implements Workshop {

    private final Integer id;
    private final String name;
    private final String description;

    /** null until the workshop is associated with an instructor. */
    private Instructor instructor;

    public WorkshopImpl(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Instructor getInstructor() {
        return instructor;
    }

    /**
     * Internal mutator used by the logic layer to keep the
     * Instructor &lt;-&gt; Workshop relation consistent. Not part of the
     * public {@link Workshop} API.
     */
    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    @Override
    public String toString() {
        return "Workshop{id=" + id + ", name=" + name + "}";
    }
}
