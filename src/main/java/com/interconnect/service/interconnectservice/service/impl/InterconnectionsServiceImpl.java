package com.interconnect.service.interconnectservice.service.impl;

import com.interconnect.service.interconnectservice.dto.*;
import com.interconnect.service.interconnectservice.exceptions.InterconnectException;
import com.interconnect.service.interconnectservice.service.InterconnectionsService;
import com.interconnect.service.interconnectservice.service.RoutesService;
import com.interconnect.service.interconnectservice.service.SchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InterconnectionsServiceImpl implements InterconnectionsService {

    private RoutesService routesService;
    private SchedulesService schedulesService;

    @Autowired
    public InterconnectionsServiceImpl(RoutesService routesService, SchedulesService schedulesService) {
        this.routesService = routesService;
        this.schedulesService = schedulesService;
    }

    @Override
    public List<FlightResponse> obtainFlights(String departure, String arrival, LocalDateTime departureDatetime, LocalDateTime arrivalDateTime)  {

        List<RouteResponse> obtainedRoutes = routesService.obtainRoutes();

        if (!obtainedRoutes.isEmpty()) {

            RouteResponse route = obtainRoute(departure, arrival, obtainedRoutes);
            List<FlightResponse> directFlights = findFlightsFromSchedules(departureDatetime, arrivalDateTime, departure, arrival);

            List<FlightResponse> flightsWIthConnection = new ArrayList<>();

            if (route.getConnectingAirport() != null) {

               flightsWIthConnection =  obtainConnectingFlights(departure, arrival, departureDatetime, arrivalDateTime, route);
               flightsWIthConnection.forEach(flight -> flight.setStops("1"));

            }
            List<FlightResponse> response = new ArrayList<>();
            response.addAll(directFlights);
            response.addAll(flightsWIthConnection);
            return response;

        } else {
            throw new InterconnectException("No obtained routes");
        }
    }

    private  List<FlightResponse> obtainConnectingFlights(String departure, String arrival, LocalDateTime departureDatetime, LocalDateTime arrivalDateTime, RouteResponse route) {
        List<FlightResponse> firstLegs = findFlightsFromSchedules(departureDatetime, arrivalDateTime, departure, route.getConnectingAirport());
        List<FlightResponse> response = new ArrayList<>();

        firstLegs.forEach(firstLeg -> {

           LegResponse secondLegToAdd = findFlightsFromSchedules(departureDatetime, arrivalDateTime, route.getConnectingAirport(), arrival).stream()
                    .filter(secondLeg -> isAfterTwoHours(firstLeg, secondLeg))
                    .findFirst().orElseThrow(() -> new InterconnectException("Error setting second leg")).getLegs().get(0);
            firstLeg.getLegs().add(secondLegToAdd);
            response.add(firstLeg);
        });

        return response;
    }

    private boolean isAfterTwoHours(FlightResponse firstLeg, FlightResponse secondLeg) {
        return secondLeg.getLegs().get(0).getDepartureTime().getHour() > firstLeg.getLegs().get(0).getArrivalDateTime().plusHours(2).getHour();
    }

    private List<FlightResponse> findFlightsFromSchedules(LocalDateTime departureDatetime, LocalDateTime arrivalDateTime, String departure, String arrival) {

        Map<LocalDateTime, List<ScheduleResponse>> scheduleByYear = new HashMap<>();
        LocalDateTime currentDate = departureDatetime;

        while (currentDate.isBefore(arrivalDateTime) || currentDate.isEqual(arrivalDateTime)) {
            List<ScheduleResponse> totalResponses = new ArrayList<>();
            List<ScheduleResponse> previousSchedules = (List<ScheduleResponse>) scheduleByYear.get(currentDate);
            ScheduleResponse currentSchehdulesRequest = schedulesService.getSchedules(departure, arrival, currentDate.getYear(), currentDate.getMonth().getValue());

            if (previousSchedules != null)
                totalResponses.addAll(previousSchedules);

            if (currentSchehdulesRequest != null)
                totalResponses.add(currentSchehdulesRequest);

            scheduleByYear.put(currentDate, totalResponses);
            currentDate = currentDate.plusMonths(1L);
        }

        List<FlightResponse> response = new ArrayList<>();

        scheduleByYear.forEach(
                (key, value) -> value.forEach(schedule -> {
                    response.addAll(
                            mapDays(
                                    departure, arrival, schedule, schedule.getMonth(), key.getMonth().getValue()).stream()
                                    .filter(days -> isBeforeLastValidArrivalDateTime(arrivalDateTime, days))
                                    .collect(Collectors.toList()
                                    )
                    );
                })
        );

        return response;
    }

    private boolean isBeforeLastValidArrivalDateTime(LocalDateTime arrivalDateTime, FlightResponse days) {
        return days.getLegs().stream().reduce((first, second) -> second)
                .orElse(null).getArrivalDateTime().isBefore(arrivalDateTime);
    }

    private List<FlightResponse> mapDays(String departure, String arrival, ScheduleResponse directDepartureRoutes, int month, int year) {
        return directDepartureRoutes.getDays().stream()
                .map(days -> {
                            return FlightResponse.builder()
                                    .stops("0")
                                    .legs(obtainLegs(days, departure, arrival, days.getDay(), year, month))
                                    .build();
                        }
                )
                .collect(Collectors.toList());
    }


    private List<LegResponse> obtainLegs(Days days, String departure, String arrival, Integer currentDay, Integer year, Integer month) {


        return days.getFlights().stream()
                .map(day -> {
                    String[] hourAndMinuteDeparture = day.getDepartureTime().split(":");
                    String[] hourAndMinuteArrival = day.getArrivalTime().split(":");
                    return LegResponse.builder()
                            .departureAirport(departure)
                            .arrivalAirport(arrival)
                            .departureTime(LocalDateTime.of(year, Month.of(month), currentDay, Integer.valueOf(hourAndMinuteDeparture[0]), Integer.valueOf(hourAndMinuteDeparture[1])))
                            .arrivalDateTime(LocalDateTime.of(year, Month.of(month), currentDay, Integer.valueOf(hourAndMinuteArrival[0]), Integer.valueOf(hourAndMinuteArrival[1]), 0))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private RouteResponse obtainRoute(String departure, String arrival, List<RouteResponse> obtainedRoutes) {
        return obtainedRoutes.stream()
                .filter(routeResponse -> isDirectFlight(departure, arrival, routeResponse)).findAny().orElseThrow(null);
    }

    private boolean isDirectFlight(String departure, String arrival, RouteResponse route) {
        return route.getAirportFrom().equalsIgnoreCase(departure) && route.getAirportTo().equalsIgnoreCase(arrival);
    }
}
