package hcmute.edu.vn.techstore.service.mail;

import hcmute.edu.vn.techstore.Enum.EOrderStatus;
import hcmute.edu.vn.techstore.entity.OrderEntity;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class OrderConcreteSubject implements OrderSubject {

    private final List<OrderObserver> observers = new ArrayList<>();

    @Getter
    private OrderEntity order;

    public OrderConcreteSubject(OrderEntity order) {
        this.order = order;
    }

    public void setOrderStatus(EOrderStatus status) {
        this.order.setOrderStatus(status);
        notifyObservers();
    }

    @Override
    public void attach(OrderObserver observer) {
        observers.add(observer);
    }

    @Override
    public void detach(OrderObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (OrderObserver observer : observers) {
            observer.update(order);
        }
    }
}
