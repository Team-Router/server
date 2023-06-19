package team.router.recycle.domain.route;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class RouteRequest {
    @Getter
    @Setter
    @NoArgsConstructor
    public static class GetDirectionRequest {
        private Location startLocation;
        private Location endLocation;
    }
}
