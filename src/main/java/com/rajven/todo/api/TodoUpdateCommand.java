package com.rajven.todo.api;

import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

/**
 * Represents a command that contains update to a {@link com.rajven.todo.db.TodoItem}.
 */
@Data
public class TodoUpdateCommand {
    private JsonNullable<String> title = JsonNullable.undefined();
    private JsonNullable<Boolean> completed = JsonNullable.undefined();
    private JsonNullable<Long> order = JsonNullable.undefined();

    /**
     * Checks if this command contains any updates, i.e. if any of the wrapped property is non-null.
     *
     * @return true if this update command contains any updates, otherwise false
     */
    public boolean hasAnyUpdate() {
        return title.isPresent() || completed.isPresent() || order.isPresent();
    }
}
