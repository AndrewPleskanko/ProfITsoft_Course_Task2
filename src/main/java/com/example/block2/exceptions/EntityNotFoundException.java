package com.example.block2.exceptions;

import static java.lang.String.format;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Entity not found")
public class EntityNotFoundException extends BaseException {

    public EntityNotFoundException(String entity, Long id) {
        super(format("Failed to found '%s' entity  with id: %s", entity, id));
    }
}
