package hcmute.edu.vn.techstore.service.mail;

import hcmute.edu.vn.techstore.entity.OrderEntity;

public interface OrderObserver {
    void update(OrderEntity order);
}