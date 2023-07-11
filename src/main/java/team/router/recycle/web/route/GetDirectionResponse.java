package team.router.recycle.web.route;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import team.router.recycle.domain.route.model.Distance;
import team.router.recycle.domain.route.model.Duration;
import team.router.recycle.domain.route.model.Location;
import team.router.recycle.domain.route.model.RoutingProfile;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GetDirectionResponse(
        RoutingProfile routingProfile,
        Duration duration,
        Distance distance,
        List<Location> locations
) {
    @JsonGetter("duration")
    public int getDuration() {
        return duration.seconds();
    }

    @JsonGetter("distance")
    public int getDistance() {
        return distance.meters();
    }
}