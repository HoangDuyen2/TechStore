package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;
import hcmute.edu.vn.techstore.dto.response.CartDetailWrapper;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;

import java.util.List;

public interface ICartDetailService {
    CartDetailWrapper getAllCartDetail(String email);
    void addCartDetail(Long productId, Long cartId);
    void deleteCartDetail(Long productId, Long cartId);
    void decreaseCartDetail(Long productId, Long cartId);
}
