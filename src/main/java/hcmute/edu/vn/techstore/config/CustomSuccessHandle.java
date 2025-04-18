package hcmute.edu.vn.techstore.config;

import hcmute.edu.vn.techstore.entity.RoleEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
@Component
@RequiredArgsConstructor
public class CustomSuccessHandle implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        var authorities = authentication.getAuthorities();
        var roleName = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
        User user = (User) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findByAccount_Email(user.getUsername()).orElse(null);

        if (userEntity == null || !userEntity.isActived()) {
            request.getSession().invalidate();
            request.getSession(true).setAttribute("LOGIN_ERROR", "Account is locked");
            response.sendRedirect("/login?error");
            return;
        }

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
