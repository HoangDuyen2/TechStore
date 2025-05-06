package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.dto.request.ReviewRequest;
import hcmute.edu.vn.techstore.entity.OrderEntity;
import hcmute.edu.vn.techstore.entity.ProductEntity;
import hcmute.edu.vn.techstore.entity.ReviewEntity;
import hcmute.edu.vn.techstore.entity.UserEntity;
import hcmute.edu.vn.techstore.repository.OrderRepository;
import hcmute.edu.vn.techstore.repository.ProductRepository;
import hcmute.edu.vn.techstore.repository.ReviewRepository;
import hcmute.edu.vn.techstore.service.interfaces.IReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    @Override
    @Transactional
    public void submitReview(ReviewRequest request, UserEntity user) {
        boolean exists = reviewRepository.existsByUserIdAndProductIdAndOrderId(
                user.getId(), request.getProductId(), request.getOrderId());

        if (exists) {
            throw new IllegalStateException("You have already reviewed this product in this order.");
        }

        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        OrderEntity order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        ReviewEntity review = new ReviewEntity();
        review.setUser(user);
        review.setProduct(product);
        review.setOrder(order);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        reviewRepository.save(review);

        long oldCount = product.getNumberOfReviews() == null ? 0 : product.getNumberOfReviews();
        int oldStar = product.getStar() == null ? 0 : product.getStar();
        int newTotalRating = oldStar * (int) oldCount + request.getRating();
        long newCount = oldCount + 1;
        int newAvgStar = Math.round((float) newTotalRating / newCount);

        product.setNumberOfReviews(newCount);
        product.setStar(newAvgStar);
        productRepository.save(product);

    }
}
