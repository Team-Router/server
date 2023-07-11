package team.router.recycle.web.route;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import team.router.recycle.domain.route.model.Distance;
import team.router.recycle.domain.route.model.Duration;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.domain.route.model.RoutingProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetDirectionResponseDeserializer extends JsonDeserializer<GetDirectionResponse> {
    @Override
    public GetDirectionResponse deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        List<Location> locations = new ArrayList<>();
        for (JsonNode coordinate : node.get("geometry").get("coordinates")) {
            locations.add(new Location(coordinate.get(1).asDouble(),
                    coordinate.get(0).asDouble()));
        }
        return new GetDirectionResponse(
                RoutingProfile.valueOf(node.get("weight_name").asText()),
                new Duration(node.get("duration").asInt()),
                new Distance(node.get("distance").asInt()),
                locations);
    }
}