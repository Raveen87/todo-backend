package com.rajven.todo.db;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * The DB model used internally in the application.
 */
@Table("todo_item")
@Data
@ToString
@Builder
@AllArgsConstructor
public class TodoItem {

    @Id
    @Column("id")
    private long id;

    @Column("title")
    private String title;

    @Column("completed")
    private boolean completed;

    @Column("sort_order")
    private Long order;
}
