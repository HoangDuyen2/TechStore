package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.convert.CartDetailConverter;
import hcmute.edu.vn.techstore.dto.response.CartDetailResponse;
import hcmute.edu.vn.techstore.dto.response.CartResponse;
import hcmute.edu.vn.techstore.dto.response.ProductResponse;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import hcmute.edu.vn.techstore.repository.CartDetailRepository;
import hcmute.edu.vn.techstore.service.interfaces.ICartDetailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CartDetailServiceImpl implements ICartDetailService {
    private final CartDetailRepository cartDetailRepository;
    private final CartDetailConverter cartDetailConverter;
    @Override
    public List<CartDetailResponse> getAllCartDetail(String email) {
        List<CartDetailEntity> cartDetailEntities = cartDetailRepository.findAllByCart_User_Account_Email(email);
        return cartDetailEntities.stream().map(cartDetailConverter::toCartDetailResponse).toList();
    }

    public BigDecimal getTotalPrice(CartResponse cartResponse) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<CartDetailResponse> cartDetailResponses = cartResponse.getCartDetails();
        for (CartDetailResponse cartDetailResponse : cartDetailResponses) {
            ProductResponse productResponse = cartDetailResponse.getProduct();
            if (productResponse.isActived()){
                BigDecimal productItem = cartDetailResponse.getProduct().getPrice();
                int quantity = cartDetailResponse.getQuantity();
                totalPrice = totalPrice.add(productItem.multiply(new BigDecimal(quantity)));
            }
        }
        return totalPrice;
    }
}
