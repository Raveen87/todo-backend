package com.rajven.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.rajven.todo.api.TodoCreateCommand;
import com.rajven.todo.api.TodoUpdateCommand;
import com.rajven.todo.db.TodoItem;
import com.rajven.todo.db.TodoRepository;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository repository;

    /**
     * Gets all elements.
     *
     * @return a {@link Flux} that emits all {@link TodoItem}s
     */
    public Flux<TodoItem> getAll() {
        return repository.getAll();
    }

    /**
     * Finds a {@link TodoItem} by id.
     *
     * @param id the id of the element to find.
     * @return   a {@link Mono} that emits the {@link TodoItem} with specified id,
     *           or {@link Mono}.empty() if no element is found
     */
    public Mono<TodoItem> find(long id) {
        return repository.findById(id);
    }

    /**
     * Creates a new {@link TodoItem} based on the given command.
     *
     * @param createCmd the {@link TodoCreateCommand} that contains the properties to create
     * @return          a {@link Mono} that emits the created element
     */
    public Mono<TodoItem> create(TodoCreateCommand createCmd) {
        return repository.create(createCmd);
    }

    /**
     * Updates the element with the specified {@code id}, based on the given update command.
     *
     * @param id        the id of the {@link TodoItem} to update
     * @param updateCmd the {@link TodoUpdateCommand} containing the new values
     * @return          a {@link Mono} that emits the updated {@link TodoItem}, or {@link Mono}.empty() if no
     *                  element with that id was found
     */
    public Mono<TodoItem> update(long id, TodoUpdateCommand updateCmd) {
        if (!updateCmd.hasAnyUpdate()) {
            return repository.findById(id);
        }

        return repository.update(id, updateCmd);
    }

    /**
     * Deletes the element with the given {@code id}.
     *
     * @param id the id of the {@link TodoItem} to delete
     * @return   a {@link Mono} that returns true if the element was deleted, otherwise false
     */
    public Mono<Boolean> delete(long id) {
        return repository.delete(id);
    }

    /**
     * Deletes all elements.
     *
     * @return a {@link Mono}.empty()
     */
    public Mono<Void> deleteAll() {
        return repository.deleteAll();
    }
}
