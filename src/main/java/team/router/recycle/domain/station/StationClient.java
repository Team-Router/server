package team.router.recycle.domain.station;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StationClient {

    private final WebClient client;
    private final String SEOUL_API_KEY;

    public StationClient(WebClient client, @Value("${client.seoul.key}") String seoulApiKey) {
        SEOUL_API_KEY = seoulApiKey;
        String BASE_URL = "http://openapi.seoul.go.kr:8088";
        this.client = client
                .mutate()
                .baseUrl(BASE_URL)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build()
                )
                .build();
    }
    public String makeRequest(String target) {
        String BIKE_PATH = "/json/bikeList";
        String requestUri = SEOUL_API_KEY + BIKE_PATH + target;
        return client.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
