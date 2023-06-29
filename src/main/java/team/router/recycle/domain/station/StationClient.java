package team.router.recycle.domain.station;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StationClient {
    private final WebClient client;
    private final String SEOUL_API_KEY;


    public StationClient(WebClient.Builder builder, @Value("${SEOUL_API_KEY}") String SEOUL_API_KEY) {
        this.client = builder.baseUrl("http://openapi.seoul.go.kr:8088/")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build()
                ).build();
        this.SEOUL_API_KEY = SEOUL_API_KEY;
    }

    public String getStationInfo(String target) {
        return client.get()
                .uri(SEOUL_API_KEY + "/json/tbCycleStationInfo" + target)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
