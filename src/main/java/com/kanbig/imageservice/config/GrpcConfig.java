package com.kanbig.imageservice.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kanbig.imageservice.KanbigImageServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class GrpcConfig {
//	@Value("${grpc.host:localhost}")
	@Value("${grpc.host:192.168.1.20}")
	private String grpcHost;
	@Value("${grpc.port:50051}")
	private int grpcPort;
	@Value("${grpc.core.thread:3}")
	private int grpcCoreThread;
	@Value("${grpc.max.thread:10}")
	private int grpcMaxThread;
	@Value("${grpc.queue.size:100}")
	private int grpcQueueSize;
	@Autowired
	private ManagedChannel channel;

	@Bean
	public ManagedChannel grpcChannel() {
		/**
		 * idleTimeout 空闲超时 enableRetry 开启重试，当服务端重启后，客户端可以重连上
		 */
		return ManagedChannelBuilder.forAddress(grpcHost, grpcPort).usePlaintext().idleTimeout(5, TimeUnit.SECONDS)
				.enableRetry()
				.executor(new ThreadPoolExecutor(grpcCoreThread, grpcMaxThread, 60L, TimeUnit.SECONDS,
						new ArrayBlockingQueue<Runnable>(grpcQueueSize), new ThreadPoolExecutor.CallerRunsPolicy()))
				.build();
	}

	@PreDestroy
	public void shutdown() {
		try {
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
//			log.info("ManagedChannel ------ shutdown success");
		} catch (Exception e) {
//			log.warn("ManagedChannel ------ shutdown fail");
		}
	}

	@Bean
	public KanbigImageServiceGrpc.KanbigImageServiceBlockingStub kanbigImageServiceBlockingStub() {
		return KanbigImageServiceGrpc.newBlockingStub(channel);
	}
}
