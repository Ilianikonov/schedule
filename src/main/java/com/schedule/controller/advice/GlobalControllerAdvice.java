package com.schedule.controller.advice;

import com.schedule.controller.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception){
        String message = "во время обработки произошла ошибка";
        return buildResponseEntity(exception, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
//
//    @ExceptionHandler(CodeIsOccupiedException.class)
//    public ResponseEntity<ErrorResponse> handleException(CodeIsOccupiedException codeIsOccupiedException){
//        return buildResponseEntity(codeIsOccupiedException, HttpStatus.BAD_REQUEST, codeIsOccupiedException.getMessage());
//    }
//
//    @ExceptionHandler(NotFoundTramRouteException.class)
//    public ResponseEntity<ErrorResponse> handleException(NotFoundTramRouteException notFoundTramRouteException){
//        return buildResponseEntity(notFoundTramRouteException, HttpStatus.NOT_FOUND, notFoundTramRouteException.getMessage());
//    }
//
    private ResponseEntity<ErrorResponse> buildResponseEntity(Exception exception, HttpStatus httpStatus, String message){
        log.error(exception.getMessage(), exception);
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMassage(message);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
