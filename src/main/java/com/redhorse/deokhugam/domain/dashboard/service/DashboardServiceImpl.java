package com.redhorse.deokhugam.domain.dashboard.service;

import com.redhorse.deokhugam.domain.alarm.dto.CursorPageResponseNotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationListRequest;
import com.redhorse.deokhugam.domain.alarm.entity.Alarm;
import com.redhorse.deokhugam.domain.alarm.exception.AlarmNotFoundException;
import com.redhorse.deokhugam.domain.alarm.exception.NoAlarmException;
import com.redhorse.deokhugam.domain.alarm.mapper.AlarmMapper;
import com.redhorse.deokhugam.domain.alarm.repository.AlarmRepository;
import com.redhorse.deokhugam.domain.book.repository.BookRepository;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularbook.PopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.popularreview.PopularReviewDto;
import com.redhorse.deokhugam.domain.dashboard.dto.poweruser.PowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.dto.request.DashboardRequest;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularBookDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePopularReviewkDto;
import com.redhorse.deokhugam.domain.dashboard.dto.response.CursorPageResponsePowerUserDto;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import com.redhorse.deokhugam.domain.dashboard.entity.PopularReview;
import com.redhorse.deokhugam.domain.dashboard.entity.PowerUser;
import com.redhorse.deokhugam.domain.dashboard.mapper.DashboardMapper;
import com.redhorse.deokhugam.domain.dashboard.repository.PopularBookRepository;
import com.redhorse.deokhugam.domain.dashboard.repository.PopularReviewRepository;
import com.redhorse.deokhugam.domain.dashboard.repository.PowerUserRepository;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import com.redhorse.deokhugam.domain.review.entity.Review;
import com.redhorse.deokhugam.domain.review.repository.ReviewRepository;
import com.redhorse.deokhugam.domain.user.entity.User;
import com.redhorse.deokhugam.domain.user.exception.UserNotFoundException;
import com.redhorse.deokhugam.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final PopularReviewRepository reviewRepository;
    private final PowerUserRepository userRepository;
    private final PopularBookRepository bookRepository;
    private final DashboardMapper dashboardMapper;

    @Override
    public CursorPageResponsePopularReviewkDto getPopularReviews(DashboardRequest request) {

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.direction())
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, request.limit() + 1, sort);

        Slice<PopularReview> slice = "ASC".equalsIgnoreCase(request.direction())
                ? reviewRepository.getAllPopularReviewAsc(request, pageable)
                : reviewRepository.getAllPopularReviewDesc(request, pageable);

        List<PopularReview> objectList = slice.getContent();
        Long objectCount = reviewRepository.count();

        String nextCursor = null;
        Instant nextAfter = null;
        boolean hasNext = objectList.size() > request.limit();

        if (hasNext) {
            PopularReview last = objectList.get(request.limit() - 1);
            nextCursor = last.getId().toString();
            nextAfter = last.getCreatedAt();
            objectList = objectList.subList(0, request.limit());
        }

        List<PopularReviewDto> content = objectList.stream()
                .map(dashboardMapper::entityToReviewDto)
                .toList();


        return new CursorPageResponsePopularReviewkDto(
                content,
                nextCursor,
                nextAfter,
                request.limit(),
                objectCount,
                hasNext
        );
    }

    @Override
    public CursorPageResponsePowerUserDto getPowerUsers(DashboardRequest request) {

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.direction())
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, request.limit() + 1, sort);

        Slice<PowerUser> slice = "ASC".equalsIgnoreCase(request.direction())
                ? userRepository.getAllPowerUserAsc(request, pageable)
                : userRepository.getAllPowerUserDesc(request, pageable);

        List<PowerUser> objectList = slice.getContent();
        Long objectCount = userRepository.count();

        String nextCursor = null;
        Instant nextAfter = null;
        boolean hasNext = objectList.size() > request.limit();

        if (hasNext) {
            PowerUser last = objectList.get(request.limit() - 1);
            nextCursor = last.getId().toString();
            nextAfter = last.getCreatedAt();
            objectList = objectList.subList(0, request.limit());
        }

        List<PowerUserDto> content = objectList.stream()
                .map(dashboardMapper::entityToPowerUserDto)
                .toList();

        return new CursorPageResponsePowerUserDto(
                content,
                nextCursor,
                nextAfter,
                request.limit(),
                objectCount,
                hasNext
        );
    }

    @Override
    public CursorPageResponsePopularBookDto getPopularBooks(DashboardRequest request) {

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.direction())
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, request.limit() + 1, sort);

        Slice<PopularBook> slice = "ASC".equalsIgnoreCase(request.direction())
                ? bookRepository.getAllPopularBookAsc(request, pageable)
                : bookRepository.getAllPopularBookDesc(request, pageable);

        List<PopularBook> objectList = slice.getContent();
        Long objectCount = bookRepository.count();

        String nextCursor = null;
        Instant nextAfter = null;
        boolean hasNext = objectList.size() > request.limit();

        if (hasNext) {
            PopularBook last = objectList.get(request.limit() - 1);
            nextCursor = last.getId().toString();
            nextAfter = last.getCreatedAt();
            objectList = objectList.subList(0, request.limit());
        }

        List<PopularBookDto> content = objectList.stream()
                .map(dashboardMapper::entityToBookDto)
                .toList();


        return new CursorPageResponsePopularBookDto(
                content,
                nextCursor,
                nextAfter,
                request.limit(),
                objectCount,
                hasNext
        );
    }
}
