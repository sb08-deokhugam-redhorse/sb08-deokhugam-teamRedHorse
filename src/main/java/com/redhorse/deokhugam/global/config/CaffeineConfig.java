package com.redhorse.deokhugam.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

// 나중에 Redis 전환 시 직렬화 문제 인지
@Configuration
@EnableCaching
public class CaffeineConfig
{
    // LRU (Least Recently Used)
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(List.of(
                new CaffeineCache("book",
                        Caffeine.newBuilder()
                                .expireAfterWrite(1, TimeUnit.HOURS)
                                .maximumSize(1000)
                                .recordStats()
                                .build()),
                new CaffeineCache("naverBook",
                        Caffeine.newBuilder()
                                .expireAfterWrite(24, TimeUnit.HOURS)
                                .maximumSize(500)
                                .recordStats()
                                .build())
        ));

        return cacheManager;
    }
}
