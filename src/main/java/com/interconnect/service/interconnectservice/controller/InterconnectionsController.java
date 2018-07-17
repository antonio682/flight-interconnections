package com.interconnect.service.interconnectservice.controller;

import com.google.common.base.Preconditions;
import com.interconnect.service.interconnectservice.dto.FlightResponse;
import com.interconnect.service.interconnectservice.exceptions.InterconnectException;
import com.interconnect.service.interconnectservice.service.InterconnectionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/interconnections")
public class InterconnectionsController {


    private InterconnectionsService interconnectionsService;

    @Autowired
    public InterconnectionsController(InterconnectionsService interconnectionsService) {
        this.interconnectionsService = interconnectionsService;
    }

    @GetMapping
    public ResponseEntity<List<FlightResponse>> getFlights(@Valid @RequestParam final String departure,
                                                           @Valid @RequestParam final String arrival,
                                                           @Valid @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  final LocalDateTime departureDateTime,
                                                           @Valid @RequestParam  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  final LocalDateTime arrivalDateTime) {
        Preconditions.checkArgument(departureDateTime.isAfter(LocalDateTime.now()),"Departure date time must be after now");
        Preconditions.checkArgument(departureDateTime.isBefore(arrivalDateTime), "Departure date time must be before Arrival date time");
        throw new InterconnectException("No obtained routes");
//        List<FlightResponse> flights = interconnectionsService.obtainFlights(departure, arrival, departureDateTime, arrivalDateTime);
//        return new ResponseEntity<>(flights, HttpStatus.ACCEPTED);
    }
}
