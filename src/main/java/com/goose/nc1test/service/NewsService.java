package com.goose.nc1test.service;

import com.goose.nc1test.dto.NewsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewsService {
    NewsDTO create(NewsDTO dto);

    boolean existsByHeadline(String headline);

    Page<NewsDTO> searchAll(NewsDTO dto, Pageable pageable);

    NewsDTO findById(Long id);

    NewsDTO update(NewsDTO dto);

    void delete(Long id);

    void deleteAll();
}
