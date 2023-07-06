package team.router.recycle.web.route;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import team.router.recycle.domain.route.model.*;

import java.util.ArrayList;
import java.util.List;

public class RouteResponse {

    @Getter
    @Setter
    @JsonDeserialize(using = GetDirectionResponseDeserializer.class)
    public static class getDirectionResponse {
        private RoutingProfile routingProfile;
        private Duration duration;
        private Distance distance;
        private List<Location> locations = new ArrayList<>();

        @JsonGetter("duration")
        public int getDuration() {
            return duration.seconds();
        }

        @JsonGetter("distance")
        public int getDistance() {
            return distance.meters();
        }
    }
}