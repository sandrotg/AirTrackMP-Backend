package com.airtrackmp.iot.airtrackmp.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ApiErrorWriter apiErrorWriter;

    public RestAccessDeniedHandler(ApiErrorWriter apiErrorWriter) {
        this.apiErrorWriter = apiErrorWriter;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        apiErrorWriter.write(
                response,
                HttpStatus.FORBIDDEN,
                "No tiene permisos para realizar esta acción",
                request.getRequestURI()
        );
    }
}
