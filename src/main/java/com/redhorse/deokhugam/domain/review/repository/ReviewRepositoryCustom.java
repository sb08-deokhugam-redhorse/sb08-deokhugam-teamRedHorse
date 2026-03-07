package com.redhorse.deokhugam.domain.review.repository;

import com.redhorse.deokhugam.domain.review.dto.ReviewSearchRequest;
import com.redhorse.deokhugam.domain.review.entity.Review;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {

  Slice<Review> getAllReviews(ReviewSearchRequest request);

  long getTotal(ReviewSearchRequest request);
}
