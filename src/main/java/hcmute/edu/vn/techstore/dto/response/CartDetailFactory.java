package hcmute.edu.vn.techstore.dto.response;

import hcmute.edu.vn.techstore.convert.CartDetailConverter;
import hcmute.edu.vn.techstore.entity.CartDetailEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartDetailFactory {

    private final CartDetailConverter cartDetailConverter;

    public CartDetailFactory(CartDetailConverter cartDetailConverter) {
        this.cartDetailConverter = cartDetailConverter;
    }

    // Phương thức factory để tạo List<CartDetailResponse> từ List<CartDetailEntity>
    public List<CartDetailResponse> createCartDetailResponse(List<CartDetailEntity> cartDetailEntities) {
        return cartDetailEntities.stream()
                .map(cartDetailConverter::toCartDetailResponse)
                .collect(Collectors.toList());
    }
}