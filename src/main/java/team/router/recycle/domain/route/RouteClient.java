package team.router.recycle.domain.route;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RouteClient {
    private static final String GEOJSON = "?geometries=geojson";
    private static final String ACCESS = "&access_token=";
    private final RestClient client;
    private final String MAPBOX_API_KEY;

    public RouteClient(RestClient client, @Value("${client.mapbox.key}") String MAPBOX_API_KEY) {
        this.client = client.mutate().baseUrl("https://api.mapbox.com/directions/v5/mapbox/")
                .build();
        this.MAPBOX_API_KEY = MAPBOX_API_KEY;
    }

    public String getRouteInfo(String profile, String coordinate) {
        return client.get()
                .uri(profile + coordinate + GEOJSON + ACCESS + MAPBOX_API_KEY)
                .retrieve()
                .body(String.class);
    }
}
