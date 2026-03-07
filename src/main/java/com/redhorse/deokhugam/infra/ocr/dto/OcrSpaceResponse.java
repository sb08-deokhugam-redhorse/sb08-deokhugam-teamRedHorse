package com.redhorse.deokhugam.infra.ocr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OcrSpaceResponse(
        @JsonProperty("ParsedResults")List<ParseResult> parsedResults,
        @JsonProperty("OCRExitCode") int ocrExitCode,
        @JsonProperty("IsErroredOnProcessing") boolean isErroredOnProcessing
) {
    public record ParseResult(
            @JsonProperty("ParsedText") String parsedText,
            @JsonProperty("FileParseExitCode") int fileParseExitCode
    ) {}
}
