package com.redhorse.deokhugam.domain.dashboard.repository;

import com.redhorse.deokhugam.domain.dashboard.entity.PopularBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PopularBookRepository extends JpaRepository<PopularBook, UUID> {
}
