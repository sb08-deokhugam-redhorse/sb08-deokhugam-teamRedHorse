package com.redhorse.deokhugam.domain.dashboard.mapper;

import com.redhorse.deokhugam.domain.dashboard.dto.popularbook.PopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.PopularReviewDto;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DashboardMapper {
    // 1. 연관 관계 객체(Review, Book, User) 안으로 파고들어야 하는 필드들
    @Mapping(source = "review.id", target = "reviewId")
    @Mapping(source = "review.book.id", target = "bookId")
    @Mapping(source = "review.book.title", target = "bookTitle")
    @Mapping(source = "review.book.thumbnailUrl", target = "bookThumbnailUrl")
    @Mapping(source = "review.user.id", target = "userId")
    @Mapping(source = "review.user.nickname", target = "userNickname")
    @Mapping(source = "review.rating", target = "reviewRating")

    // 2. 이름이 미묘하게 다른 필드
    @Mapping(source = "ranking", target = "rank")

    // 3. (중요) 엔티티에 존재하지 않는 필드 처리
    // 이전 Review 엔티티 코드 기준으로 'reviewCount'가 없었으므로 임의의 값(0L)을 넣거나 무시해야 에러가 안 납니다.
    @Mapping(target = "reviewCount", constant = "0L")
    PopularReviewDto entityToReivewDto(PopularReview review);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.nickname", target = "nickname")
    @Mapping(source = "ranking", target = "rank")
    PowerUserDto entityToPowerUserDto(PowerUser powerUser);

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "title")
    @Mapping(source = "book.thumbnailUrl", target = "thumbnailUrl")
    @Mapping(source = "ranking", target = "rank")
    PopularBookDto entityToBookDto(PopularBook popularBook);
}
