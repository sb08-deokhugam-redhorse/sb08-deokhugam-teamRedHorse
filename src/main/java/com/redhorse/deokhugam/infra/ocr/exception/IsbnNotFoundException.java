package com.redhorse.deokhugam.infra.ocr.exception;

import com.redhorse.deokhugam.global.exception.ApiException;
import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;

public class IsbnNotFoundException extends ApiException
{
    public IsbnNotFoundException() {
        super(ErrorCode.ISBN_NOT_FOUND, Map.of());
    }
}
