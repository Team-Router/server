package team.router.recycle.domain.route;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RouteClient {
    final String GEOJSON = "?geometries=geojson";
    final String ACCESS = "&access_token=";
    private final WebClient client;
    private final String MAPBOX_API_KEY;

    public RouteClient(WebClient client, @Value("${client.mapbox.key}") String MAPBOX_API_KEY) {
        this.client = client.mutate().baseUrl("https://api.mapbox.com/directions/v5/mapbox/")
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build()
                )
                .build();
        this.MAPBOX_API_KEY = MAPBOX_API_KEY;
    }

    public String getRouteInfo(String profile, String coordinate) {
        return client.get()
                .uri(profile + coordinate + GEOJSON + ACCESS + MAPBOX_API_KEY)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
