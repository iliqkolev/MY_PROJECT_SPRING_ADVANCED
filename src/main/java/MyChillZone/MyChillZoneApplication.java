package MyChillZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
//@EnableCaching
@EnableAsync
public class MyChillZoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyChillZoneApplication.class, args);
	}

}
