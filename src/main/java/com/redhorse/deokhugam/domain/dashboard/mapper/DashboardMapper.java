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
    @Mapping(source = "review.id", target = "reviewId")
    @Mapping(source = "review.book.id", target = "bookId")
    @Mapping(source = "review.book.title", target = "bookTitle")
    @Mapping(source = "review.book.thumbnailUrl", target = "bookThumbnailUrl")
    @Mapping(source = "review.user.id", target = "userId")
    @Mapping(source = "review.user.nickname", target = "userNickname")
    @Mapping(source = "review.rating", target = "reviewRating")
    @Mapping(source = "ranking", target = "rank")
    @Mapping(target = "reviewCount", constant = "0L")
    PopularReviewDto entityToReviewDto(PopularReview review);

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
