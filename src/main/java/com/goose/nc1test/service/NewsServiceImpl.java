package com.goose.nc1test.service;

import com.goose.nc1test.config.MapStructMapper;
import com.goose.nc1test.dto.NewsDTO;
import com.goose.nc1test.model.News;
import com.goose.nc1test.repository.NewsRepository;
import com.goose.nc1test.specification.NewsSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository repository;
    private final MapStructMapper mapper;

    @Override
    public NewsDTO create(NewsDTO dto) {
        if (Objects.isNull(dto.getHeadline())
                || Objects.isNull(dto.getDescription())
                || Objects.isNull(dto.getTime())
                || existsByHeadline(dto.getHeadline())) {
            return null;
        }

        NewsDTO result = mapper.toDTO(repository.save(mapper.toModel(dto)));
        log.info("News saved: {}", result);
        return result;
    }

    @Override
    public boolean existsByHeadline(String headline) {
        boolean result = repository.existsByHeadline(headline);
        log.info("By headline = {} exists = {}", headline, result);
        return result;
    }

    @Override
    public Page<NewsDTO> searchAll(NewsDTO dto, Pageable pageable) {
        Page<News> newsPage = repository.findAll(NewsSpecification.getSpecification(dto), pageable);
        log.info("News find by specifications = {}, {}: {}", dto, pageable, newsPage.get().toList());
        return new PageImpl<>(newsPage.get().map(mapper::toDTO).toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NewsDTO findById(Long id) {
        News byId = repository.findById(id).orElse(null);
        log.info("News find by id: {}", byId);
        return mapper.toDTO(byId);
    }

    @Override
    public NewsDTO update(NewsDTO dto) {
        NewsDTO updateNews = findById(dto.getId());

        if (Objects.nonNull(dto.getHeadline())
                && !dto.getHeadline().equals(updateNews.getHeadline())) {
            updateNews.setHeadline(dto.getHeadline());
        }
        if (Objects.nonNull(dto.getDescription())
                && !dto.getDescription().equals(updateNews.getDescription())) {
            updateNews.setDescription(dto.getDescription());
        }
        if (Objects.nonNull(dto.getTime())
                && !dto.getTime().equals(updateNews.getTime())) {
            updateNews.setTime(dto.getTime());
        }
        log.info("News to update: {}", updateNews);
        return updateNews;
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
        log.info("Cleanup end");
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) return;

        repository.deleteById(id);
        log.info("News by id = {} is exist: {}", id, repository.existsById(id));
    }
}
