package org.devsu.common;

import org.devsu.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setMessage("Body validation error");
        errorResponse.setTimestamp(LocalDateTime.now());

        Map<String, Object> errors = new LinkedHashMap<>();

        for(FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        errorResponse.setDetails(errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setMessage("Invalid data format");
        errorResponse.setTimestamp(LocalDateTime.now());

        Map<String, Object> errors = new LinkedHashMap<>();
        errors.put("error", "The provided data could not be understood. Please check your input and try again.");

        errorResponse.setDetails(errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
