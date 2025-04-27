package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.response.CartResponse;

import java.util.Optional;

public interface ICartService {
    CartResponse getCart(String email);
    void addCart(Long productId, String email);
}
