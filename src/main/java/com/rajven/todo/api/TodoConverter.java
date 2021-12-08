package com.rajven.todo.api;

import java.net.URI;

import lombok.extern.log4j.Log4j2;
import com.rajven.todo.db.TodoItem;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
public final class TodoConverter {
    private TodoConverter() {
    }

    /**
     * Converts the DB Model {@link TodoItem} to the API model {@link TodoResponse}.
     *
     * @param todoItem the element to convert
     * @return         an API model representation of the DB model
     */
    public static TodoResponse convert(TodoItem todoItem, URI requestUrl) {
        var itemUri = determineItemUrl(requestUrl, todoItem.getId());

        return new TodoResponse(todoItem.getId(), todoItem.getTitle(), todoItem.isCompleted(), todoItem.getOrder(), itemUri);
    }

    /**
     * If the request was for a specific item the request URL already contains the item id, otherwise we have to append item id.
     *
     * @param requestUrl the uri that the original request was sent to
     * @param itemId     the id of the item to return
     * @return           the full uri to the item
     */
    private static URI determineItemUrl(URI requestUrl, long itemId) {
        if (requestUrl.toString().endsWith(String.valueOf(itemId))) {
            return requestUrl;
        }

        return UriComponentsBuilder.fromUri(requestUrl).path(String.format("/%d", itemId)).build().toUri().normalize();
    }
}
