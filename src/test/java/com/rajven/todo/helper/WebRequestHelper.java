package com.rajven.todo.helper;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static com.rajven.todo.config.Constants.API_PATH;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import com.rajven.todo.api.TodoCreateCommand;

@Component
@RequiredArgsConstructor
public class WebRequestHelper {
    private final WebTestClient webClient;

    public WebTestClient.ResponseSpec postTodoItem(TodoCreateCommand createCommand) {
        return webClient
            .post()
            .uri(API_PATH)
            .body(Mono.just(createCommand), TodoCreateCommand.class)
            .exchange();
    }

    public WebTestClient.ResponseSpec getAllTodoItems() {
        return webClient
            .get()
            .uri(API_PATH)
            .exchange();
    }

    public WebTestClient.ResponseSpec getTodoItem(long id) {
        return webClient
            .get()
            .uri(String.format("%s/%d", API_PATH, id))
            .exchange();
    }

    public WebTestClient.ResponseSpec updateTodoItem(long id, String title, Boolean completed, Long order) {
        return webClient
            .patch()
            .uri(String.format("%s/%d", API_PATH, id))
            .contentType(APPLICATION_JSON)
            .bodyValue("""
                { "title": %s, "completed": %s, "order": %s }
                """.stripIndent().formatted(getJsonString(title), completed, order))
            .exchange();
    }

    public WebTestClient.ResponseSpec deleteTodoItem(long id) {
        return webClient
            .delete()
            .uri(String.format("%s/%d", API_PATH, id))
            .exchange();
    }

    public WebTestClient.ResponseSpec deleteAll() {
        return webClient
            .delete()
            .uri(API_PATH)
            .exchange();
    }

    private String getJsonString(String value) {
        return value == null
            ? "null"
            : String.format("\"%s\"", value);
    }
}
