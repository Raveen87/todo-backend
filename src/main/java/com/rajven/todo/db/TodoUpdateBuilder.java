package com.rajven.todo.db;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.relational.core.query.Update;
import com.rajven.todo.api.TodoUpdateCommand;

/**
 * Helper class to determine which properties should be updated.
 */
public class TodoUpdateBuilder {
    private TodoUpdateBuilder() {
    }

    private static final String TITLE_COLUMN_NAME = "title";
    private static final String COMPLETED_COLUMN_NAME = "completed";
    private static final String ORDER_COLUMN_NAME = "sort_order";

    /**
     * Determines which properties of the {@link TodoUpdateCommand} contains updates that should be persisted.
     * The update command is expected to contain at least one update, otherwise null is returned.
     *
     * @param cmd the update command with updates to apply
     * @return    an {@link Update} object that contains the updates to be applied, or null if no updates
     */
    public static Update withUpdates(TodoUpdateCommand cmd) {
        Update update = null;

        if (cmd.getTitle().isPresent()) {
            update = createOrAppend(update, TITLE_COLUMN_NAME, cmd.getTitle());
        }

        if (cmd.getCompleted().isPresent()) {
            update = createOrAppend(update, COMPLETED_COLUMN_NAME, cmd.getCompleted());
        }

        if (cmd.getOrder().isPresent()) {
            update = createOrAppend(update, ORDER_COLUMN_NAME, cmd.getOrder());
        }

        return update;
    }

    private static <T> Update createOrAppend(Update update, String columnName, JsonNullable<T> updateProperty) {
        return update == null
            ? Update.update(columnName, updateProperty.get())
            : update.set(columnName, updateProperty.get());
    }
}
