package com.interconnect.service.interconnectservice.dto;

import lombok.Data;

@Data
public class RouteResponse {

    private String airportFrom;
    private String airportTo;
    private String connectingAirport;
    private boolean newRoute;
    private boolean seasonalRoute;
    private String group;

}
