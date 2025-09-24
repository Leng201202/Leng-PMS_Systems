package com.example.gateway_service.Controllers;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    @GrpcClient("greeting-service")
    private GreetingServiceGrpc.GreetingServiceBlockingStub greetingServiceBlockingStub;

    @GetMapping("/greet")
    public String greet(@RequestParam(name = "name", defaultValue = "World") String name) {
        GreetingRequest greetingRequest = GreetingRequest.newBuilder()
                .setName(name)
                .build();
        GreetingResponse response = greetingServiceBlockingStub.greet(greetingRequest);
        return response.getMessage();
    }
}