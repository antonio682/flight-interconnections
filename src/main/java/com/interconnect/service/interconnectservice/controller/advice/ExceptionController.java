package com.interconnect.service.interconnectservice.controller.advice;

import com.interconnect.service.interconnectservice.exceptions.InterconnectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {InterconnectException.class})
    protected ResponseEntity<Object> hadleWithInterconnectionErzror(RuntimeException ex, WebRequest webRequest) {
        log.warn("Interconnection error: {}", ex);
        return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
    }
}
