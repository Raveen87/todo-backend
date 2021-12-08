package com.rajven.todo.api;

import java.net.URI;

/**
 * API response object, i.e. a DTO.
 */
public record TodoResponse(long id, String title, boolean completed, Long order, URI url) {
}
