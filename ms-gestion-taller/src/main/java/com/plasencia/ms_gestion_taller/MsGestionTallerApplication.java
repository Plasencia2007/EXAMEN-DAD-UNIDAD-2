package com.plasencia.ms_gestion_taller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsGestionTallerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGestionTallerApplication.class, args);
	}

}
