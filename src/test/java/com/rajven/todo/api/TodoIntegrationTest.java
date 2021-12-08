package com.rajven.todo.api;

import static org.springframework.http.HttpStatus.*;

import javax.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.relational.core.conversion.BasicRelationalConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import com.rajven.todo.converter.ClobConverter;
import com.rajven.todo.helper.DbTestHelper;
import com.rajven.todo.helper.WebRequestHelper;
import com.rajven.todo.db.TodoItem;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureWebTestClient(timeout = "PT30S")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@RequiredArgsConstructor
public class TodoIntegrationTest {
    private final WebRequestHelper webRequestHelper;
    private final DbTestHelper dbTestHelper;
    private final BasicRelationalConverter basicRelationalConverter;

    @PostConstruct
    void addClobConverter() {
        ((ConfigurableConversionService) basicRelationalConverter.getConversionService())
            .addConverter(new ClobConverter());
    }

    @AfterEach
    void clearDatabase() {
        dbTestHelper.clearTodoItems();
    }

    @Test
    void givenNoItemsAdded_whenGettingAllItems_thenEmptyArrayIsReturned() {
        webRequestHelper.getAllTodoItems()
            .expectStatus().is2xxSuccessful()
            .expectBody()
            .consumeWith(System.out::println)
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void givenOneItemAdded_whenGettingAllItems_ThenArrayWithOnlyThatItemIsReturned() {
        var expectedTodo = new TodoItem(1, "test", false, 1337L);
        dbTestHelper.createTodoItem(expectedTodo);

        webRequestHelper.getAllTodoItems()
            .expectStatus().is2xxSuccessful()
            .expectBody()
            .consumeWith(System.out::println)
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].id").isEqualTo(expectedTodo.getId())
            .jsonPath("$[0].title").isEqualTo(expectedTodo.getTitle())
            .jsonPath("$[0].completed").isEqualTo(expectedTodo.isCompleted())
            .jsonPath("$[0].order").isEqualTo(expectedTodo.getOrder());
    }

    @Test
    void givenTwoItemAdded_whenGettingAllItems_ThenArrayWithTwoItemsIsReturned() {
        var expectedTodo1 = new TodoItem(5, "a todo item", false, null);
        var expectedTodo2 = new TodoItem(8, "another todo item", true, 55L);
        dbTestHelper.createTodoItem(expectedTodo1);
        dbTestHelper.createTodoItem(expectedTodo2);

        webRequestHelper.getAllTodoItems()
            .expectStatus().is2xxSuccessful()
            .expectBody()
            .consumeWith(System.out::println)
            .jsonPath("$.length()").isEqualTo(2)
            .jsonPath("$[0].id").isEqualTo(expectedTodo1.getId())
            .jsonPath("$[0].title").isEqualTo(expectedTodo1.getTitle())
            .jsonPath("$[0].completed").isEqualTo(expectedTodo1.isCompleted())
            .jsonPath("$[0].order").isEqualTo(expectedTodo1.getOrder())
            .jsonPath("$[1].id").isEqualTo(expectedTodo2.getId())
            .jsonPath("$[1].title").isEqualTo(expectedTodo2.getTitle())
            .jsonPath("$[1].completed").isEqualTo(expectedTodo2.isCompleted())
            .jsonPath("$[1].order").isEqualTo(expectedTodo2.getOrder());
    }

    @Test
    void givenOneItemAdded_whenGettingItemById_ThenThatItemIsReturned() {
        var expectedTodo = new TodoItem(1, "kiss hands & shake babies", true, null);
        dbTestHelper.createTodoItem(expectedTodo);

        webRequestHelper.getTodoItem(expectedTodo.getId())
            .expectStatus().is2xxSuccessful()
            .expectBody()
            .consumeWith(System.out::println)
            .jsonPath("$.id").isEqualTo(expectedTodo.getId())
            .jsonPath("$.title").isEqualTo(expectedTodo.getTitle())
            .jsonPath("$.completed").isEqualTo(expectedTodo.isCompleted())
            .jsonPath("$.order").isEqualTo(expectedTodo.getOrder());
    }

    @Test
    void givenDatabaseIsEmpty_whenGettingItemById_Then404Returned() {
        webRequestHelper.getTodoItem(1)
            .expectStatus().isEqualTo(NOT_FOUND)
            .expectBody()
            .consumeWith(System.out::println);
    }

    @Test
    void givenAValidModel_whenPostingAnItem_thenTheCreatedModelIsReturned() {
        var expectedResult = new TodoCreateCommand("Take over the world", false, 5L);

        webRequestHelper.postTodoItem(expectedResult)
            .expectStatus().isEqualTo(CREATED)
            .expectBody()
            .consumeWith(System.out::println)
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.title").isEqualTo(expectedResult.title())
            .jsonPath("$.completed").isEqualTo(expectedResult.completed())
            .jsonPath("$.order").isEqualTo(expectedResult.order());
    }

    @Test
    void givenAnItemAddedWithPost_whenGettingThatItem_thenThePostedItemIsReturned() {
        var expectedResult = new TodoCreateCommand("Sing loudly in the shower", false, 1L);

        webRequestHelper.postTodoItem(expectedResult)
            .expectStatus().isEqualTo(CREATED)
            .expectBody()
            .consumeWith(System.out::println)
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.title").isEqualTo(expectedResult.title())
            .jsonPath("$.completed").isEqualTo(expectedResult.completed())
            .jsonPath("$.order").isEqualTo(expectedResult.order());
    }

    @Test
    void givenAnItemExists_whenUpdatingThatItem_thenThatItemIsUpdated() {
        var expectedId = 8;
        var originalItem = new TodoItem(expectedId, "binge watch Friendz", false, 1337L);
        dbTestHelper.createTodoItem(originalItem);

        var expectedTitle = "binge watch Friends";
        var expectedCompleted = true;
        Long expectedOrder = null;

        webRequestHelper.updateTodoItem(expectedId, expectedTitle, expectedCompleted, expectedOrder)
            .expectBody()
            .consumeWith(System.out::println);

        webRequestHelper.getTodoItem(expectedId)
            .expectStatus().is2xxSuccessful()
            .expectBody()
            .consumeWith(System.out::println)
            .jsonPath("$.id").isEqualTo(expectedId)
            .jsonPath("$.title").isEqualTo(expectedTitle)
            .jsonPath("$.completed").isEqualTo(expectedCompleted)
            .jsonPath("$.order").isEmpty();
    }

    @Test
    void givenAnItemExists_whenDeletingThatItem_thenThatItemIsDeleted() {
        var existingItem = new TodoItem(32, "Learn a new skill", false, 32L);
        dbTestHelper.createTodoItem(existingItem);

        webRequestHelper.deleteTodoItem(existingItem.getId())
            .expectStatus().isEqualTo(NO_CONTENT);

        webRequestHelper.getTodoItem(existingItem.getId())
            .expectStatus().isEqualTo(NOT_FOUND);
    }

    @Test
    void givenNoItemsExist_whenDeletingAnItem_then404IsReturned() {
        webRequestHelper.deleteTodoItem(1)
            .expectStatus().isEqualTo(NOT_FOUND)
            .expectBody()
            .consumeWith(System.out::println);
    }

    @Test
    void givenMultipleItemsExists_whenDeletingAllItems_thenNoItemsAreReturned() {
        dbTestHelper.createTodoItem(new TodoItem(1, "Get shit done", false, null));
        dbTestHelper.createTodoItem(new TodoItem(27, "Make a todo item", true, 13L));
        dbTestHelper.createTodoItem(new TodoItem(518, "Brew a killer beer", true, 1L));

        webRequestHelper.deleteAll()
            .expectStatus().isEqualTo(NO_CONTENT);

        webRequestHelper.getAllTodoItems()
            .expectStatus().is2xxSuccessful()
            .expectBody()
            .consumeWith(System.out::println)
            .jsonPath("$").isArray()
            .jsonPath("$.length()").isEqualTo(0);
    }
}
