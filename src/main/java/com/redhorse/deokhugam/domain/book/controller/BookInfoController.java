package com.redhorse.deokhugam.domain.book.controller;

import com.redhorse.deokhugam.domain.book.controller.api.BookInfoApi;
import com.redhorse.deokhugam.infra.naver.NaverBookProvider;
import com.redhorse.deokhugam.infra.naver.dto.NaverBookDto;
import com.redhorse.deokhugam.infra.ocr.OcrProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/books")
@RestController
public class BookInfoController implements BookInfoApi
{
    private final NaverBookProvider naverBookProvider;
    private final OcrProvider ocrProvider;

    @GetMapping("/info")
    public ResponseEntity<NaverBookDto> getBookInfo(@RequestParam String isbn) {
        log.debug("[BookInfo-Controller] 도서 정보 조회 요청 시작: isbn={}", isbn);

        NaverBookDto bookInfo = naverBookProvider.getBookInfoByIsbn(isbn);

        return ResponseEntity.status(HttpStatus.OK).body(bookInfo);
    }

    @PostMapping(value = "/isbn/ocr", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> getIsbnFromOcr(@RequestParam("image") MultipartFile image) {
        log.debug("[BookInfo-Controller] ISBN 인식 요청 시작: fileName={}", image.getOriginalFilename());

        List<String> isbnList = ocrProvider.extractIsbn(image);

        return ResponseEntity.status(HttpStatus.OK).body(isbnList);
    }
}
