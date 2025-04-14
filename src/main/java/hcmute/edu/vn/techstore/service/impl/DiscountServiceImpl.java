package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.Enum.EDiscountType;
import hcmute.edu.vn.techstore.entity.DiscountEntity;
import hcmute.edu.vn.techstore.dto.request.DiscountRequest;
import hcmute.edu.vn.techstore.dto.response.DiscountResponse;
import hcmute.edu.vn.techstore.repository.DiscountRepository;
import hcmute.edu.vn.techstore.repository.OrderRepository;
import hcmute.edu.vn.techstore.service.interfaces.IDiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements IDiscountService {
    private final DiscountRepository discountRepository;
    private final OrderRepository orderRepository;

    @Override
    public Page<DiscountResponse> findAll(Pageable pageable) {
        return discountRepository.findAll(pageable).map(discount -> DiscountResponse.builder()
                .id(discount.getId())
                .name(discount.getName())
                .code(discount.getCode())
                .type(discount.getDiscountType().name())
                .amount(discount.getAmount())
                .expiredDate(discount.getExpiredDate())
                .quantity(discount.getQuantity())
                .usedQuantity(orderRepository.countAllByDiscount_Code(discount.getCode()))
                .build());
    }

    @Override
    public DiscountEntity findByName(String name) {
        return discountRepository.findByName(name);
    }

    @Override
    public DiscountEntity findByCode(String code) {
        return discountRepository.findByCode(code);
    }

    @Override
    public DiscountRequest findByIdRequest(Long id) {
        DiscountEntity discount = discountRepository.findById(id).orElse(null);
        if (discount != null) {
            return DiscountRequest.builder()
                    .name(discount.getName())
                    .code(discount.getCode())
                    .type(discount.getDiscountType().name())
                    .amount(discount.getAmount())
                    .expiredDate(discount.getExpiredDate())
                    .quantity(discount.getQuantity())
                    .build();
        }
        return null;
    }

    @Override
    public boolean addDiscount(DiscountRequest discountRequest) {
        DiscountEntity discount = new DiscountEntity();
        discount.setName(discountRequest.getName());
        discount.setCode(discountRequest.getCode());
        discount.setDiscountType(EDiscountType.valueOf(discountRequest.getType()));
        discount.setAmount(discountRequest.getAmount());
        discount.setExpiredDate(discountRequest.getExpiredDate());
        discount.setQuantity(discountRequest.getQuantity());
        discountRepository.save(discount);
        return true;
    }

    @Override
    public boolean updateDiscount(Long id, DiscountRequest discountRequest) {
        DiscountEntity discount = discountRepository.findById(id).orElse(null);
        if (discount != null) {
            discount.setName(discountRequest.getName());
            discount.setCode(discountRequest.getCode());
            discount.setDiscountType(EDiscountType.valueOf(discountRequest.getType()));
            discount.setAmount(discountRequest.getAmount());
            discount.setExpiredDate(discountRequest.getExpiredDate());
            discount.setQuantity(discountRequest.getQuantity());
            discountRepository.save(discount);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDiscount(Long id) {
        try {
            discountRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
