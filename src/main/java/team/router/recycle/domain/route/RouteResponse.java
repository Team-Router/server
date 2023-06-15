package team.router.recycle.domain.route;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class RouteResponse {

    @Getter
    @Setter
    public static class getDirectionResponse {
        private RoutingProfile routingProfile;
        private Duration duration;
        private Distance distance;
        private List<Location> locations = new ArrayList<>();

        @JsonGetter("duration")
        public int getDuration() {
            return duration.getSeconds();
        }

        @JsonGetter("distance")
        public int getDistance() {
            return distance.getMeters();
        }
    }
}
