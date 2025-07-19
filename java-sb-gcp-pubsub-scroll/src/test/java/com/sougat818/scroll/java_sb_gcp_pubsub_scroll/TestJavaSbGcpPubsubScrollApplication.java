package com.sougat818.scroll.java_sb_gcp_pubsub_scroll;

import org.springframework.boot.SpringApplication;

public class TestJavaSbGcpPubsubScrollApplication {

	public static void main(String[] args) {
		SpringApplication.from(JavaSbGcpPubsubScrollApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
