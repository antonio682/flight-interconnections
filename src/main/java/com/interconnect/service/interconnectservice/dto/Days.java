package com.interconnect.service.interconnectservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Days {

    private Integer day;
    List<FlightScheduleResponse> flights;
}
