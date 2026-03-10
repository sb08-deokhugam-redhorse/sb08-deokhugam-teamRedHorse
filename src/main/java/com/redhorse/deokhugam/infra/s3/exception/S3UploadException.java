package com.redhorse.deokhugam.infra.s3.exception;

import com.redhorse.deokhugam.global.exception.ApiException;
import com.redhorse.deokhugam.global.exception.ErrorCode;

import java.util.Map;

public class S3UploadException extends ApiException
{
    public S3UploadException(Throwable cause) {
        super(ErrorCode.S3_UPLOAD_FAIL, Map.of(), cause);
    }
}
