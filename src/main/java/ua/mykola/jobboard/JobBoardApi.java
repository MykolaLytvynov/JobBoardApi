package ua.mykola.jobboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobBoardApi {

	public static void main(String[] args) {
		SpringApplication.run(JobBoardApi.class, args);
	}

}
