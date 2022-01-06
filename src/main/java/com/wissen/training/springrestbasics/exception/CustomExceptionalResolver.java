package com.wissen.training.springrestbasics.exception;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomExceptionalResolver extends AbstractHandlerExceptionResolver {
    //ResultSetExtractor
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return null;
    }
}
