package com.interconnect.service.interconnectservice.service;

import com.interconnect.service.interconnectservice.dto.FlightResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface InterconnectionsService {

    List<FlightResponse> obtainFlights(final String departure, final String arrival, final LocalDateTime departureDateTime, final LocalDateTime arrivalDateTime);
}
