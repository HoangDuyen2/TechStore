package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.UserRequest;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.service.interfaces.IUserRegistrationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RegistrationContext {
    private final Map<String, IUserRegistrationStrategy> strategies;
    private IUserRegistrationStrategy strategy;

    public void setStrategy(String roleName) {
        this.strategy = strategies.get(roleName);
        if (this.strategy == null) {
            throw new IllegalArgumentException("No strategy for role: " + roleName);
        }
    }

    public UserEntity executeRegistration(UserRequest req) throws IOException {
        return strategy.createUser(req);
    }
}
