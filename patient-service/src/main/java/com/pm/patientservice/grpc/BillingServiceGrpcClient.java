package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import billing.BillingServiceGrpc;

@Service
public class BillingServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9091}") int serverPort){

        log.info("Connecting to billing service GRPC at {} {}",serverAddress,serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress,serverPort).usePlaintext().build();
        this.blockingStub=BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount( String patientId , String name , String email){
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();
        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("Received response from billing service via grpc {}",response);
        return  response;
    }
}

