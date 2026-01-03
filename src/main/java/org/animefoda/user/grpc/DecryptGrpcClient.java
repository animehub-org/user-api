package org.animefoda.user.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.animefoda.grpc.auth.DecryptRequest;
import org.animefoda.grpc.auth.DecryptResponse;
import org.animefoda.grpc.auth.DecryptServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DecryptGrpcClient {

    @Value("${grpc.client.auth-server.host:localhost}")
    private String host;

    @Value("${grpc.client.auth-server.port:9090}")
    private int port;

    private ManagedChannel channel;
    private DecryptServiceGrpc.DecryptServiceBlockingStub decryptStub;

    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        decryptStub = DecryptServiceGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null) {
            channel.shutdown();
        }
    }

    public String decrypt(String encryptedData) throws Exception {
        DecryptRequest request = DecryptRequest.newBuilder()
                .setEncryptedData(encryptedData)
                .build();

        DecryptResponse response = decryptStub.decrypt(request);

        if (response.getSuccess()) {
            return response.getDecryptedData();
        } else {
            throw new Exception("Decryption failed: " + response.getErrorMessage());
        }
    }
}
