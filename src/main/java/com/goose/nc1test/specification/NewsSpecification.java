package com.goose.nc1test.specification;

import com.goose.nc1test.config.PeriodOfDay;
import com.goose.nc1test.dto.NewsDTO;
import com.goose.nc1test.model.News;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;

/** class for searching logic **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsSpecification {

    public static Specification<News> getSpecification(NewsDTO dto) {
        return Specification.where(likeHeadline(dto.getHeadline()))
                .and(likeDescription(dto.getDescription()))
                .and(greaterOrEqualThanPublicationTime(dto.getTime()))
                .and(equalDayPeriod(dto.getPeriod()));
    }

    private static Specification<News> likeHeadline(String headline) {
        if (headline == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("headline"), "%" + headline + "%");
    }

    private static Specification<News> likeDescription(String description) {
        if (description == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"), "%" + description + "%");
    }

    private static Specification<News> greaterOrEqualThanPublicationTime(LocalTime time) {
        if (time == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("time"), time);
    }

    private static Specification<News> equalDayPeriod(PeriodOfDay period) {
        if (period == null) return null;

        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("time"), period.getTimeFrom(), period.getTimeTo());
    }
}
