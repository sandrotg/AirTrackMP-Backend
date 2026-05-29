package com.airtrackmp.iot.airtrackmp.exception;

import com.airtrackmp.iot.airtrackmp.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException exception,
            HttpServletRequest request
    ) {
        return buildResponse(exception.getStatus(), exception.getMessage(), request);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            Exception exception,
            HttpServletRequest request
    ) {
        String message = exception.getMessage() != null
                ? exception.getMessage()
                : "Solicitud inválida";
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiErrorResponse> handleDateTimeParse(
            DateTimeParseException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Formato de fecha inválido. Use: yyyy-MM-ddTHH:mm:ss (ej. 2026-05-27T23:10:00)",
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableBody(
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "JSON inválido o cuerpo de solicitud incorrecto",
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Datos de entrada inválidos");
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            HttpServletRequest request
    ) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "No tiene permisos para realizar esta acción",
                request
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccess(
            DataAccessException exception,
            HttpServletRequest request
    ) {
        log.error("Database error on {} {}", request.getMethod(), request.getRequestURI(), exception);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error al acceder a los datos",
                request
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntime(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = resolveStatus(exception);
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("Unhandled runtime error on {} {}", request.getMethod(), request.getRequestURI(), exception);
        }
        String message = exception.getMessage() != null
                ? exception.getMessage()
                : "Error en la operación";
        return buildResponse(status, message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), exception);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity
                .status(status)
                .body(ApiErrorResponse.of(status, message, request.getRequestURI()));
    }

    private HttpStatus resolveStatus(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String normalized = message.toLowerCase();

        if (normalized.contains("invalid password")
                || normalized.equals("user not found")) {
            return HttpStatus.UNAUTHORIZED;
        }
        if (normalized.contains("already exists")) {
            return HttpStatus.CONFLICT;
        }
        if (normalized.contains("not found")
                || normalized.contains("notfound")
                || normalized.contains("deleted")
                || normalized.contains("no measurements")) {
            return HttpStatus.NOT_FOUND;
        }
        if (normalized.contains("invalid")
                || normalized.contains("must be created via")) {
            return HttpStatus.BAD_REQUEST;
        }
        if (normalized.contains("unavailable")) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
