package com.redhorse.deokhugam.infra.ocr;

import org.springframework.web.multipart.MultipartFile;

public interface OcrClient
{
    String extractText(MultipartFile image);
}
