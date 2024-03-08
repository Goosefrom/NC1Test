package com.goose.nc1test.config;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public enum PeriodOfDay {
    MORNING(LocalTime.of(5, 0, 1), LocalTime.of(12, 0)),
    DAY(LocalTime.of(12, 0, 1), LocalTime.of(17, 0)),
    EVENING(LocalTime.of(17, 0, 1), LocalTime.of(21, 0)),
    NIGHT(LocalTime.of(21, 0, 1), LocalTime.of(5, 0));

    private final LocalTime timeFrom;
    private final LocalTime timeTo;

    PeriodOfDay(LocalTime timeFrom, LocalTime timeTo) {
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }
}
