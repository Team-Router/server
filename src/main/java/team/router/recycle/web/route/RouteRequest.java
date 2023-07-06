package team.router.recycle.web.route;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import team.router.recycle.domain.route.model.Location;

public class RouteRequest {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetDirectionRequest {
        private Location startLocation;
        private Location endLocation;
    }
}