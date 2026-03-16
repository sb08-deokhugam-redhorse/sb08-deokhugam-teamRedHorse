package com.redhorse.deokhugam.infra.ocr;

public record OcrResult(
        String text,
        OcrSource source
) {}
