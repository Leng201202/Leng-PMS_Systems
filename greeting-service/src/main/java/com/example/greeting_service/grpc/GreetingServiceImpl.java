package com.example.greeting_service.grpc;

import com.example.greeting_service.GreetingServiceGrpc;
import com.example.greeting_service.GreetingRequest;
import com.example.greeting_service.GreetingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.kafka.core.KafkaTemplate;

@GrpcService
public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public GreetingServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void greet(GreetingRequest request, StreamObserver<GreetingResponse> responseObserver) {
        kafkaTemplate.send("greetings", "hello from greeting-service: " + request.getName());
        GreetingResponse response = GreetingResponse.newBuilder()
                .setMessage("Hello, " + request.getName() + "!")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}