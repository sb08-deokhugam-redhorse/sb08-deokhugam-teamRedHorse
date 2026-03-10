package com.redhorse.deokhugam.infra.naver;

import com.redhorse.deokhugam.infra.naver.dto.NaverBookResponse.NaverBookItem;

import java.util.Optional;

public interface NaverBookClient
{
    Optional<NaverBookItem> fetchInfoByIsbn(String isbn);
}
