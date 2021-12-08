package com.rajven.todo.helper;

import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.Parameter;
import org.springframework.stereotype.Component;
import com.rajven.todo.db.TodoItem;

@Component
@RequiredArgsConstructor
public class DbTestHelper {
    private final ConnectionFactory connectionFactory;

    public void createTodoItem(TodoItem todoItem) {
        createTodoItem(todoItem.getId(), todoItem.getTitle(), todoItem.isCompleted(), todoItem.getOrder());
    }

    public void createTodoItem(long id, String title, boolean completed, Long order) {
        var sql = """
            INSERT INTO todo_item (id, title, completed, sort_order)
                VALUES (:id, :title, :completed, :sortOrder)
         """;

        DatabaseClient.create(connectionFactory)
            .sql(sql)
            .bind("id", id)
            .bind("title", Parameter.fromOrEmpty(title, String.class))
            .bind("completed", completed)
            .bind("sortOrder", Parameter.fromOrEmpty(order, Long.class))
            .fetch()
            .rowsUpdated()
            .block();
    }

    public void clearTodoItems() {
        var sql = """
            TRUNCATE TABLE todo_item
         """;

        DatabaseClient.create(connectionFactory)
            .sql(sql)
            .fetch()
            .rowsUpdated()
            .block();
    }
}
