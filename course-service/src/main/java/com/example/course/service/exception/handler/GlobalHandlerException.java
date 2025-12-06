package com.example.course.service.exception.handler;

import com.example.course.service.dto.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandlerException {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResponseObject.builder()
                        .message("System Error: " + ex.getMessage())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .data(null)
                        .build()
        );
    }


}
