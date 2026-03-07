package com.redhorse.deokhugam.infra.ocr;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OcrClient
{
    String extractText(MultipartFile image) throws IOException;
}
