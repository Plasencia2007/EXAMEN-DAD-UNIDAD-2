package com.plasencia.ms_seguridad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsSeguridadApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsSeguridadApplication.class, args);
	}

}
