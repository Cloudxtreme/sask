package org.dice_research.sask.taipan_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * This class is responsible for launching the microservice.
 * 
 * @author André Sonntag
 *
 */
@SpringBootApplication
//@EnableDiscoveryClient
public class TaipanMsApplication {
	public static void main(String[] args) {
		SpringApplication.run(TaipanMsApplication.class, args);
	}
}
