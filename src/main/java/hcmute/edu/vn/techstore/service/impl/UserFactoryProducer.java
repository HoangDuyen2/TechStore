package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.service.interfaces.IUserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFactoryProducer {

    @Qualifier("customerFactory")
    private final IUserFactory customerFactory;

    @Qualifier("staffFactory")
    private final IUserFactory staffFactory;

    public IUserFactory getFactory(String roleName) {
        if (roleName != null && roleName.contains("STAFF")) {
            return staffFactory;
        }
        return customerFactory;
    }
}
