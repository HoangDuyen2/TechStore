package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.entity.DiscountEntity;
import hcmute.edu.vn.techstore.model.request.DiscountRequest;
import hcmute.edu.vn.techstore.model.response.DiscountResponse;
import hcmute.edu.vn.techstore.repository.DiscountRepository;
import hcmute.edu.vn.techstore.repository.OrderRepository;
import hcmute.edu.vn.techstore.service.IDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DiscountServiceImpl implements IDiscountService {
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Page<DiscountResponse> findAll(Pageable pageable) {
        return discountRepository.findAll(pageable).map(discount -> DiscountResponse.builder()
                .id(discount.getId())
                .name(discount.getName())
                .code(discount.getCode())
                .discountPercent(discount.getDiscountPercent())
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
                    .discountPercent(discount.getDiscountPercent())
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
        discount.setDiscountPercent(discountRequest.getDiscountPercent());
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
            discount.setDiscountPercent(discountRequest.getDiscountPercent());
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
