package hcmute.edu.vn.techstore.configuration;

import hcmute.edu.vn.techstore.entity.RoleEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
@Component
public class CustomSuccessHandle implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var authorities = authentication.getAuthorities();
        var roleName = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        if (roleName.contains("ROLE_CUSTOMER")) {
                response.sendRedirect("/home");
        }
        else if (roleName.contains("ROLE_ADMIN")) {
                response.sendRedirect("/admin/dashboard");
        }
        else if (roleName.contains("ROLE_STAFF")) {
                response.sendRedirect("/staff/home");
        }
        else {
            response.sendRedirect("/error");
        }
    }
}
