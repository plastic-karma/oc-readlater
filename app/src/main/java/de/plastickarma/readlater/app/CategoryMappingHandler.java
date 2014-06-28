package de.plastickarma.readlater.app;

/**
 * Handles CRUD-like Events of category mappings.
 */
public interface CategoryMappingHandler {

    /**
     * Event, that a category mapping shall be edited.
     */
    public abstract void handleEditMapping(CategoryMapping mapping);

    /**
     *  Event, that a new category mapping shall be created.
     */
    public abstract void handleCreateMapping(CategoryMapping mapping);




}
