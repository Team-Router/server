package team.router.recycle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ReCycleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReCycleApplication.class, args);
    }

}
