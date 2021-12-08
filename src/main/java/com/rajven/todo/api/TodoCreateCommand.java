package com.rajven.todo.api;

import com.rajven.todo.db.TodoItem;

/**
 * Used to create a new {@link TodoItem}.
 */
public record TodoCreateCommand(String title, boolean completed, Long order) {

    /**
     * Converts this command into a {@link TodoItem}, with id not set.
     *
     * @return a {@link TodoItem} representation of this command
     */
    public TodoItem toTodoItem() {
        return new TodoItem(0, title, completed, order);
    }
}
