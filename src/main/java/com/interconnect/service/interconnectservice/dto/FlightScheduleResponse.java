package com.interconnect.service.interconnectservice.dto;

import lombok.Data;

@Data
public class FlightScheduleResponse {

    private String number;
    private String departureTime;
    private String arrivalTime;
}
