package hcmute.edu.vn.techstore.service.mail;

public interface OrderSubject {
    void attach(OrderObserver observer);
    void detach(OrderObserver observer);
    void notifyObservers();
}