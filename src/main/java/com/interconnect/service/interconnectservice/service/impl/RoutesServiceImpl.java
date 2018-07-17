package com.interconnect.service.interconnectservice.service.impl;

import com.interconnect.service.interconnectservice.dto.RouteResponse;
import com.interconnect.service.interconnectservice.service.RoutesService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@Service
public class RoutesServiceImpl implements RoutesService {

    @Value("${routes.service.scheme}")
    private String schemeRoutesService;

    @Value("${routes.service.host}")
    private String routesHost;

    @Value("${routes.service.resource.path}")
    private String routesResourcePath;

    private RestTemplate restTemplate;

    @Autowired
    public RoutesServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "obtainRoutesFallback")
    @Override
    public List<RouteResponse> obtainRoutes()  {

        URI routesUri = UriComponentsBuilder.newInstance()
                .scheme(schemeRoutesService)
                .host(routesHost)
                .path(routesResourcePath)
                .build()
                .toUri();
        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return restTemplate.exchange(routesUri, HttpMethod.GET, new HttpEntity<>(HttpEntity.EMPTY),
                new ParameterizedTypeReference<List<RouteResponse>>() {
                }).getBody();
    }

    public List<RouteResponse> obtainRoutesFallback() {
        return Collections.emptyList();
    }
}
