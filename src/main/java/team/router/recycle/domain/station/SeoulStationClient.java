package team.router.recycle.domain.station;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SeoulStationClient {

    private final RestClient client;
    private final String SEOUL_API_KEY;
    private static final String BIKE_PATH = "/json/bikeList";

    public SeoulStationClient(RestClient client, @Value("${client.seoul.key}") String seoulApiKey) {
        SEOUL_API_KEY = seoulApiKey;
        String BASE_URL = "http://openapi.seoul.go.kr:8088";
        this.client = client
                .mutate()
                .baseUrl(BASE_URL)
                .build();
    }

    public String makeRequest(String target) {
        String requestUri = SEOUL_API_KEY + BIKE_PATH + target;
        return client.get()
                .uri(requestUri)
                .retrieve()
                .body(String.class);
    }
}
