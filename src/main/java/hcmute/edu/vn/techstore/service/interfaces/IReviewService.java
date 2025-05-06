package hcmute.edu.vn.techstore.service.interfaces;

import hcmute.edu.vn.techstore.dto.request.ReviewRequest;
import hcmute.edu.vn.techstore.entity.UserEntity;

public interface IReviewService {
    void submitReview(ReviewRequest request, UserEntity user);
}
