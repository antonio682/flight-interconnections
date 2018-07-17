package com.interconnect.service.interconnectservice.service.impl;

import com.interconnect.service.interconnectservice.dto.ScheduleResponse;
import com.interconnect.service.interconnectservice.service.SchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class SchedulesServiceImpl implements SchedulesService {

    @Value("${routes.service.scheme}")
    private String scheme;

    @Value("${routes.service.host}")
    private String hostRyanair;

    @Value("${schedules.service.path}")
    private String schedulesPath;

    private RestTemplate restTemplate;

    @Autowired
    public SchedulesServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ScheduleResponse getSchedules(final String departure, final String arrival,
                                               final Integer year, final Integer month) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("departure", departure);
        parameters.put("arrival", arrival);
        parameters.put("year", year.toString());
        parameters.put("month", month.toString());

        URI routesUri = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(hostRyanair)
                .path(schedulesPath)
                .build(parameters);

        return restTemplate.exchange(routesUri, HttpMethod.GET, new HttpEntity<>(HttpEntity.EMPTY),
                new ParameterizedTypeReference<ScheduleResponse>() {
                }).getBody();
    }
}
