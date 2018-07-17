package com.interconnect.service.interconnectservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interconnect.service.interconnectservice.dto.FlightResponse;
import com.interconnect.service.interconnectservice.dto.RouteResponse;
import com.interconnect.service.interconnectservice.dto.ScheduleResponse;
import com.interconnect.service.interconnectservice.service.RoutesService;
import com.interconnect.service.interconnectservice.service.SchedulesService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


public class InterconnectionsServiceImplTest {

    private ObjectMapper objectMapper;

    @InjectMocks
    private InterconnectionsServiceImpl interconnectionsService;

    @Mock
    private RoutesService routesService;
    @Mock
    private SchedulesService schedulesService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void should_obtain_flights() throws IOException {
        // GIVEN
        List<RouteResponse> routes = Arrays.asList(obtainRoute());
        BDDMockito.given(routesService.obtainRoutes()).willReturn(routes);
        BDDMockito.given(schedulesService.getSchedules(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).willReturn(obtainSchuleResponse());
        // WHEN
        List<FlightResponse> response = interconnectionsService.obtainFlights("LUZ", "STN", LocalDateTime.now(), LocalDateTime.now().plusMonths(1));
        // THEN
        Assert.assertTrue("Response must not be empty", !response.isEmpty());
        Assert.assertTrue("Only direct flights allowed", response.get(0).getLegs().size() == 1);
        Assert.assertTrue("Stops must be zero", response.get(0).getStops().equalsIgnoreCase("0"));
    }

    private RouteResponse obtainRoute() throws IOException {

        String jsonString = "{\n" +
                "        \"airportFrom\": \"LUZ\",\n" +
                "        \"airportTo\": \"STN\",\n" +
                "        \"connectingAirport\": null,\n" +
                "        \"newRoute\": false,\n" +
                "        \"seasonalRoute\": false,\n" +
                "        \"group\": \"Generic\"\n" +
                "    }";

        return objectMapper.readValue(jsonString, RouteResponse.class);

    }

    private ScheduleResponse obtainSchuleResponse() throws IOException {
        String jsonString = "{\n" +
                "    \"month\": 7,\n" +
                "    \"days\": [\n" +
                "        {\n" +
                "            \"day\": 14,\n" +
                "            \"flights\": [\n" +
                "                {\n" +
                "                    \"number\": \"7257\",\n" +
                "                    \"departureTime\": \"10:35\",\n" +
                "                    \"arrivalTime\": \"12:20\"\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"day\": 15,\n" +
                "            \"flights\": [\n" +
                "                {\n" +
                "                    \"number\": \"7257\",\n" +
                "                    \"departureTime\": \"10:35\",\n" +
                "                    \"arrivalTime\": \"12:20\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        return objectMapper.readValue(jsonString, ScheduleResponse.class);

    }
}