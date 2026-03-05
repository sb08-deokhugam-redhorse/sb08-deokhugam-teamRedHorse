package com.redhorse.deokhugam.domain.alarm.controller;

import com.redhorse.deokhugam.domain.alarm.controller.docs.AlarmApi;
import com.redhorse.deokhugam.domain.alarm.dto.NotificationDto;
import com.redhorse.deokhugam.domain.alarm.service.AlarmService;
import com.redhorse.deokhugam.domain.comment.dto.CommentDto;
import com.redhorse.deokhugam.domain.comment.repository.CommentRepository;
import com.redhorse.deokhugam.domain.review.dto.ReviewLikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notification")
public class AlarmController implements AlarmApi {
    private final AlarmService alarmService;
    private final CommentRepository commentRepository;

//    테스트용  ----------------------
//    스웨그 api나 요구사항을 보면 알림에서 필요한 api는
//    업데이트와 조회 뿐입니다. 업데이트와 조회는 별도의 일정으로 빼놔서
//    알림 등록에서 컨트롤러로 확인 하실건 없습니다.

    Instant now = Instant.now();
    CommentDto test = new CommentDto(
            UUID.fromString("50000000-0000-0000-0000-000000000001"), // id
            UUID.fromString("30000000-0000-0000-0000-000000000001"), // reviewId
            UUID.fromString("10000000-0000-0000-0000-000000000002"), // userId
            "테스트유저2", // userNickName (임시)
            "공감합니다. 좋은 리뷰 감사해요!", // content
            now, // createdAt
            now  // updatedAt
    );

    ReviewLikeDto test2=new ReviewLikeDto(
            UUID.fromString("30000000-0000-0000-0000-000000000001"),
            UUID.fromString("10000000-0000-0000-0000-000000000002"),
            true
    );

    @GetMapping("/test")
    public ResponseEntity<NotificationDto> createAlarm(){
//        NotificationDto notificationDto = alarmService.createCommentAlarm(test);
        NotificationDto notificationDto = alarmService.createLikeAlarm(test2);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationDto);
    }
//    ________________________________________________________-
}
