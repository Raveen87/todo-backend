package com.rajven.todo.api;

import static org.springframework.http.HttpStatus.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.rajven.todo.config.Constants;
import com.rajven.todo.service.TodoService;

@RestController
@RequestMapping(Constants.API_PATH)
@CrossOrigin("*")
@Log4j2
@RequiredArgsConstructor
public class TodoController {

    private static final String NOT_FOUND_ERROR_MSG = "No todo with that id found";

    private final TodoService todoService;

    @GetMapping
    public Flux<TodoResponse> list(ServerHttpRequest request) {
        return todoService.getAll()
            .doOnSubscribe(subscription -> log.info("Received request for all todo items"))
            .map(todoItem -> TodoConverter.convert(todoItem, request.getURI()));
    }

    @GetMapping("/{id}")
    public Mono<TodoResponse> get(@PathVariable long id, ServerHttpRequest request) {
        return todoService.find(id)
            .doOnSubscribe(subscription -> log.info("Received request for todo item with id {}", id))
            .map(todoItem -> TodoConverter.convert(todoItem, request.getURI()))
            .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, NOT_FOUND_ERROR_MSG)));
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Mono<TodoResponse> create(@RequestBody TodoCreateCommand todoCreateCmd, ServerHttpRequest request) {
        return todoService.create(todoCreateCmd)
            .doOnSubscribe(subscription -> log.info("Received request to create todo item"))
            .map(todoItem -> TodoConverter.convert(todoItem, request.getURI()));
    }

    @PatchMapping("/{id}")
    public Mono<TodoResponse> update(@PathVariable long id, @RequestBody TodoUpdateCommand todoUpdateCmd, ServerHttpRequest request) {
        return todoService.update(id, todoUpdateCmd)
            .doOnSubscribe(subscription -> log.info("Received request to update todo item with id {}", id))
            .map(todoItem -> TodoConverter.convert(todoItem, request.getURI()))
            .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, NOT_FOUND_ERROR_MSG)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public Mono<Boolean> delete(@PathVariable long id) {
        return todoService.delete(id)
            .doOnSubscribe(subscription -> log.info("Received request to delete todo item with id {}", id))
            .filter(result -> result)
            .switchIfEmpty(Mono.error(new ResponseStatusException(NOT_FOUND, NOT_FOUND_ERROR_MSG)));
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    public Mono<Void> deleteAll() {
        return todoService.deleteAll()
            .doOnSubscribe(subscription -> log.info("Received request to delete all todo items"));
    }
}
