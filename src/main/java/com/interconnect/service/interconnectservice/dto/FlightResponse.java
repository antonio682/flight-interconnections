package com.interconnect.service.interconnectservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FlightResponse {

    private String stops;
    List<LegResponse> legs;

}
