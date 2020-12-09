package com.kanbig.faceservice.config;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kanbig.faceservice.KanbigImageServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Configuration
public class GrpcConfig {

	@Value("${grpc.host:work.kanbig.com}")
	private String grpcHost;
	@Value("${grpc.host:50051}")
	private int grpcPort;

	@Bean
	public ManagedChannel grpcChannel() {
		return ManagedChannelBuilder.forAddress(grpcHost, grpcPort).usePlaintext()
				.idleTimeout(3, TimeUnit.SECONDS)
				.build();
	}

	@Bean
	public KanbigImageServiceGrpc.KanbigImageServiceBlockingStub kanbigImageServiceBlockingStub(
			ManagedChannel channel) {
		return KanbigImageServiceGrpc.newBlockingStub(channel);
	}
}
