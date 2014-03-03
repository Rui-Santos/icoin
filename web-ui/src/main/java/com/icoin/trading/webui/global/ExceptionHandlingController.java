package com.icoin.trading.webui.global;

import com.google.common.base.Throwables;
import com.homhon.core.exception.IZookeyException;
import com.homhon.util.Strings;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.NoHandlerForCommandException;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.axonframework.common.AxonException;
import org.axonframework.eventhandling.annotation.EventHandlerInvocationException;
import org.joda.money.IllegalCurrencyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

/**
 * Created with IntelliJ IDEA.
 * User: jihual
 * Date: 2/21/14
 * Time: 7:20 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class ExceptionHandlingController {
    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlingController.class);

    @ExceptionHandler(value = {
            IZookeyException.class
    })
    public String handleException(IZookeyException ex, WebRequest request) {
        logger.error("Server internal executed error:", ex.getMessage(), ex.getCause());

        request.setAttribute("javax.servlet.error.exception", ex, WebRequest.SCOPE_REQUEST);
        request.setAttribute("javax.servlet.error.status_code", 500, WebRequest.SCOPE_REQUEST);
        return "error";
    }

    @ExceptionHandler(value = {
            IllegalCurrencyException.class
    })
    public String handleException(IllegalCurrencyException ex, WebRequest request) {
        logger.error("currency not found:", ex.getMessage(), ex.getCause());

        request.setAttribute("javax.servlet.error.exception", ex, WebRequest.SCOPE_REQUEST);
        request.setAttribute("javax.servlet.error.status_code", 404, WebRequest.SCOPE_REQUEST);
        return "error";
    }


    @ExceptionHandler(value = {
            JSR303ViolationException.class
    })
    public String handleException(JSR303ViolationException ex, WebRequest request) {
        logger.error("JSR303 constraints were violated constraints:", ex.getMessage(), ex.getCause());

        request.setAttribute("javax.servlet.error.exception", ex, WebRequest.SCOPE_REQUEST);
        request.setAttribute("javax.servlet.error.status_code", 500, WebRequest.SCOPE_REQUEST);
        return "error";
    }

    @ExceptionHandler(value = {
            AxonException.class
    })
    public String handleException(AxonException ex, WebRequest request) {
        if (CommandExecutionException.class.isAssignableFrom(ex.getClass())) {
            logger.error("Command executed error: {}", ex.getMessage(), ex.getCause());
        }
        if (NoHandlerForCommandException.class.isAssignableFrom(ex.getClass())) {
            logger.error("Command handler not found: {}", ex.getMessage(), ex.getCause());
        } else if (EventHandlerInvocationException.class.isAssignableFrom(ex.getClass())) {
            logger.error("event executed error: {}", ex.getMessage(), ex.getCause());
        } else {
            logger.error("axon error: {}", ex.getMessage(), ex.getCause());
        }

        request.setAttribute("javax.servlet.error.exception", new AxonInternalException("Internal error"), WebRequest.SCOPE_REQUEST);
        request.setAttribute("javax.servlet.error.status_code", 500, WebRequest.SCOPE_REQUEST);
        return "error";
    }

    @RequestMapping("error")
    public String error(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // retrieve some useful information from the request
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        // String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
        String exceptionMessage = getExceptionMessage(throwable, statusCode);


        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
        if (requestUri == null) {
            requestUri = "Unknown";
        }

        String title = MessageFormat.format("{0} returned for {1} with message:",
                statusCode, requestUri);

        String message = Strings.hasText(exceptionMessage) ? exceptionMessage : "Unknown message";

        logger.error("error occurred: status code {}, requestUri {}, msg {}, exception {}", statusCode, requestUri, exceptionMessage, throwable);

        redirectAttributes.addFlashAttribute("errorTitle", title);
        redirectAttributes.addFlashAttribute("errorMessage", message);
        return "redirect:/displayError";
    }

    @RequestMapping("displayError")
    public String displayError() {
        return "errorPage";
    }

    private String getExceptionMessage(Throwable throwable, Integer statusCode) {
        if (throwable != null) {
            return Throwables.getRootCause(throwable).getMessage();
        }
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return httpStatus.getReasonPhrase();
    }
}