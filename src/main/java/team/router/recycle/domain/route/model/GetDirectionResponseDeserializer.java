package team.router.recycle.domain.route.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetDirectionResponseDeserializer extends JsonDeserializer<RouteResponse.getDirectionResponse> {
    @Override
    public RouteResponse.getDirectionResponse deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        RouteResponse.getDirectionResponse response = new RouteResponse.getDirectionResponse();
        response.setRoutingProfile(RoutingProfile.valueOf(node.get("weight_name").asText()));
        response.setDistance(new Distance(node.get("distance").asInt()));
        response.setDuration(new Duration(node.get("duration").asInt()));
        List<Location> locations = new ArrayList<>();
        for (JsonNode coordinate : node.get("geometry").get("coordinates")) {
            locations.add(new Location(coordinate.get(1).asDouble(),
                    coordinate.get(0).asDouble()));
        }
        response.setLocations(locations);
        return response;
    }
}