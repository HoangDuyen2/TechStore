package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.entity.CartEntity;

public interface ICartDetailService {
    void addCartDetail(Long productId, CartEntity cartEntity);
    void deleteCartDetail(Long productId, CartEntity cartEntity);
    void decreaseCartDetail(Long productId, CartEntity cartEntity);
    void deleteAllCartDetail(CartEntity cartEntity);}
