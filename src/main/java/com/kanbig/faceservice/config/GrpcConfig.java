package com.kanbig.faceservice.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kanbig.faceservice.KanbigImageServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class GrpcConfig {

	@Value("${grpc.host:work.kanbig.com}")
	private String grpcHost;
	@Value("${grpc.host:50051}")
	private int grpcPort;

	@Autowired
	private ManagedChannel channel;

	@Bean
	public ManagedChannel grpcChannel() {
		/**
		 * idleTimeout 空闲超时 enableRetry 开启重试，当服务端重启后，客户端可以重连上
		 */
		return ManagedChannelBuilder.forAddress(grpcHost, grpcPort).usePlaintext().idleTimeout(3, TimeUnit.SECONDS)
				.enableRetry()
				.executor(new ThreadPoolExecutor(1, 3, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100),
						new ThreadPoolExecutor.CallerRunsPolicy()))
				.build();
	}

	@PreDestroy
	public void shutdown() {
		try {
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
			log.info("ManagedChannel ------ shutdown success");
		} catch (Exception e) {
			log.warn("ManagedChannel ------ shutdown fail");
		}
	}

	@Bean
	public KanbigImageServiceGrpc.KanbigImageServiceBlockingStub kanbigImageServiceBlockingStub() {
		return KanbigImageServiceGrpc.newBlockingStub(channel);
	}
}
