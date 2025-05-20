package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;
import hcmute.edu.vn.techstore.dto.response.CartDetailWrapper;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import hcmute.edu.vn.techstore.entity.CartEntity;

import java.util.List;

public interface ICartDetailService {
    CartDetailWrapper getAllCartDetail(CartEntity cartEntity);
    void addCartDetail(Long productId, CartEntity cartEntity);
    void deleteCartDetail(Long productId, CartEntity cartEntity);
    void decreaseCartDetail(Long productId, CartEntity cartEntity);
    void deleteAllCartDetail(CartEntity cartEntity);}
