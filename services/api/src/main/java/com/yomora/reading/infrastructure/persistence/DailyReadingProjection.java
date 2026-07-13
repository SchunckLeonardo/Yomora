package com.yomora.reading.infrastructure.persistence;

import java.time.LocalDate;

interface DailyReadingProjection {
    LocalDate getReadingDate();

    Long getMinutes();

    Long getPages();
}
