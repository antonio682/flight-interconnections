package com.interconnect.service.interconnectservice.service;

import com.interconnect.service.interconnectservice.dto.ScheduleResponse;
import org.springframework.stereotype.Service;

@Service
public interface SchedulesService {

    ScheduleResponse getSchedules(final String departure, final String arrival, final Integer year, final Integer month);
}
