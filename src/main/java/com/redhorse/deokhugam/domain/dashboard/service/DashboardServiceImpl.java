package com.redhorse.deokhugam.domain.dashboard.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final PopularReviewRepository reviewRepository;
    private final PowerUserRepository userRepository;
    private final PopularBookRepository bookRepository;
    private final DashboardMapper dashboardMapper;

    @Override
    @Cacheable(value = "popularReviews", key = "#request") // DTO가 record 타입이면 DTO자체를 키로 설정 가능
    public CursorPageResponsePopularReviewkDto getPopularReviews(DashboardRequest request) {

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.direction())
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, request.limit() + 1, sort);

        Slice<PopularReview> slice = reviewRepository.getAllPopularReview(request, pageable);

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
    @Cacheable(value = "powerUsers", key = "#request")
    public CursorPageResponsePowerUserDto getPowerUsers(DashboardRequest request) {

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.direction())
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, request.limit() + 1, sort);

        Slice<PowerUser> slice = userRepository.getAllPowerUser(request, pageable);

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
    @Cacheable(value = "popularBooks", key = "#request")
    public CursorPageResponsePopularBookDto getPopularBooks(DashboardRequest request) {

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.direction())
                ? Sort.Direction.ASC : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));

        Pageable pageable = PageRequest.of(0, request.limit() + 1, sort);

        Slice<PopularBook> slice = bookRepository.getAllPopularBook(request, pageable);

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

    // CacheEvict로 배치에서 캐시만 비울려고 만들었습니다.
    @CacheEvict(value = "popularReviews", allEntries = true)
    @Override
    public void clearReviewDashboardCache() {}

    @CacheEvict(value = "powerUsers", allEntries = true)
    @Override
    public void clearUserDashboardCache() {}

    @CacheEvict(value = "popularBooks", allEntries = true)
    @Override
    public void clearBooksDashboardCache() {}
}
