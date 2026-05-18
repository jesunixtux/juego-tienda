package cl.duoc.videojuegos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class VideojuegosApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideojuegosApplication.class, args);
	}

}
