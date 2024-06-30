package by.plamya.project.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import by.plamya.project.payload.response.InvalidLoginResponse;
import by.plamya.project.utils.constants.SecurityConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        InvalidLoginResponse loginResponse = new InvalidLoginResponse();
        // String jsonLoginResponse = new Gson().toJson(loginResponse);

        response.setContentType(SecurityConstants.CONTENT_TYPE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());// 401
       
        response.getWriter().println(authException.getLocalizedMessage());
    }

}
