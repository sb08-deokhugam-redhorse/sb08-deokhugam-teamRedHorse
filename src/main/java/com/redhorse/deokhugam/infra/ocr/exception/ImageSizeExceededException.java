package com.redhorse.deokhugam.infra.ocr.exception;

import com.redhorse.deokhugam.global.exception.ApiException;
import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;

public class ImageSizeExceededException extends ApiException
{
    public ImageSizeExceededException(long imageSize) {
        super(ErrorCode.IMAGE_SIZE_EXCEEDED, Map.of("imageSize: ", imageSize));
    }
}
