package com.rajven.todo.db;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.data.domain.Sort.by;
import static org.springframework.data.relational.core.query.Criteria.where;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.rajven.todo.api.TodoCreateCommand;
import com.rajven.todo.api.TodoUpdateCommand;

@Repository
@RequiredArgsConstructor
public class TodoRepository {
    private final R2dbcEntityTemplate template;

    /**
     * Gets all {@link TodoItem}s.
     *
     * @return a {@link Flux} that emits all existing items
     */
    public Flux<TodoItem> getAll() {
        return template.select(TodoItem.class)
            .matching(Query.empty()
                .sort(by(asc("id"))))
            .all();
    }

    /**
     * Finds the {@link TodoItem} with the given id.
     *
     * @param id the id of the item to find
     * @return   a {@link Mono} that emits the item with the given id, or {@link Mono}.empty()
     */
    public Mono<TodoItem> findById(long id) {
        return template.select(TodoItem.class)
            .matching(Query.query(where("id").is(id))
                .sort(by(asc("id"))))
            .one();
    }

    /**
     * Creates a new {@link TodoItem} based on the given create command.
     *
     * @param todoCreateCmd the {@link TodoCreateCommand} containing the properties of the item to create
     * @return              a {@link Mono} that emits the created item
     */
    public Mono<TodoItem> create(TodoCreateCommand todoCreateCmd) {
        return template.insert(TodoItem.class)
            .using(todoCreateCmd.toTodoItem());
    }

    /**
     * Updates the {@link TodoItem} with the specified id, based on the given update command.
     * Only properties that have been explicitly set in {@link TodoUpdateCommand} will be updated.
     *
     * @param id        the id of the item to update
     * @param updateCmd the command containing the updates to apply
     * @return          a {@link Mono } that emits the updated item, or {@link Mono}.empty() if no element was found
     */
    public Mono<TodoItem> update(long id, TodoUpdateCommand updateCmd) {
        return template.update(TodoItem.class)
            .matching(Query.query(where("id").is(id)))
            .apply(TodoUpdateBuilder.withUpdates(updateCmd))
            .flatMap(rowsAffected -> findById(id));
    }

    /**
     * Deletes the {@link TodoItem} with the specified id.
     *
     * @param id the id of the item to delete
     * @return   a {@link Mono} that emits true if the item was deleted, otherwise false
     */
    public Mono<Boolean> delete(long id) {
        return template.delete(TodoItem.class)
            .matching(Query.query(where("id").is(id)))
            .all()
            .map(deletedRows -> deletedRows > 0);
    }

    /**
     * Deletes all {@link TodoItem}s.
     *
     * @return a {@link Mono} that emits Void
     */
    public Mono<Void> deleteAll() {
        return template.delete(TodoItem.class)
            .all()
            .then();
    }
}
