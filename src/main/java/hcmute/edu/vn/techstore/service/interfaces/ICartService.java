package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.entity.CartEntity;

import java.util.Optional;

public interface ICartService {
    CartResponse getCart(String email);
    void addCart(Long productId, String email);
    CartResponse getAllCartDetailInactive(String email);
    void deleteAllCartDetails(String email);
    CartEntity getCartEntity(String email);
}
