package team.router.recycle.domain.station;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DaejeonStationClient {
    
    private final WebClient client;
    private final String DAEJEON_API_KEY;
    
    public DaejeonStationClient(WebClient client, @Value("${client.daejeon.key}") String daejeonApiKey) {
        DAEJEON_API_KEY = daejeonApiKey;
        String BASE_URL = "https://bikeapp.tashu.or.kr:50041/v1/openapi/station";
        this.client = client
                .mutate()
                .baseUrl(BASE_URL)
                .defaultHeader("api-token", DAEJEON_API_KEY)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build()
                )
                .build();
    }
    
    public String makeRequest() {
        return client.get()
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
