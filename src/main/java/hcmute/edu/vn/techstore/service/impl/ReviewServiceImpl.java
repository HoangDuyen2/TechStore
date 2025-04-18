package hcmute.edu.vn.techstore.service.impl;

import hcmute.edu.vn.techstore.repository.ReviewRepository;
import hcmute.edu.vn.techstore.service.interfaces.IReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {
    private final ReviewRepository reviewRepository;
}
