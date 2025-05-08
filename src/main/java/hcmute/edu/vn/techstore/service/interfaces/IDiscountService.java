package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.entity.DiscountEntity;
import hcmute.edu.vn.techstore.dto.request.DiscountRequest;
import hcmute.edu.vn.techstore.dto.response.DiscountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDiscountService {
    Page<DiscountResponse> findAll(Pageable pageable);

    DiscountEntity findByName(String name);

    DiscountEntity findByCode(String code);

    DiscountRequest findByIdRequest(Long id);

    boolean addDiscount(DiscountRequest discountRequest);

    boolean updateDiscount(Long id, DiscountRequest discountRequest);

    boolean deleteDiscount(Long id);

    boolean decreaseQuantity(String code, int quantity);

    boolean checkDiscount(String code);
}
