package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;

import java.util.List;

public interface ICartDetailService {
    void getAllCartDetail(String email);
    List<CartDetailResponse> getAllCartDetailInactive();
    List<CartDetailResponse> getAllCartDetailActived();
}
