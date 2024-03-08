package com.goose.nc1test.config;

import com.goose.nc1test.dto.NewsDTO;
import com.goose.nc1test.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class MapStructMapper {

    @Mapping(target = "period", expression = "java(news.getTime().isAfter(PeriodOfDay.DAY.getTimeFrom()) ? (news.getTime().isAfter(PeriodOfDay.EVENING.getTimeFrom()) ? (news.getTime().isAfter(PeriodOfDay.NIGHT.getTimeFrom()) ? PeriodOfDay.NIGHT : PeriodOfDay.EVENING) : PeriodOfDay.DAY) : news.getTime().isBefore(PeriodOfDay.NIGHT.getTimeTo()) ? PeriodOfDay.NIGHT : PeriodOfDay.MORNING)")
    public abstract NewsDTO toDTO(News news);

    public abstract News toModel(NewsDTO dto);
}
