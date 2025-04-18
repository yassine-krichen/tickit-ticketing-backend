package com.sofrecom.tickit.exception;

import com.sofrecom.tickit.dto.ErrorDetails;
import com.sofrecom.tickit.dto.ErrorResponse;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Catches specific exceptions and formats them into a standard ErrorResponse.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles validation errors for request parameters annotated with validation constraints.
     *
     * @param ex The ConstraintViolationException.
     * @param request The incoming request.
     * @return ResponseEntity containing the formatted error response.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        Map<String, List<String>> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String parameterName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            String message = violation.getMessage();
            errors.computeIfAbsent(parameterName, k -> new ArrayList<>()).add(message);
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for request parameters.",
                request.getRequestURI()
        );
        errorResponse.setValidationErrors(errors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles errors when request parameters have the wrong type.
     *
     * @param ex The MethodArgumentTypeMismatchException.
     * @param request The incoming request.
     * @return ResponseEntity containing the formatted error response.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String parameterName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String providedValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String message = String.format("Parameter '%s' should be of type '%s' but received value '%s'.",
                parameterName, requiredType, providedValue);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Override the handleMissingServletRequestParameter method from ResponseEntityExceptionHandler
     * to customize the response for missing request parameters.
     *
     * @param ex The MissingServletRequestParameterException.
     * @param headers The HTTP headers.
     * @param status The HTTP status.
     * @param request The Web request.
     * @return ResponseEntity containing the formatted error response.
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, 
            HttpStatus status, WebRequest request) {
        
        String parameterName = ex.getParameterName();
        String message = String.format("Required parameter '%s' is missing.", parameterName);
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                ((ServletWebRequest)request).getRequest().getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, headers, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom InvalidSortParameterException.
     *
     * @param ex The InvalidSortParameterException.
     * @param request The incoming request.
     * @return ResponseEntity containing the formatted error response.
     */
    @ExceptionHandler(InvalidSortParameterException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSortParameterException(
            InvalidSortParameterException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic exceptions as a fallback.
     * Note: This won't catch exceptions already handled by ResponseEntityExceptionHandler.
     *
     * @param ex The Exception.
     * @param request The incoming request.
     * @return ResponseEntity containing the formatted error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles validation errors for method arguments.
     *
     * @param ex The MethodArgumentNotValidException.
     * @param headers The HTTP headers.
     * @param status The HTTP status.
     * @param request The WebRequest.
     * @return ResponseEntity containing the formatted error response.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles specific ResourceNotFoundException.
     *
     * @param ex The ResourceNotFoundException.
     * @param webRequest The WebRequest.
     * @return ResponseEntity containing the formatted error response.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(ResourceNotFoundException ex,
            WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), webRequest.getDescription(false));

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles specific EntityCreationException.
     *
     * @param ex The EntityCreationException.
     * @param webRequest The WebRequest.
     * @return ResponseEntity containing the formatted error response.
     */
    @ExceptionHandler(EntityCreationException.class)
    public ResponseEntity<ErrorDetails> handleEntityCreationException(
            EntityCreationException ex, WebRequest webRequest) {
        ErrorDetails errorDetails = new ErrorDetails(
            new Date(), ex.getMessage(), webRequest.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
}
