package team.router.recycle.domain.station;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DaejeonStationClient {

    private final RestClient client;
    private final String DAEJEON_API_KEY;

    public DaejeonStationClient(RestClient client, @Value("${client.daejeon.key}") String daejeonApiKey) {
        DAEJEON_API_KEY = daejeonApiKey;
        String BASE_URL = "https://bikeapp.tashu.or.kr:50041/v1/openapi/station";
        this.client = client
                .mutate()
                .baseUrl(BASE_URL)
                .defaultHeader("api-token", DAEJEON_API_KEY)
                .build();
    }

    public String makeRequest() {
        return client.get()
                .retrieve()
                .body(String.class);
    }
}
