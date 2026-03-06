package com.redhorse.deokhugam.domain.book.exception;

import com.redhorse.deokhugam.global.exception.ErrorCode;
import org.springframework.web.client.RestClientException;

import java.util.Map;

public class NaverApiException extends BookException
{
    public NaverApiException(RestClientException e) {
        super(ErrorCode.NAVER_API_ERROR, Map.of("message", e.getMessage()));
    }
}
