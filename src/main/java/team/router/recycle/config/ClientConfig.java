package team.router.recycle.config;

import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder();
    }
}
