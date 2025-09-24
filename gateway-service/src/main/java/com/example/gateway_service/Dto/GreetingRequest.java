package com.example.gateway_service.Dto;

public class GreetingRequest {
    private String name;

    public GreetingRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
