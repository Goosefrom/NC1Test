package com.goose.nc1test.dto;

import com.goose.nc1test.config.PeriodOfDay;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/** data transfer object class**/
@Getter
@Setter
@NoArgsConstructor
public class NewsDTO {
    private Long id;
    private String headline;
    private String description;
    private LocalTime time;
    private PeriodOfDay period;
}
