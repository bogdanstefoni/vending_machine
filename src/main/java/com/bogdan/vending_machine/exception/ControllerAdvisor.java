package com.bogdan.vending_machine.exception;

import com.bogdan.vending_machine.ErrorsEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<String> handleException(
            HttpServletRequest request, Throwable ex) {
        ErrorsEnum error;
        if (ex instanceof CustomException) {
            CustomException customException = (CustomException) ex;
            error = customException.getErrorsEnum();
        } else {
            error = ErrorsEnum.GENERAL_ERROR;
        }
        logger.error(error.getErrorDescription(), ex);

        return RestResponse.createErrorResponse(error);
    }
}
