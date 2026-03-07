package com.redhorse.deokhugam.infra.ocr.exception;

import com.redhorse.deokhugam.global.exception.ApiException;
import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;

public class OcrProcessingException extends ApiException
{
    public OcrProcessingException() {
        super(ErrorCode.OCR_PROCESSING_FAILED, Map.of());
    }
}
