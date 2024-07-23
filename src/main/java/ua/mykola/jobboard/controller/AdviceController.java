package ua.mykola.jobboard.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.mykola.jobboard.exception.NotFoundException;
import ua.mykola.jobboard.exception.ParsingException;
import ua.mykola.jobboard.exception.ValidationException;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler
    public ResponseEntity<String> parsingException(ParsingException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> validationException(ValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> notFoundException(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
